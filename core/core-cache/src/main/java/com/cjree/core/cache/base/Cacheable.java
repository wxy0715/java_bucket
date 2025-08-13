package com.cjree.core.cache.base;

import com.cjree.core.common.ResponseCode;
import com.cjree.core.common.exception.DataVerifyException;
import com.cjree.core.common.exception.Error;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;

/**
 * 标志接口，表明实现了该接口的类的实例都可被缓存
 */
public interface Cacheable {

    static String getCacheKey(String entityName, Long entityId) {
        if (StringUtils.isEmpty(entityName) || Objects.isNull(entityId)) {
            throw new DataVerifyException(Error.builder()
                    .responseCode(ResponseCode.DATA_VERIFY_EMPTY_ID)
                    .build());
        }
        return entityName + ":" + entityId.toString();
    }

    Serializable getCacheKey();

}
