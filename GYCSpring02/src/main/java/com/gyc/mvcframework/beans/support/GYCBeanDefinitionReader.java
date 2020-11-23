package com.gyc.mvcframework.beans.support;

import com.gyc.mvcframework.beans.config.GYCBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by GYC
 * 2020/11/22 11:01
 */
public class GYCBeanDefinitionReader {
    private Properties contextConfig = new Properties();
    private List<String> registryBeanClasses = new ArrayList<>();

    public GYCBeanDefinitionReader(String... conifgLocations) {
        doLoadConfig(conifgLocations[0]);
        doScanner(contextConfig.getProperty("scanPackage"));
    }

    private void doLoadConfig(String contextConfigLocation) {
        try (InputStream resource = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation.substring(contextConfigLocation.indexOf(":") + 1))) {
            contextConfig.load(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<GYCBeanDefinition> loadBeanDefinitions() {
        List<GYCBeanDefinition> result = new ArrayList<>();
        try {
            for (String className : registryBeanClasses) {
                Class<?> beanClazz = Class.forName(className);
                if (beanClazz.isInterface()) continue;
                result.add(new GYCBeanDefinition(toLowerFirst(beanClazz.getSimpleName()), className));//放入SimpleName和全类名
                //把实现的接口也放到BeanDefinition中
                for (Class<?> i : beanClazz.getInterfaces()) {
                    result.add(new GYCBeanDefinition(toLowerFirst(i.getName()), className));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void doScanner(String packageName) {
        getClassName(packageName);
    }

    private void getClassName(String packageName) {
        //        /com/gyc/demo
        URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));
        File classPath = new File(url.getFile());
        //扫描文件夹下面的所有文件
        for (File subFile : classPath.listFiles()) {
            if (subFile.isDirectory()) {
                getClassName(packageName + "." + subFile.getName());
            } else {
                //全类名
                if (!subFile.getName().endsWith(".class")) {
                    continue;
                }
                String className = packageName + "." + subFile.getName().replaceAll(".class", "");
                //对上面有注解的类进行操作
                registryBeanClasses.add(className);
            }
        }
    }

    private static String toLowerFirst(String s) {
        char[] chars = s.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
