package com.gyc.dao;

import com.gyc.pojo.User;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.support.SqlSessionDaoSupport;

import java.util.Date;
import java.util.List;

/**
 * Created by GYC
 * 2020/11/20 18:19
 * 和UserDaoImpl相比 代码简洁了
 */
public class UserDaoImpl extends SqlSessionDaoSupport implements UserDao {
    @Override
    public List<User> selectAllUsers(int offset, int len) {
        SqlSession sqlSession = getSqlSession();
        UserDao mapper = sqlSession.getMapper(UserDao.class);
        return mapper.selectAllUsers(offset, len);
    }

    @Override
    public User selectUserById(int id) {
        //精简版
        return getSqlSession().getMapper(UserDao.class).selectUserById(id);
    }

    @Override
    public int insertUser(User user) {
        return getSqlSession().getMapper(UserDao.class).insertUser(user);
    }

    //写一个错误的sql用于测试事务
    @Override
    public int deletesUserById(int id) {
        return getSqlSession().getMapper(UserDao.class).deletesUserById(id);
    }

    //测试Spring事务
    @Override
    public void testTransaction() {
        User user = new User();
        user.setUserId("2");
        user.setUserName("testTransaction");
        user.setPhone("111");
        user.setLanId(1);
        user.setRegionId(1);
        user.setCreateTime(new Date());
        System.out.println(insertUser(user));//4686181
        System.out.println(deletesUserById(4686180));
    }
}
