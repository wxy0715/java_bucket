package com.cjree.core.common.log;

import cn.hutool.core.util.StrUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * spanId生成器
 */
public class SpanIdGenerator {

    public static volatile TransmittableThreadLocal<String> currentSpanIdTL = new TransmittableThreadLocal<>();

    public static volatile TransmittableThreadLocal<AtomicInteger> spanIndex = new TransmittableThreadLocal<>();

    public static void putSpanId(String spanId) {
        if (StringUtils.isBlank(spanId)) {
            spanId = "0";
        }
        currentSpanIdTL.set(spanId);
        spanIndex.set(new AtomicInteger(0));
    }

    public static String getSpanId() {
        return currentSpanIdTL.get();
    }

    public static void removeSpanId() {
        currentSpanIdTL.remove();
    }

    public static String generateNextSpanId() {
        String currentSpanId = TLogContext.getSpanId();
        int currentSpanIndex = spanIndex.get().incrementAndGet();
        return StrUtil.format("{}.{}", currentSpanId, currentSpanIndex);
    }
}
