import com.gyc.config.AppConfig;
import com.gyc.service.UserService;
import com.gyc.service.serviceImpl.UserServiceImpl;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by GYC
 * 2020/11/19 13:27
 */
public class AppConfigTest {
    // 暂时不能用以下扫描注解的方式
    // ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
    // 来获取IOC容器，因为所有的AOP目前是写在applicationContext.xml中的，
    // 我暂时还没把AOP的注解写在程序中，所以是无法通过注解ComponentScan扫描到的。
    // *应该用 ClassPathXmlApplicationContext 这个类来获取IOC容器
    // 见test01
    @Test
    public void test01() {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        UserService userService = (UserServiceImpl) context.getBean("userServiceImpl");
        System.out.println(userService.toString());

        userService.addUser();
        userService.deleteUser();
        userService.updateUser();
        userService.getUser();
    }

    // way1 通过Spring提供的接口来实现AOP
    // 用ClassPathXmlApplicationContext
    // 通过配置文件applicationContext.xml
    // applicationContext.xml中需要把<context:component-scan/>标签关闭了，否则还是去扫描注解
    //
    @Test
    public void test02() {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        UserService userService = (UserService) context.getBean("userServiceImpl");
        userService.deleteUser();
        userService.addUser();
        userService.updateUser();
        userService.getUser();
    }

    // way2 自定义切面类 GYCAspect来实现AOP
    // 配置文件在 applicationContext2.xml
    @Test
    public void test03() {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext2.xml");
        UserService userService = (UserService) context.getBean("userServiceImpl2");
        userService.addUser();
        userService.deleteUser();
        userService.updateUser();
        userService.getUser();
    }


}
