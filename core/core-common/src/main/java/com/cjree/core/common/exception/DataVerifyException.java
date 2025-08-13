package com.cjree.core.common.exception;

import com.cjree.core.common.ResponseCode;
import com.cjree.core.common.base.BaseException;

/**
 * 数据校验异常
 */
public class DataVerifyException extends BaseException {

    public DataVerifyException(Error response) {
        super(response);
    }

    public DataVerifyException(Throwable e) {
        super(e, Error.builder().responseCode(ResponseCode.DATA_VERIFY_EXCEPTION).build());
    }

    public DataVerifyException(Throwable e, Error response) {
        super(e, response);
    }

}
