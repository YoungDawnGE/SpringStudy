package com.gyc.dao;

import com.gyc.pojo.User;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.support.SqlSessionDaoSupport;

import java.util.List;

/**
 * Created by GYC
 * 2020/11/20 18:19
 * 和UserDaoImpl相比 代码简洁了
 */
public class UserDaoImpl2 extends SqlSessionDaoSupport implements UserDao {
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
}
