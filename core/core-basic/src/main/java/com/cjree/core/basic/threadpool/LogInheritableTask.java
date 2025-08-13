package com.cjree.core.basic.threadpool;

import com.cjree.core.common.log.TLogConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Map;

/**
 * 解决子线程中 MDC 上下文（如 traceId）传递问题
 */
public abstract class LogInheritableTask implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(LogInheritableTask.class);

    // 保存父线程的 MDC 上下文（如 traceId）
    private final Map<String, String> parentMdcContext;

    public LogInheritableTask() {
        // 构造时复制父线程的 MDC 上下文（线程安全）
        this.parentMdcContext = MDC.getCopyOfContextMap();
    }

    /**
     * 子类需实现的任务逻辑
     */
    public abstract void runTask();

    @Override
    public final void run() {
        // 备份子线程原有 MDC 上下文（避免覆盖）
        Map<String, String> originalChildMdc = MDC.getCopyOfContextMap();
        try {
            // 将父线程的 MDC 上下文设置到当前子线程
            if (parentMdcContext != null) {
                MDC.setContextMap(parentMdcContext);
            }
            try {
                MDC.put(TLogConstants.TLOG_THREAD_KEY, String.valueOf(Thread.currentThread().getId()));
            } catch (Exception e) {
                log.error("[TLOG]获取当前线程ID出错", e);
            }
            // 执行实际任务
            runTask();
        } finally {
            // 恢复子线程原有 MDC 上下文（避免线程复用导致的污染）
            if (originalChildMdc != null) {
                MDC.setContextMap(originalChildMdc);
            } else {
                MDC.clear();
            }
        }
    }

    /**
     * 启动任务（新建线程执行）
     */
    public void start() {
        new Thread(this).start();
    }
}