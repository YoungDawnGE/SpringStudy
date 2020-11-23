package com.gyc.mvcframework.beans;

/**
 * Created by GYC
 * 2020/11/22 10:59
 * 用于把原生对象和代理对象都变成 GYCBeanWrapper
 */
public class GYCBeanWrapper {
    private Object wrapperedInstance;
    private Class<?> wrapperedClass;



    public GYCBeanWrapper(Object instance) {
        wrapperedInstance = instance;
        wrapperedClass = wrapperedInstance.getClass();
    }

    public Object getWrapperedInstance() {
        return wrapperedInstance;
    }

    public Class<?> getWrapperedClass() {
        return wrapperedClass;
    }
}
