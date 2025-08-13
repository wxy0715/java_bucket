package com.cjree.core.common.exception;

import com.cjree.core.common.ResponseCode;
import com.cjree.core.common.base.BaseException;

/**
 * 数据访问异常
 */
@SuppressWarnings("serial")
public class DataAccessException extends BaseException {

    public DataAccessException(Error response) {
        super(response);
    }

    public DataAccessException(Throwable e) {
        super(e, Error.builder().responseCode(ResponseCode.DATA_ACCESS_EXCEPTION).build());
    }

    public DataAccessException(Throwable e, Error response) {
        super(e, response);
    }

}
