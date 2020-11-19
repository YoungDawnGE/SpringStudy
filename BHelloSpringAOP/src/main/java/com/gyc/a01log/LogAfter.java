package com.gyc.a01log;

import org.springframework.aop.AfterReturningAdvice;

import java.lang.reflect.Method;

/**
 * Created by GYC
 * 2020/11/19 14:59
 * 利用Spring提供的AOP的接口
 * 后置
 */

//@Component
public class LogAfter implements AfterReturningAdvice {
    /**
     * @param returnValue 下面方法的返回值
     * @param method 要执行的目标对象的方法
     * @param args 上述方法的参数
     * @param target 目标对象
     * @throws Throwable
     */
    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        System.out.println("[GYCDebug]" + target.getClass().getName() + "的" + method.getName() + "执行完成, 返回结果: " + returnValue);
    }
}
