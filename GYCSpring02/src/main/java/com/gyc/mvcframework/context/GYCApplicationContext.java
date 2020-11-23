package com.gyc.mvcframework.context;

import com.gyc.mvcframework.annonation.GYCAutowired;
import com.gyc.mvcframework.annonation.GYCController;
import com.gyc.mvcframework.annonation.GYCService;
import com.gyc.mvcframework.beans.GYCBeanWrapper;
import com.gyc.mvcframework.beans.config.GYCBeanDefinition;
import com.gyc.mvcframework.beans.support.GYCBeanDefinitionReader;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by GYC
 * 2020/11/22 11:00
 * ioc容器
 */
public class GYCApplicationContext {
    private Map<String, GYCBeanDefinition> beanDefinitionMap = new HashMap<>();
    private Map<String, GYCBeanWrapper> factoryBeanInstanceCache = new HashMap<>();

    //缓存原生对象
//    private Map<String, Object> factoryBeanObjectCache = new HashMap<>();

    public GYCApplicationContext(String... conifgLocations) {
        try {
            // 1、读取配置文件，并解析成为BeanDefinition对象，
            GYCBeanDefinitionReader reader = new GYCBeanDefinitionReader(conifgLocations);
            List<GYCBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
            // 2、并缓存到BeanDefinition中
            doRegisterBeanDefinition(beanDefinitions);
            // 3、触发创建对象的当作，调用getBean方法
            doCreateBean();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doCreateBean() {
        for (Map.Entry<String, GYCBeanDefinition> entry : beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            //真正触发IOC和DI的动作
            //1、创建bean
            //2、依赖注入
            getBean(beanName);
        }
    }

    private void doRegisterBeanDefinition(List<GYCBeanDefinition> beanDefinitions) throws Exception {
        for (GYCBeanDefinition beanDefinition : beanDefinitions) {
            if (beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                throw new Exception("The" + beanDefinition.getFactoryBeanName() + " exists!");
            } else {
                beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
                beanDefinitionMap.put(beanDefinition.getBeanClassName(), beanDefinition);
            }
        }
    }

    public Object getBean(String beanName) {
        if (factoryBeanInstanceCache.containsKey(beanName)) {
            return factoryBeanInstanceCache.get(beanName).getWrapperedInstance();
        }
        //-------------创建实例-------------
        //1、获取配置信息,只要拿到GYCBeanDefinition对象就拿到了配置
        GYCBeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        //2、用反射创建实例
        Object instance = initBean(beanDefinition);
        //   实例可能是代理对象，也可能是实例对象，封装成beanDefinitionWrapper，并放入缓存
        GYCBeanWrapper beanWrapper = new GYCBeanWrapper(instance);
        factoryBeanInstanceCache.put(beanName, beanWrapper);
        //-------------依赖注入-------------
        populateBean(beanWrapper);//populate填充
        return factoryBeanInstanceCache.get(beanName).getWrapperedInstance();
    }

    //填充bean
    private void populateBean(GYCBeanWrapper beanWrapper) {
        Object instance = beanWrapper.getWrapperedInstance();
        Class<?> clazz = beanWrapper.getWrapperedClass();

        if (!clazz.isAnnotationPresent(GYCController.class) && !clazz.isAnnotationPresent(GYCService.class)) {
            return;
        }

        //对于每个IOC容器的对象，查看它的每一个Field，
        // 如果Field上有@GYCAutowired的注解，则进行赋值（依赖注入）
        // 拿到所有的Field,包括private public protected default
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(GYCAutowired.class)) continue;
            //赋值
            GYCAutowired autowired = field.getAnnotation(GYCAutowired.class);
            //取GYCAutowired的值如果有的话
            String autoWiredBeanName = autowired.value();
            //如果GYCAutowired的值为空，就用Field字段的全类名
            if ("".equals(autoWiredBeanName)) {
                autoWiredBeanName = field.getType().getName();
            }
            //比如：给private StudentService studentService这个声明赋IoC之前初始化的值
            field.setAccessible(true);//暴力破解
            try {
                if (!factoryBeanInstanceCache.containsKey(autoWiredBeanName)) continue;
                field.set(instance, factoryBeanInstanceCache.get(autoWiredBeanName).getWrapperedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }

    private Object initBean(GYCBeanDefinition beanDefinition) {
        String className = beanDefinition.getBeanClassName();
        Object instance = null;

        try {
            //原生对象的初始化
            Class<?> clazz = Class.forName(className);
            instance = clazz.newInstance();

            //如果要代理，接入AOP，代码后期补充
//            factoryBeanObjectCache.put(beanName, instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    public Object getBean(Class className) {
        //全类名
        return getBean(className.getName());
    }

    public int getBeanDefinitionCount() {
        return beanDefinitionMap.size();
    }

    public String[] getBeanDefinitionNames() {
        return beanDefinitionMap.keySet().toArray(new String[0]);
    }
}
