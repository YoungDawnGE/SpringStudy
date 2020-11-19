package com.gyc.a01log;

import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

/**
 * Created by GYC
 * 2020/11/19 13:01
 * 为原有的项目增加日志功能
 * 利用Spring提供的AOP的接口
 *
 * 前置
 */
//@Component
public class LogBefore implements MethodBeforeAdvice {
    /**
     *
     * @param method 要执行的目标对象的方法
     * @param objects 上述方法的参数
     * @param target 目标对象
     * @throws Throwable
     */
    @Override
    public void before(Method method, Object[] objects, Object target) throws Throwable {
        System.out.println("[GYCDebug]"+target.getClass().getName()+"的"+method.getName()+"将被执行");
    }
}
