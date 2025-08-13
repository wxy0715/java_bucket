package com.cjree.core.basic.aop;

import com.cjree.core.cache.factory.CacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import static com.cjree.core.basic.invoker.TransactionalInvoker.localTCCacheKeys;


@Aspect
@Component
@Slf4j
public class TransactionalAspect {
    /**
     * 考虑嵌套事务,多层次的删。
     */
    ThreadLocal<AtomicInteger> threadTCNums = ThreadLocal.withInitial(AtomicInteger::new);

    @Before(value = "@annotation(org.springframework.transaction.annotation.Transactional)")
    public void deleteTransactionalCache(JoinPoint joinPoint) {
        threadTCNums.get().incrementAndGet();
        try {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCompletion(int status) {
                    super.afterCompletion(status);
                    if (threadTCNums.get().decrementAndGet() == 0) {
                        long id = Thread.currentThread().getId();
                        String threadId = String.valueOf(id);
                        ArrayList<String> keys = localTCCacheKeys.get();
                        CompletableFuture.runAsync(() -> {
                            deleteTCCache(threadId, keys);
                        });
                        threadTCNums.remove();
                    }
                }
            });
        } catch (Exception e) {
            log.error("deleteTransactionalCache failed:" + e.getMessage());
        }
    }
    public void deleteTCCache(String threadId, ArrayList<String> keys) {
        try {
            if (!ObjectUtils.isEmpty(threadId) && !ObjectUtils.isEmpty(keys)) {
                log.info("开始删除系统缓存--------threadId:{}", threadId);
                keys.forEach(key -> {
                    // 记录等待删除缓存的cachekey
                    CacheUtil.getCacheCommand().set("TC_WaitDeleteCacheKey:" + key, key);
                    delTCCache(key, 0);
                });
            }
        } catch (Exception e) {
            log.error("deleteTCCache failed:" + e.getMessage());
        } finally {
            keys.clear();
        }
    }

    public void delTCCache(String key, int waitTime) {
        try {
            // 回滚有延迟防止回滚还没完成已有新的脏数据写入
            Thread.sleep(1000L * waitTime);
            CacheUtil.getCacheCommand().del(key);
            // 此cache的缓存已清理，此编辑也删除
            CacheUtil.getCacheCommand().del("TC_WaitDeleteCacheKey:" + key);
        } catch (Exception e) {
            log.error("TransactionalAspect->delTCCache_error:" + e.getMessage());
        }
    }
}

