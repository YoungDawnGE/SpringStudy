package com.gyc.a02MyAspect;

/**
 * Created by GYC
 * 2020/11/19 19:06
 * a01log中使用了Spring提供的AOP的接口
 * 本包a02MyAspect自定义了一个切面类GYCAspect
 *
 */
public class GYCAspect {

    public void gycBefore() {
        System.out.println("[GYCDebug]========方法执行前========");
    }

    public void gycAfter() {
        System.out.println("[GYCDebug]========方法执行后========");
    }

}
