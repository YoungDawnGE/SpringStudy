import com.gyc.dao.UserDao;
import com.gyc.pojo.User;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import sun.nio.cs.US_ASCII;

import java.util.Date;
import java.util.List;

/**
 * Created by GYC
 * 2020/11/20 17:45
 */
public class Transaction {
    //DaoMapperImpl继承了SqlSessionDaoSupport
    @Test
    public void test01() {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        UserDao userMapper = context.getBean("userMapper", UserDao.class);
        List<User> users = userMapper.selectAllUsers(0, 20);
        for (User user : users) {
            System.out.println(user);
        }
    }

    //测试插入数据
    @Test
    public void test02() {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        UserDao userMapper = context.getBean("userMapper", UserDao.class);

        //id = 4686176
        User user = new User();
//        user.setId(4686176);
        user.setUserId("1");
        user.setUserName("testInsert");
        user.setPhone("111");
        user.setLanId(1);
        user.setRegionId(1);
        user.setCreateTime(new Date());

        System.out.println(userMapper.insertUser(user));
    }

    //测试错误的删除数据(删除test02创建的User)
    @Test
    public void test03() {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        UserDao userMapper = context.getBean("userMapper", UserDao.class);
        System.out.println(userMapper.deletesUserById(4686177));
    }

    //测试事务：如果删除失败了，插入需要回滚之前的插入操作
    @Test
    public void test04() {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        UserDao userMapper = context.getBean("userMapper", UserDao.class);
        userMapper.testTransaction();
    }


}
