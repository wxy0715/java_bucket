package com.cjree.core.canal;

import com.alibaba.otter.canal.protocol.CanalEntry;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 基于canal的数据处理器
 */
@Slf4j
public class CanalDataHandler extends TypeConvertHandler {

    /**
     * 将binlog的记录解析为一个bean对象
     *
     * @param columnList
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T convertToBean(List<CanalEntry.Column> columnList, Class<T> clazz) {
        T bean = null;
        try {
            bean = clazz.newInstance();
            Field[] fields = clazz.getDeclaredFields();
            AccessibleObject.setAccessible(fields, true);
            Map<String, Field> fieldMap = new HashMap<>();
            for (Field field : fields) {
                fieldMap.put(field.getName(), field);
            }
            if (fieldMap.containsKey("serialVersionUID")) {
                fieldMap.remove("serialVersionUID".toLowerCase());
            }
            for (CanalEntry.Column column : columnList) {
                String columnName = column.getName();
                // 把columnName下划线转驼峰
                columnName = parseToCamel(columnName);
                String columnValue = column.getValue();
                if (fieldMap.containsKey(columnName)) {
                    //基础类型转换不了
                    Field field = fieldMap.get(columnName);
                    if(Objects.isNull(field)){
                        continue;
                    }
                    Class<?> type = field.getType();
                    if(BEAN_FIELD_TYPE.containsKey(type)){
                        switch (BEAN_FIELD_TYPE.get(type)) {
                            case "Integer":
                                field.set(bean, parseToInteger(columnValue));
                                break;
                            case "java.math.BigDecimal":
                                field.set(bean, parseToBigDecimal(columnValue));
                                break;
                            case "Long":
                                field.set(bean, parseToLong(columnValue));
                                break;
                            case "Double":
                                field.set(bean, parseToDouble(columnValue));
                                break;
                            case "String":
                                field.set(bean, columnValue);
                                break;
                            case "java.handle.Date":
                                field.set(bean, parseToDate(columnValue));
                                break;
                            case "java.sql.Date":
                                field.set(bean, parseToSqlDate(columnValue));
                                break;
                            case "java.sql.Timestamp":
                                field.set(bean, parseToTimestamp(columnValue));
                                break;
                            case "java.sql.Time":
                                field.set(bean, parseToSqlTime(columnValue));
                                break;
                        }
                    }else{
                        field.set(bean, parseObj(columnValue));
                    }
                }

            }
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("[CanalDataHandler]convertToBean，初始化对象出现异常，对象无法被实例化,异常为{}", e);
        }
        return bean;
    }


    /**
     * 下划线转驼峰
     */
    public static String parseToCamel(String columnName){
        if(columnName.contains("_")){
            String[] split = columnName.split("_");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < split.length; i++) {
                if(i == 0){
                    sb.append(split[i]);
                }else{
                    sb.append(split[i].substring(0,1).toUpperCase()).append(split[i].substring(1));
                }
            }
            return sb.toString();
        }
        return columnName;
    }


    
    /**
     * 其他类型自定义处理
     *
     * @param source
     * @return
     */
    public static Object parseObj(String source){
        return source;
    }

    
}

