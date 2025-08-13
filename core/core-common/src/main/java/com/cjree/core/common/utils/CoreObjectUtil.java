package com.cjree.core.common.utils;

import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

public class CoreObjectUtil extends ObjectUtils {
    public CoreObjectUtil() {
    }

    public static boolean isNotArray(@Nullable Object obj) {
        return !isEmpty(obj);
    }

    public static boolean isNotEmpty(@Nullable Object[] array) {
        return !isEmpty(array);
    }

    public static boolean isNotEmpty(@Nullable Object obj) {
        return !isEmpty(obj);
    }
}
