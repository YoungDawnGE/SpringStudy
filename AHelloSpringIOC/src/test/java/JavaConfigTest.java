import com.gyc.appConifg.GYCConfig;
import com.gyc.pojo.GraduateStudent;
import com.gyc.pojo.Professor;
import com.gyc.pojo.Student;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by GYC
 * 2020/11/18 17:15
 * 使用纯java的方式（JavaConfig）配置Spring
 * 而非xml文件
 */
public class JavaConfigTest {
    @Test
    public void test01() {
        //测试@Bean
        ApplicationContext context = new AnnotationConfigApplicationContext(GYCConfig.class);
        GraduateStudent graduateStudent = context.getBean("graduateStudent", GraduateStudent.class);
        System.out.println(graduateStudent);
    }

    @Test
    public void test02() {
        //测试@ComponentScan
        //看看用了@ComponentScan，是不是就可以不用@Bean了
        ApplicationContext context = new AnnotationConfigApplicationContext(GYCConfig.class);
        Professor professor = context.getBean("professor", Professor.class);
        System.out.println(professor);

    }
}
