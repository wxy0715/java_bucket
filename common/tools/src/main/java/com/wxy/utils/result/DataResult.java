package com.wxy.utils.result;


import com.wxy.utils.result.code.BaseResponseCode;
import com.wxy.utils.result.code.ResponseCodeInterface;

/**
 * @author wxy
 * @description 返回信息
 * @data 2020/11/11
 */
public class DataResult<T> {

	/** 数据对象 */
	public static final String DATA_TAG = "data";

	private int code;

	private String msg;

	private T data;

	public DataResult(int code, T data) {
		this.code = code;
		this.data = data;
		this.msg=null;
	}

	public DataResult(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public DataResult(T data) {
		this.data = data;
		this.code= BaseResponseCode.SUCCESS.getCode();
		this.msg=BaseResponseCode.SUCCESS.getMsg();
	}

	/**
	 * 操作成功 data为null
	 */
	public DataResult() {
		this.code= BaseResponseCode.SUCCESS.getCode();
		this.msg=BaseResponseCode.SUCCESS.getMsg();
		this.data=null;
	}

	public static <T> DataResult<T> success(){
		return new DataResult<T>();
	}

	/**判断成功还是失败*/
	public static <T> DataResult<T> toAjax(int data){
		if (data > 0) {
			return DataResult.success();
		}else{
			return DataResult.error();
		}
	}

	public static <T> DataResult<T> error(){
		return new DataResult(BaseResponseCode.OPERATION_ERROR);
	}

	public static <T> DataResult<T> error(String msg){
		return new DataResult<T>(20001,msg);
	}

	/**
	 * 操作成功 data 不为null
	 */
	public static <T> DataResult<T> success(T data){
		return new DataResult<T>(data);
	}

	/**
	 * 自定义 返回操作 data 可控
	 */
	public DataResult(int code, String msg, T data) {
		this.code = code;
		this.msg = msg;
		this.data = data;
	}

	public static <T> DataResult<T> getResult(int code,String msg,T data){
		return new DataResult<T>(code,msg,data);
	}

	/**
	 *  自定义返回  data为null
	 */
	public static <T> DataResult<T> getResult(int code,String msg){
		return new DataResult<T>(code,msg);
	}

	/**
	 * 自定义返回 入参一般是异常code枚举 data为空
	 */
	public DataResult(ResponseCodeInterface responseCodeInterface) {
		this.data = null;
		this.code = responseCodeInterface.getCode();
		this.msg = responseCodeInterface.getMsg();
	}

	public static <T> DataResult<T> getResult(BaseResponseCode responseCode){
		return new DataResult<T>(responseCode);
	}

	/**
	 * 自定义返回 入参一般是异常code枚举 data 可控
	 */
	public DataResult(ResponseCodeInterface responseCodeInterface, T data) {
		this.data = data;
		this.code = responseCodeInterface.getCode();
		this.msg = responseCodeInterface.getMsg();
	}

	public static <T> DataResult<T> getResult(BaseResponseCode responseCode, T data){
		return new DataResult<T>(responseCode,data);
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "DataResult{" +
				"code=" + code +
				", msg='" + msg + '\'' +
				", data=" + data +
				'}';
	}
}