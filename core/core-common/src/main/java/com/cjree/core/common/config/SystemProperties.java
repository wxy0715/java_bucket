package com.cjree.core.common.config;

import com.cjree.core.common.ResponseCode;
import com.cjree.core.common.exception.DataAccessException;
import com.cjree.core.common.exception.Error;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public final class SystemProperties {

    private static Map<String, String> properties = new HashMap<>(128);

    public static void put(String key, String value) {
        properties.put(key, value);
    }

    public static String getString(String key) {
        if (StringUtils.isEmpty(properties.get(key))) {
            throw new DataAccessException(
                    Error.builder()
                            .responseCode(ResponseCode.DATA_ACCESS_UNEXIST_KEY)
                            .arg(key, key)
                            .build());
        }
        return properties.get(key);
    }

    public static String getString(String key, String defaultValue) {
        if (StringUtils.isEmpty(properties.get(key))) {
            return defaultValue;
        }
        return properties.get(key);
    }

    public static Integer getInt(String key) {
        if (StringUtils.isEmpty(properties.get(key))) {
            throw new DataAccessException(
                    Error.builder()
                            .responseCode(ResponseCode.DATA_ACCESS_UNEXIST_KEY)
                            .arg(key, key)
                            .build());
        }
        return Integer.parseInt(properties.get(key).trim());
    }

    public static int getInt(String key, int defaultValue) {
        if (StringUtils.isEmpty(properties.get(key))) {
            return defaultValue;
        }
        return Integer.parseInt(properties.get(key).trim());
    }

    public static long getLong(String key) {
        if (StringUtils.isEmpty(properties.get(key))) {
            throw new DataAccessException(
                    Error.builder()
                            .responseCode(ResponseCode.DATA_ACCESS_UNEXIST_KEY)
                            .arg(key, key)
                            .build());
        }
        return Long.parseLong(properties.get(key).trim());
    }

    public static long getLong(String key, long defaultValue) {
        if (StringUtils.isEmpty(properties.get(key))) {
            return defaultValue;
        }
        return Long.parseLong(properties.get(key).trim());
    }

}
