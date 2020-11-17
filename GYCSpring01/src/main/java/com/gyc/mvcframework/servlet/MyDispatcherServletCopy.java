package com.gyc.mvcframework.servlet;

import com.gyc.mvcframework.annonation.GYCAutowired;
import com.gyc.mvcframework.annonation.GYCController;
import com.gyc.mvcframework.annonation.GYCRequestMapping;
import com.gyc.mvcframework.annonation.GYCService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * Created by GYC
 * 2020/6/17 15:37
 */
public class MyDispatcherServletCopy extends HttpServlet {

    private Properties contextConfig = new Properties();
    //用个容器存全类名
    private List<String> clazzNames = new ArrayList<>();
    //IoC容器用于放实例化的对象
    private Map<String, Object> ioc = new HashMap<>();
    //映射url
    private Map<String, Method> handlerMapping = new HashMap<>();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("进入了doGet");
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //运行阶段
        try {
            doDispatch(req, resp);
        } catch (IOException e) {
            e.printStackTrace();
            resp.getWriter().write("GYC 500 Exception. Details " + Arrays.toString(e.getStackTrace()));
        }
    }

    //运行阶段
    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //把绝对路径替换成相对路径
        String url = req.getRequestURI();//拿到绝对路径 浏览器传过来的
        System.out.println(url);
        String contextPath = req.getContextPath();//拿到相对路径
        url = url.replaceAll(contextPath, "").replaceAll("/+", "/");//绝对路径换成相对路径

        System.out.println(url);

        if (!handlerMapping.containsKey(url)) {
            resp.getWriter().write("GYC 404 NOT FOUND");
            return;
        }

        Method method = handlerMapping.get(url);

        //服务器获得来自客户端传来的request请求中的参数
        Map<String, String[]> params = req.getParameterMap();
        String beanName = toLowerFirst(method.getDeclaringClass().getSimpleName());//controller存的都是类名首字母小写
        try {
            //controller默认单例
            method.invoke(ioc.get(beanName), req, resp, params.get("id")[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        //初始化阶段
        //1、加载配置文件 ServletConfig获取的是web.xml文件中配置init-params参数 “contextConfigLocation”，它的值为classpath:application.properties
        doLoadConfig(servletConfig.getInitParameter("contextConfigLocation"));//classpath:application.properties

        //2、扫描相关的类
        doScanner(contextConfig.getProperty("scanPackage"));

        //3、实例相关的类，并缓存到IOC容器中
        doInstance();

        //4、完成依赖注入
        doAutowired();

        //5、初始化HandlerMapping
        doInitHandlerMapping();

        //初始化阶段完成
        System.out.println("My SpringFramework init done");
    }

    private void doLoadConfig(String contextConfigLocation) {
        //在CLASSPATH路径下取找配置文件, 这边传入的值是classpath:application.properties
        //需要取到classpath:之后的字符串
        String s = contextConfigLocation.substring(contextConfigLocation.indexOf(':')+1);

        InputStream in = this.getClass().getClassLoader().getResourceAsStream(s);

        try {
            contextConfig.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doScanner(String packageName) {
        ClassLoader classLoader = this.getClass().getClassLoader();
        //file:/D:/Develpoment/JavaDev/SpringStudy/target/SpringStudy-1.0-SNAPSHOT/WEB-INF/classes/com/gyc/demo/
        URL aURL = classLoader.getResource(packageName.replace(".", "/"));//包路径
        if (null != aURL) {
            //把Url变成File
            File classPath = new File(aURL.getFile());
            //循环，得到com/gyc/demo/下所有文件，变成全类名
            for (File fileName : classPath.listFiles()) {
                if (fileName.isDirectory()) {
                    //如果是文件夹，递归
                    doScanner(packageName+"."+fileName.getName());
                } else {
                    if(!fileName.getName().endsWith(".class"))
                        continue;
                    //获得当前的文件名，前面再加上包名，最后存到容器中
                    String clazzName = packageName + "." + fileName.getName().replace(".class", "");
                    clazzNames.add(clazzName);
                }
            }
        }
    }

    private void doInstance() {
        if(clazzNames.isEmpty()) return;
        //通过类名获取字节码文件并实例化  反射
        try {
            //TODO 这边有个疑问，StudentDao如何没有标注Controller Service，但是在ServiceImpl中被Autowired怎么办？这边是靠MyBatis去实现的么？
            for (String clazzName : clazzNames) {
                //1、对每个全类名获取其字节码文件
                Class<?> clazz = Class.forName(clazzName);

                //1.1只把Service和Controller注解的类放进去 （这一步可以在扫描的时候只放入这两个注解的类啊）
                if(clazz.isAnnotationPresent(GYCController.class)){//Controller一般不重名???@Controller
                    Object instance = clazz.newInstance();
                    //2、把通过反射构造对象放进IoC容器
                    //2.1、key值，即beanName在Spring中默认是类名首字母小写  这个key可能会冲突
                    String beanName = toLowerFirst(clazz.getSimpleName());//传递的是最小的类名
                    ioc.put(beanName, instance);
                } else if(clazz.isAnnotationPresent(GYCService.class)){
                    Object instance = clazz.newInstance();
                    //2、把通过反射构造对象放进IoC容器
                    //2.1、key值，即beanName在Spring中默认是类名首字母小写  这个key可能会冲突
                    String beanName = toLowerFirst(clazz.getSimpleName());//传递的是最小的类名
                    ioc.put(beanName, instance);
                    //2.2、如果不同的包下有同类名的.class文件，那么该方法就不行了，可以自定义beanName
                    GYCService service = clazz.getAnnotation(GYCService.class);
                    if (!"".equals(service.value())) {
                        beanName = service.value();
                    }
                    ioc.put(beanName, instance);

                    //3、全类名（找到实现的接口名） 注入的时候有的时候声明的是 @Autowired StudentService StudentServiceImpl,这个时候就需要注入StudentService接口
                    for (Class<?> i : clazz.getInterfaces()) {
                        if (ioc.containsKey(i.getName())) {
                            throw new Exception("The beanName " + i.getName() + " already exists!!!");
                        }
                        ioc.put(i.getName(), instance);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //把非全类名改成首字母小写
    private String toLowerFirst(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    private void doAutowired() {
        if (ioc.isEmpty()) return;
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            //拿到某个实例对象@Controller和@Service的所有域 Field, 对于所有的域看看有没有加@Autowired(这个注解是注入对象的)
            //有的话@Autowired就实例化对象，并赋值给field域
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(GYCAutowired.class))continue;
                //获取到标注的 GYCAutowired注解
                GYCAutowired gycAutowired = field.getAnnotation(GYCAutowired.class);
                String beanName = gycAutowired.value();
                if ("".equals(beanName)) {
                    beanName = toLowerFirst(field.getType().getSimpleName());//getName全类名，Controller用的不是全类名，用的是SimpleName
                    //Controller里面的域和Service里面的域 用的都是全类名
                }

                //field.set相当于  @GYCAutowired private StudentService studentService;
                //getValue()的值相当于一个引用 studentService ,
                //ioc.get("com.gyc.demo.service.IStudentService"), ioc把这个实例化的对象给引用 studentService
                field.setAccessible(true);
                try {
                    field.set(entry.getValue(), ioc.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doInitHandlerMapping() {
        if(ioc.isEmpty()) return;
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            if (!entry.getValue().getClass().isAnnotationPresent(GYCController.class))continue;

            //记得加上StudentController上面的GYCRequestMapping中的值
            String controllerUrl = "";
            if (entry.getValue().getClass().isAnnotationPresent(GYCRequestMapping.class)) {
                controllerUrl = entry.getValue().getClass().getAnnotation(GYCRequestMapping.class).value();
            }

            //对于每个Controller拿到每个public的方法，看看有没有RequestMapping修饰
            for (Method method : entry.getValue().getClass().getDeclaredMethods()) {
                if(!method.isAnnotationPresent(GYCRequestMapping.class)) continue;
                //把url和对应的method放入handlerMapping容器中，哦对了，url还得把Controller上面的RequestMapping给加上
                // 无正则不架构  这一步是将多余的//替换成/
                String url = ("/"+controllerUrl + "/" + method.getAnnotation(GYCRequestMapping.class).value()).replaceAll("/+", "/");
                handlerMapping.put(url, method);
            }
        }
    }
}
