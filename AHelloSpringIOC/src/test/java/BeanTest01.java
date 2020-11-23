import com.gyc.pojo.Student;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by GYC
 * 2020/11/18 10:15
 */
public class BeanTest01 {
    //1 默认无参构造器创建
    @Test
    public void testGetBean01() {
        ApplicationContext context = new ClassPathXmlApplicationContext("A01_ConstructorDI.xml");
        Student student = (Student) context.getBean("stu1");
        System.out.println(student);
    }
    //2 有参构造器创建
    @Test
    public void testGetBean02() {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        //xml中用下标方式赋值
        Student student = (Student) context.getBean("stu2");
        System.out.println(student);

        //xml中根据参数类型匹配赋值
        Student student2 = (Student) context.getBean("stu3");
        System.out.println(student2);

        //根据参数名字赋值
        Student student3 = (Student) context.getBean("stu4");
        System.out.println(student3);
    }

    //3 测试是否是单例模式  是的是单例模式
    @Test
    public void testGetBean03() {
        ApplicationContext context = new ClassPathXmlApplicationContext("A01_ConstructorDI.xml");
        Student student = (Student) context.getBean("stu1");
        System.out.println(student.hashCode());

        Student student2 = (Student) context.getBean("stu1");
        System.out.println(student2.hashCode());

        System.out.println(context.getBeanDefinitionCount());
    }
}
