package com.cjree.core.common.exception;

import com.cjree.core.common.ResponseCode;
import com.cjree.core.common.base.BaseException;

/**
 * 数据解析异常
 */
public class DataParseException extends BaseException {

    public DataParseException(Error response) {
        super(response);
    }

    public DataParseException(Throwable e) {
        super(e, Error.builder().responseCode(ResponseCode.DATA_PARSE_EXCEPTION).build());
    }

    public DataParseException(Throwable e, Error response) {
        super(e, response);
    }

}
