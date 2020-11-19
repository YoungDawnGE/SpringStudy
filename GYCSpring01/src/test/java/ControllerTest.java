import com.gyc.demo.controller.StudentController;
import com.gyc.mvcframework.annonation.GYCController;
import org.junit.Test;

/**
 * Created by GYC
 * 2020/6/22 10:34
 */
public class ControllerTest {
    @Test
    public void testStudentController() {
        StudentController studentController = new StudentController();

        System.out.println(studentController.getClass().isAnnotationPresent(GYCController.class));

    }
}
