package com.wxy.reflect;

import java.lang.reflect.Field;

/**
 * @author <a href="mailto:forbreak@163.com">Zhang Peng</a>
 * @since 2019-03-26
 */
public class ReflectClassDemo04 {

    public static void main(String[] args) throws NoSuchFieldException {
        // 父类
        Class c1 = java.util.ArrayList.class.getSuperclass();
        System.out.println(c1.getCanonicalName());
        System.out.println("====================");

        // public类
        Class<?>[] classes = Character.class.getClasses();
        for (Class c : classes) {
            System.out.println(c.getCanonicalName());
        }
        System.out.println("====================");

        // 所有的类
        Class<?>[] classes2 = Character.class.getDeclaredClasses();
        for (Class c : classes2) {
            System.out.println(c.getCanonicalName());
        }
        System.out.println("====================");

        Field f = System.class.getField("out");
        Class c2 = f.getDeclaringClass();
        System.out.println(c2.getCanonicalName());
        System.out.println("====================");

        Class c3 = Thread.State.class.getEnclosingClass();
        System.out.println(c3.getCanonicalName());
    }

}
// Output:
// java.util.AbstractList
// java.lang.Character.Subset
// java.lang.Character.UnicodeBlock
// java.lang.Character.UnicodeScript
// java.lang.Character.CharacterCache
// java.lang.Character.Subset
// java.lang.Character.UnicodeBlock
// java.lang.Character.UnicodeScript
// java.lang.System
// java.lang.Thread
