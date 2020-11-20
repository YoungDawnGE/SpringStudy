import com.gyc.dao.UserDao;
import com.gyc.pojo.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by GYC
 * 2020/11/20 10:14
 */
public class MyBatisTest {
    @Test
    public void testGetAllUsers() throws IOException {
        String resourceFile = "mybatis-config.xml";
        InputStream in = Resources.getResourceAsStream(resourceFile);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(in);
        SqlSession session = sqlSessionFactory.openSession(true);
        //以上可以写成工具类

        UserDao userDao = session.getMapper(UserDao.class);
        List<User> users = userDao.selectAllUsers(0, 20);
        for (User user : users) {
            System.out.println(user);
        }
    }

    @Test
    public void testGetUserById() throws IOException {
        String resourceFile = "mybatis-config.xml";
        InputStream in = Resources.getResourceAsStream(resourceFile);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(in);
        SqlSession session = sqlSessionFactory.openSession(true);
        //以上可以写成工具类

        UserDao userDao = session.getMapper(UserDao.class);
        User user = userDao.selectUserById(2);
        System.out.println(user);
    }
}
