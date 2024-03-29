package com.wxy.utils.result.code;

/**
 * @author wxy
 * @description 实现自定义错误返回码接口
 * @data 2020/11/11
 */
public enum BaseResponseCode implements ResponseCodeInterface {
	/** 成功和失败 **/
	SUCCESS(20000,"操作成功"),
	SYSTEM_ERROR(20001,"系统异常,请联系系统管理员"),
	OPERATION_ERROR(20002,"操作失败"),
	ADD_ERROR(20002,"新建失败"),
	EDIT_ERROR(20002,"编辑失败"),
	DELETE_ERROR(20002,"删除失败"),

	/** 部门 **/
	PARENT_DEPT_IS_NULL(10000,"上级部门数据不存在"),
	PARENT_NAME_REPEAT(10001,"部门名称重复"),
	PARENT_NAME_IS_NULL(10002,"部门名称不能为空"),
	PARENT_DEPT_IS_OVER(10003,"该上级部门不能创建下级部门"),
	DEPT_IS_NOT_SAME_LEVEL(10004,"请更换到相同的级别下的部门"),
	DEPT_PARENT_IS_NOT_EXISTS(10005,"父级部门已被删除"),
	DEPT_HAS_CHILD(10006,"该部门下存在子部门,不能删除"),
	DEPT_HAS_NOT_EXISTS(10006,"部门不存在"),
	/** 文件类 **/
	UPLOAD_FILE_ERROR(21000,"上传失败"),
	FILE_TOO_LARGE(21001,"上传的文件超出范围"),
	FILE_ID_NULL(21002,"文件内容不能为空"),
	CREATE_FILE_ERROR(21003,"创建文件失败"),
	/** 手机号**/
	PHONE_IS_NULL(22000,"该手机号未注册"),
	PHONE_IS_MONEY_NULL(22001,"短信余额不足"),
	PHONE_IS_OVER(22002,"今日发送超过限额"),
	PHONE_IS_MISTAKE(22003,"手机号码格式错误"),
	PHONE_CODE_NOT_SEND(22004,"您的编辑已超时,验证码失效,请重新发送验证码"),
	PHONE_IS_EXISTS(22005,"该手机号已被注册"),
	PHONE_IS_SUBMIT(22005,"您已提交"),
	PHONE_CODE_MISTAKE(22006,"验证码输入错误"),
	/** 表单 **/
	DATA_ERROR(23000,"传入数据异常"),
	METHOD_IDENTITY_ERROR(23002,"数据校验异常"),
	ACCOUNT_ERROR(23003,"该账号不存在"),
	ACCOUNT_LOCK(23004,"该账号被锁定,请联系系统管理员"),
	ACCOUNT_PASSWORD_ERROR(23005,"用户名密码不匹配"),
	ACCOUNT_EXISTS(23006,"该账号已存在"),
	ACCOUNT_HAS_DELETED_ERROR(23007,"该账号已被删除，请联系系统管理员"),
	HAS_DELETED_ERROR(23007,"不存在该记录"),
	VERTIFY_CODE_ERROR(23008,"验证码出错了"),
	IDS_NOT_NULL(23011,"请先选择要删除的信息"),
	ID_NOT_NULL(23011,"该账号不能删除"),
	/** shiro **/
	TOKEN_ERROR(24000,"用户未登录，请重新登录"),
	TOKEN_NOT_NULL(24001,"token 不能为空"),
	SHIRO_AUTHENTICATION_ERROR(24002,"用户认证异常"),
	TOKEN_PAST_DUE(24003,"token失效,请刷新token"),
	NOT_PERMISSION(24004,"没有权限访问该资源"),
	NOT_SET_PERMISSIONS(24005,"不符合设定权限"),
	;

	/**
	 * 响应状态码
	 */
	private final int code;

	/**
	 * 响应提示
	 * @return
	 */
	private final String msg;

	BaseResponseCode(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	@Override
	public int getCode() {
		return code;
	}

	@Override
	public String getMsg() {
		return msg;
	}
}
