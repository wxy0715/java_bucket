package com.wxy.utils.result.code;

/**
 * @author wxy
 * @description 自定义返回码接口
 * @data 2020/11/11
 */
public interface ResponseCodeInterface {

	/** 状态码 **/
	int getCode();

	/** 信息 **/
	String getMsg();
}
