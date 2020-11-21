import com.gyc.dao.UserDao;
import com.gyc.pojo.User;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * Created by GYC
 * 2020/11/20 17:45
 */
public class MybatisSpringTest {
    @Test
    public void test01() {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        UserDao userMapper = context.getBean("userMapper", UserDao.class);
        List<User> users = userMapper.selectAllUsers(0, 50);
        for (User user : users) {
            System.out.println(user);
        }
    }

    //DaoMapperImpl继承了SqlSessionDaoSupport
    @Test
    public void test02() {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        UserDao userMapper = context.getBean("userMapper2", UserDao.class);
        List<User> users = userMapper.selectAllUsers(0, 20);
        for (User user : users) {
            System.out.println(user);
        }
    }
}
