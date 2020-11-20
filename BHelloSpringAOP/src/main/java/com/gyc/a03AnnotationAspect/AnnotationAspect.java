package com.gyc.a03AnnotationAspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

/**
 * Created by GYC
 * 2020/11/19 20:46
 * 方法3
 * 本包a03AnnotationAspect提供注解实现AOP
 */

@Aspect //标注这个类是一个切面
public class AnnotationAspect {
    @Before("execution(* com.gyc.service.serviceImpl.UserServiceImpl.*(..))")
    public void gycBefore() {
        System.out.println("[GYCDebug]========方法执行前========");
    }

    @After("execution(* com.gyc.service.serviceImpl.UserServiceImpl.*(..))")
    public void gycAfter() {
        System.out.println("[GYCDebug]========方法执行后========");
    }

    @Around("execution(* com.gyc.service.serviceImpl.UserServiceImpl.*(..))")
    public void around(ProceedingJoinPoint jp) {
        System.out.println("round执行前");
        try {
            Signature signature = jp.getSignature();
//            System.out.println("signature:"+signature);
            Object proceed = jp.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        System.out.println("round执行后");
    }
}
