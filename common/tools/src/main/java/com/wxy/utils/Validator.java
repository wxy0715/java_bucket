package com.wxy.utils;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * Description: 数据校验
 * @author wxy
 */
public class Validator {
	/**
	 * Description:校验实体类中的属性是否正确 <br/>
	 * @author wxy
	 * @param errorResult：BindingResult类型
	 * @return：如果返回值为null，则代表数据完全正确；如果不为null，则返回的是一个封装错误信息map集合
	 */
	public static Map<String, Object> fieldValidate(BindingResult errorResult) {
		Map<String, Object> errorMap = null;
		boolean flag = errorResult.hasErrors();
		if (flag) {
			errorMap = new HashMap<>();
			// 将字段对应的错误信息答应出来
			List<FieldError> errorList = errorResult.getFieldErrors();
			for (FieldError fieldError : errorList) {
				// 1、获取实体类中的属性名
				String fieldName = fieldError.getField();
				// 2、当数据不满足匹配规则时，获取错误提示信息
				String errorMessage = fieldError.getDefaultMessage();
				errorMap.put(fieldName, errorMessage);
			}
		}
		return errorMap;
	}


	public static final String EMAIL = "[a-z0-9]+([._\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+";
	public static final String QQ = "[1-9][0-9]{4,14}";
	public static final String WECHAT = "[-_a-zA-Z0-9]{6,20}";
	public static final String MOBILE = "1(?:3|4|5|6|7|8|9)\\d{9}";
	public static final String HIGH_PWD = "(?![A-Za-z0-9]+$)(?![a-z0-9\\W]+$)(?![A-Za-z\\W]+$)(?![A-Z0-9\\W]+$)[a-zA-Z0-9\\W\\_]{8,32}";
	public static final String USER_USERNAME = "([A-Za-z]|\\.|\\-|\\@|\\_|\\(|\\)|[0-9]){1,32}";
	public static final String USER_REALNAME = "([A-Za-z]|[\\u4e00-\\u9fa5]|\\-||\\.|\\@|\\_|[0-9]){0,32}";
	public static final String IPV4 = "(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])";
	public static final String IPV6 = "\\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?\\s*";
	public static final String DOMAIN ="(http(s)?:\\/\\/)?(www\\.)?([a-zA-Z])[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+(:\\d+)*(\\/\\w+\\.\\w+)*";
	public static final String PORT = "([0-9]|[1-9]\\d|[1-9]\\d{2}|[1-9]\\d{3}|[1-5]\\d{4}|6[0-4]\\d{3}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5])";
	//------------------验证方法
	/**公共的判断*/
	public static int commonRegWithLength(String str, String pattern, Integer length) {
		return regularWithLength(str,pattern,length);
	}

	public static int commonRegNoLength(String str, String pattern) {
		return regularNoLength(str,pattern);
	}

	/**匹配是否符合正则表达式pattern*/
	private static int regularWithLength(String str,String pattern, Integer length){
		if(null == str || str.trim().length()<=0) {
			//字符串不能为空
			return 1;
		}
		if(length != null && str.trim().length() > length) {
			//超过限制长度
			return 2;
		}
		Pattern p = compile(pattern);
		Matcher m = p.matcher(str);
		if (!m.matches() && !"".equals(str)){
			//不匹配
			return 3;
		}
		//匹配的返回值
		return 0;
	}

	/**匹配是否符合正则表达式pattern*/
	private static int regularNoLength(String str,String pattern){
		if(null == str || str.trim().length()<=0) {
			//字符串不能为空
			return 1;
		}
		Pattern p = compile(pattern);
		Matcher m = p.matcher(str);
		if (!m.matches() && !"".equals(str)){
			//不匹配
			return 3;
		}
		//匹配的返回值
		return 0;
	}

	/**@description 两个正则需要同时满足*/
	public static int commonReg_File(String str, String pattern,String pattern1, Integer length) {
		return Regular_file(str,pattern,pattern1,length);
	}

	/**描述是否超过限制长度*/
	public static boolean desc(String str,Integer length){
		return str.trim().length() <= length;
	}

	private static int Regular_file(String str,String pattern,String pattern1,  Integer length){
		if(null == str || str.trim().length()<=0) {
			//字符串不能为空
			return 1;
		}
		if(length != null && str.trim().length() > length) {
			//超过限制长度
			return 2;
		}
		Pattern p = compile(pattern);
		Matcher m = p.matcher(str);
		Pattern p1 = compile(pattern1);
		Matcher m1 = p1.matcher(str);
		if (!m.matches() && !"".equals(str) && !m1.matches() && !"".equals(str)){
			//不匹配
			return 3;
		}
		//匹配的返回值
		return 0;
	}

	/**判断是否为数字*/
	public static boolean isInteger(String str) {
		Pattern pattern = compile("^[-\\+]?[\\d]*$");
		return pattern.matcher(str).matches();
	}

}
