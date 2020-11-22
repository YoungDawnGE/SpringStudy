package com.gyc.mvcframework.servlet;

import com.gyc.mvcframework.annonation.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * Created by GYC
 * 2020/11/12 20:23
 */
public class GYCDispatcherServlet extends HttpServlet {
    private Properties contextConfig = new Properties();
    private List<String> classNames = new ArrayList<>();

    //IOC容器
    private Map<String, Object> IoC = new HashMap<>();

    //url->方法映射的handlerMapping容器
    private Map<String, Method> handlerMapping = new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //运行阶段
        //6、委派(根据url 拿到 method)
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("500 The Server Error. Details " + Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        //初始化阶段
        //1、加载配置文件
        doLoadConfig(config.getInitParameter("contextConfigLocation"));//这个contextConfigLocation与web.xml里面的一致
        //2、扫描相关的类
        doScanner(contextConfig.getProperty("scanPackage"));
        //3、实例化相关的类，并缓存到IoC容器
        doInstance();
        //4、完成依赖注入
        doAutowired();
        //5、初始化HandlerMapping,用于url到controller方法的映射
        doInitHandlerMapping();
        //初始化阶段完成
        System.out.println("GYC SpringFramework 初始化完成");
    }


    private void doLoadConfig(String contextConfigLocation) {
        System.out.println("1: in doLoadConfig");
        try (InputStream resource = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation.substring(contextConfigLocation.indexOf(":") + 1))) {
            contextConfig.load(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void doScanner(String packageName) {
        System.out.println("2: in doScanner");
        getClassName(packageName);
    }

    private void getClassName(String packageName) {
        //        /com/gyc/demo
        URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));
        File classPath = new File(url.getFile());
        //扫描文件夹下面的所有文件
        for (File subFile : classPath.listFiles()) {
            if (subFile.isDirectory()) {
                getClassName(packageName + "." + subFile.getName());
            } else {
                //全类名
                if (!subFile.getName().endsWith(".class")) {
                    continue;
                }
                String className = packageName + "." + subFile.getName().replaceAll(".class", "");
                //对上面有注解的类进行操作
                classNames.add(className);
            }
        }
    }

    private void doInstance() {
        System.out.println("3: in doInstance");

        if (classNames.isEmpty()) return;

        try {
            for (String className : classNames) {
                //一个被实例化的对象对应多个beanName,如下
                Class<?> clazz = Class.forName(className);
                //哪些类需要实例化？
                //  没加注解的不用实例化
                //  interface不能实例化
                //  加了Controller的和Service的需要实例化
                //  Controller只要类名首字母小写，但是Service的情况就比较复杂
                if (clazz.isAnnotationPresent(GYCController.class)) {
                    Object instance = clazz.newInstance();
                    //1、beanName(key)是类名首字母小写
                    String beanName = toLowerFirst(clazz.getSimpleName());
                    IoC.put(beanName, instance);
                } else if (clazz.isAnnotationPresent(GYCService.class)) {
                    Object instance = clazz.newInstance();
                    //1、beanName(key)是类名首字母小写
                    String beanName = toLowerFirst(clazz.getSimpleName());
                    IoC.put(beanName, instance);
                    //2、在不同包下的类，如果类相同了，自定义beanName,如@GYCService("StudentService")
                    GYCService annotation = clazz.getAnnotation(GYCService.class);
                    if (null!=annotation && !"".equals(annotation.value())) {
                        beanName = annotation.value();
                        IoC.put(beanName, instance);
                    }
                    //3、在@GYCAutowired的时候，往往是用的IService接口，
                    // 所以beanName也可以是接口的名字，这里我们放入接口的全类名
                    // 但是会存在一个接口被多个类实现的情况，这个我们先抛出异常
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> face : interfaces) {
                        if (IoC.containsKey(face.getName())) {
                            throw new Exception("异常: 接口"+face.getName()+"被多个子类实现");
                        } else {
                            IoC.put(face.getName(), instance);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //首字母小写
    private static String toLowerFirst(String s) {
        char[] chars = s.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    private void doAutowired() {
        System.out.println("4: in doAutowired");
        if (IoC.isEmpty()) return;
        //对于每个IOC容器的对象，查看它的每一个Field，
        // 如果Field上有@GYCAutowired的注解，则进行赋值（依赖注入）
        for (Map.Entry<String, Object> entry : IoC.entrySet()) {
            //拿到所有的Field,包括private public protected default
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(GYCAutowired.class)) continue;
                //赋值
                GYCAutowired autowired = field.getAnnotation(GYCAutowired.class);
                //取GYCAutowired的值如果有的话
                String beanName = autowired.value();
                //如果GYCAutowired的值为空，就用Field字段的全类名
                if ("".equals(beanName)) {
                    beanName = field.getType().getName();
                }
                //比如：给private StudentService studentService这个声明赋IoC之前初始化的值
                field.setAccessible(true);//暴力破解
                try {
                    field.set(entry.getValue(), IoC.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //这一步是将url与controller的方法对应起来  HashMap
    private void doInitHandlerMapping() {
        System.out.println("5: in doInitHandlerMapping");
        for (Map.Entry<String, Object> entry : IoC.entrySet()) {
            Class<?> claz = entry.getValue().getClass();
            if (!claz.isAnnotationPresent(GYCController.class)) continue;

            //拿到Controller上面对应的GYCRequestMapping里面的值作为prefix,
            // 也可能Controller上没有RequestMapping
            GYCRequestMapping requestMapping = claz.getAnnotation(GYCRequestMapping.class);
            String prefix = "";
            if (null != requestMapping) {
                prefix = requestMapping.value();
            }
            /*//或者上面可以这样写isAnnotationPresent
            if (claz.isAnnotationPresent(GYCRequestMapping.class)) {
                prefix = claz.getAnnotation(GYCRequestMapping.class).value();
            }*/

            //把GYCController中的每个带GYCRequestMapping方法，进行url->方法的映射
            for (Method method : claz.getDeclaredMethods()) {
                if (!method.isAnnotationPresent(GYCRequestMapping.class)) continue;
                String suffix = method.getAnnotation(GYCRequestMapping.class).value();
                String url = ("/" + prefix + "/" + suffix + "/").replaceAll("/+", "/");
                handlerMapping.put(url, method);
                System.out.println("Mapped:" + url + "->" + method);
            }
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        System.out.println("6: in doDispatch");
        /*
        System.out.println(req.getContextPath());// /SpringStudy
        System.out.println(req.getRequestURI());//  /SpringStudy/stu/query
        System.out.println(req.getRequestURL());//  http://localhost:8080/SpringStudy/stu/query
         */
        String url = req.getRequestURI() + "/";
        url = url.replaceAll(req.getContextPath(), "").replaceAll("/+", "/");

        if (!handlerMapping.containsKey(url)) {
            resp.getWriter().write("404 The resource doesn't exist");
            return;
        }

        //根据url获得对应的方法method
        Method method = handlerMapping.get(url);
        String controllerName = method.getDeclaringClass().getSimpleName();
        controllerName = toLowerFirst(controllerName);

        //req传来的参数
        Map<String, String[]> parameterMap = req.getParameterMap();

        //获得形参列表
        Class<?>[] parameterTypes = method.getParameterTypes();
        //定义实参列表
        Object[] parameterValues = new Object[parameterTypes.length];
        //根据形参列表给实参赋值

        //动态获取参数
        for (int i = 0; i < parameterTypes.length; i++) {
            if (parameterTypes[i] == HttpServletRequest.class) {
                parameterValues[i] = req;
            } else if (parameterTypes[i] == HttpServletResponse.class) {
                parameterValues[i] = resp;
            } else if (parameterTypes[i] == String.class) {
                //拿到形参的注解,每个参数对应多个注解，有多个参数，所以对应二维数组
                Annotation[][] pas = method.getParameterAnnotations();
                for (Annotation a : pas[i]) {
                    String paramName = ((GYCRequestParam) a).value();
                    if (!"".equals(paramName)) {
                        String value = Arrays.toString(parameterMap.get(paramName))
                                .replaceAll("\\[|\\]", "")
                                .replaceAll("\\s", "");
                        parameterValues[i] = value;
                    }
                }
            }
        }
        method.invoke(IoC.get(controllerName), parameterValues);
    }
}
