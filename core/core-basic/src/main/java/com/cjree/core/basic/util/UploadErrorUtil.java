package com.cjree.core.basic.util;

/**
 * 针对excel导入报错的处理
 */
public class UploadErrorUtil {
    /** 去除valid注解的前缀报错*/
    public static String getConstraintViolationExceptionMessage(Exception e){
        String message = e.getMessage();
        if(e instanceof jakarta.validation.ConstraintViolationException){
            try {
                message = "";
                if (e.getMessage().contains(",")){
                    for (String s : e.getMessage().split(",")) {
                        message += s.substring(s.indexOf(": ")+1) + " ";
                    }
                }else {
                    message = e.getMessage().substring(e.getMessage().indexOf(": ") +1);
                }
            } catch (Exception e1) {
                return message;
            }
        }else if (e instanceof NullPointerException) {
            return "该数据存在异常";
        }
        if (message.contains("重复")){
            return "该数据已经存在";
        }
        return message;
    }
}
