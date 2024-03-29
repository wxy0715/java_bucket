package com.wxy.study.wechat.common;

import lombok.Data;

import java.util.UUID;

@Data
public class BaseResponseVO<M> {

	private String requestId;
	private M result;

	public static<M> BaseResponseVO success(){
		BaseResponseVO baseResponseVO = new BaseResponseVO();
		baseResponseVO.setRequestId(genRequestId());

		return baseResponseVO;
	}

	public static<M> BaseResponseVO success(M result){
		BaseResponseVO baseResponseVO = new BaseResponseVO();
		baseResponseVO.setRequestId(genRequestId());
		baseResponseVO.setResult(result);

		return baseResponseVO;
	}

	private static String genRequestId(){
		return UUID.randomUUID().toString();
	}

}
