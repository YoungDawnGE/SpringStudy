package com.gyc.mvcframework.servlet;

import com.gyc.mvcframework.annonation.*;
import com.gyc.mvcframework.context.GYCApplicationContext;

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
    //url->方法映射的handlerMapping容器
    private Map<String, Method> handlerMapping = new HashMap<>();

    //GYCSpring2.0
    private GYCApplicationContext applicationContext;

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

//        //---------------IOC---------------
//        //1、加载配置文件
//        doLoadConfig(config.getInitParameter("contextConfigLocation"));//这个contextConfigLocation与web.xml里面的一致
//        //2、扫描相关的类
//        doScanner(contextConfig.getProperty("scanPackage"));
//        //3、实例化相关的类，并缓存到IoC容器
//        doInstance();
//        //---------------DI---------------
//        //4、完成依赖注入
//        doAutowired();

        //以上由GYCApplicationContext一步完成
        applicationContext = new GYCApplicationContext(config.getInitParameter("contextConfigLocation"));

        //---------------MVC---------------
        //5、初始化HandlerMapping,用于url到controller方法的映射
        doInitHandlerMapping();
        //初始化阶段完成
        System.out.println("============GYC Servlet 初始化完成============");
    }


    //首字母小写
    private static String toLowerFirst(String s) {
        char[] chars = s.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    //这一步是将url与controller的方法对应起来  HashMap
    private void doInitHandlerMapping() {
        if (applicationContext.getBeanDefinitionCount() == 0) {
            return;
        }
        String[] beanNames = applicationContext.getBeanDefinitionNames();

        for (String beanName:beanNames) {
            Object instance = applicationContext.getBean(beanName);

            Class<?> claz = instance.getClass();
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
        method.invoke(applicationContext.getBean(controllerName), parameterValues);
    }
}
