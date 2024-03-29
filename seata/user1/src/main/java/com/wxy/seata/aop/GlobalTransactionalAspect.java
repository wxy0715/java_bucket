//package com.wxy.seata.aop;
//
//import com.wxy.redis.config.RedisCache;
//import io.seata.core.context.RootContext;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.support.TransactionSynchronizationAdapter;
//import org.springframework.transaction.support.TransactionSynchronizationManager;
//
//import javax.annotation.Resource;
//import java.util.ArrayList;
//import java.util.concurrent.atomic.AtomicInteger;
//
//
//@Aspect
//@Component
//@Slf4j
//public class GlobalTransactionalAspect {
//    public static ThreadLocal<ArrayList<String>> localTCCacheKeys = ThreadLocal.withInitial(ArrayList::new);
//    @Resource
//    private RedisCache redisCache;
//    private final GlobalTransactionalAspect globalTransactionalAspect;
//
//    public GlobalTransactionalAspect(@Lazy GlobalTransactionalAspect globalTransactionalAspect) {
//        this.globalTransactionalAspect = globalTransactionalAspect;
//    }
//
//    public void doDeleteGlobalTransactionalCache() {
//        String XID = RootContext.getXID();
//        if (StringUtils.isEmpty(XID)) {
//            XID = String.valueOf(Thread.currentThread().getId());
//        }
//        try {
//            if (!StringUtils.isEmpty(XID)) {
//                ArrayList<String> keys = (ArrayList<String>) redisCache.get("GlobalTransactional" + XID);
//                if (keys != null) {
//                    keys.forEach(key -> {
//                        redisCache.deleteObject(key);
//                    });
//                }
//            }
//        } catch (Exception e) {
//        } finally {
//            redisCache.deleteObject("GlobalTransactional" + XID);
//        }
//    }
//
//    //考虑嵌套事务,多层次的删
//    ThreadLocal<AtomicInteger> threadTCNums = ThreadLocal.withInitial(AtomicInteger::new);
//
//    @Before(value = "@annotation(org.springframework.transaction.annotation.Transactional)")
//    public void deleteTransactionalCache(JoinPoint joinPoint) {
//        threadTCNums.get().incrementAndGet();
//        try {
//            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
//                @Override
//                public void afterCompletion(int status) {
//                    super.afterCompletion(status);
//                    if (threadTCNums.get().decrementAndGet() == 0) {
//                        // 删除缓存
//                        threadTCNums.remove();
//                    }
//                }
//            });
//        } catch (Exception e) {
//            log.error("deleteTransactionalCache failed:" + e.getMessage());
//        }
//    }
//
//}
