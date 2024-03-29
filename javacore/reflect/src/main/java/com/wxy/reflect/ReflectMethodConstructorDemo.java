package com.wxy.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ReflectMethodConstructorDemo {

    public static void main(String[] args)
        throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        // 获取所有的构造器
        Constructor<?>[] constructors1 = String.class.getDeclaredConstructors();
        System.out.println("String getDeclaredConstructors 清单（数量 = " + constructors1.length + "）：");
        for (Constructor c : constructors1) {
            System.out.println(c);
        }
        // 获取所有的public构造器
        System.out.println("====================");
        Constructor<?>[] constructors2 = String.class.getConstructors();
        System.out.println("String getConstructors 清单（数量 = " + constructors2.length + "）：");
        for (Constructor c : constructors2) {
            System.out.println(c);
        }
        // 获取具体的构造器
        System.out.println("====================");
        Constructor constructor = String.class.getConstructor(String.class);
        System.out.println(constructor);
        String str = (String) constructor.newInstance("bbb");
        System.out.println(str);
    }

}
