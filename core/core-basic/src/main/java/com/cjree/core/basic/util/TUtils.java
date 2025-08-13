package com.cjree.core.basic.util;


import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wangxingyu
 * Bean 前后空格处理
 **/
@Slf4j
public class TUtils {
    /**
     * 去掉bean中所有属性为字符串的前后空格
     * @param bean
     * @throws Exception
     */
    public static void trimBeanString(Object bean) throws Exception {
        if (bean != null) {
            //获取所有的字段包括public,private,protected,private
            Field[] fields = bean.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
                // 对其中的集合同样进行空格处理
                if (f.getType().getName().equals("java.util.List")) {
                    List<Object> objects = objCastList(getFieldValue(bean, f.getName()), Object.class);
                    for (Object object : objects) {
                        trimBeanString(object);
                    }
                }
                if (f.getType().getName().equals("java.lang.String")) {
                    String key = f.getName();
                    //获取字段名
                    Object value = getFieldValue(bean, key);
                    if (value == null || value.toString().length() == 0){
                        continue;
                    }
                    if (value.toString().length() == value.toString().trim().length()){
                        continue;
                    }
                    log.info("去除字段空格前{},去除后{}",value,value.toString().trim());
                    setFieldValue(bean, key, value.toString().trim());
                }
            }
        }
    }

    /**
     * 利用反射通过get方法获取bean中字段fieldName的值
     *
     * @param bean
     * @param fieldName
     * @return
     * @throws Exception
     */
    private static Object getFieldValue(Object bean, String fieldName)
            throws Exception {
        StringBuffer result = new StringBuffer();
        String methodName = result.append("get")
                .append(fieldName.substring(0, 1).toUpperCase())
                .append(fieldName.substring(1)).toString();

        Object rObject = null;
        Method method = null;

        @SuppressWarnings("rawtypes")
        Class[] classArr = new Class[0];
        method = bean.getClass().getMethod(methodName, classArr);
        rObject = method.invoke(bean, new Object[0]);

        return rObject;
    }

    /**
     * 利用发射调用bean.set方法将value设置到字段
     *
     * @param bean
     * @param fieldName
     * @param value
     * @throws Exception
     */
    private static void setFieldValue(Object bean, String fieldName, Object value)
            throws Exception {
        StringBuffer result = new StringBuffer();
        String methodName = result.append("set")
                .append(fieldName.substring(0, 1).toUpperCase())
                .append(fieldName.substring(1)).toString();

        //利用反射调用bean.set方法将value设置到字段
        @SuppressWarnings("rawtypes")
        Class[] classArr = new Class[1];
        classArr[0] = "java.lang.String".getClass();
        Method method = bean.getClass().getMethod(methodName, classArr);
        method.invoke(bean, value);
    }

    /**
     * 集合
     * @param obj
     * @param clazz
     * @param <R>
     * @return
     */
    private static <R> List<R>objCastList(Object obj, Class<R> clazz){
        List<R> list =new ArrayList<>();
        if(obj instanceof List<?>){
            for(Object o :(List<?>)obj){
                list.add(clazz.cast(o));
            }
        }
        return list;
    }
}
