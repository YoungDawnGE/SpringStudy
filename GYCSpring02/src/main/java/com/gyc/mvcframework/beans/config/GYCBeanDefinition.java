package com.gyc.mvcframework.beans.config;

/**
 * Created by GYC
 * 2020/11/22 11:02
 */
public class GYCBeanDefinition {
    private String factoryBeanName;
    private String beanClassName;

    public GYCBeanDefinition(String factoryBeanName, String beanClassName) {
        this.factoryBeanName = factoryBeanName;
        this.beanClassName = beanClassName;
    }

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }
}
