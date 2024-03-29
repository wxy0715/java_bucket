package com.wxy.utils.cache;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;
import java.util.concurrent.*;

/**
 * 本地缓存
 * @author wxy
 */
@Component
public class LocalCacheUtil implements DisposableBean {

    private static ConcurrentMap<String, LocalCacheData> cacheRepository = new ConcurrentHashMap<String, LocalCacheData>();

    private static ScheduledExecutorService scheduledExecutorService = null;
    private static ScheduledFuture<?> scheduledFuture = null;

    @Override
    public void destroy() throws Exception {
        stop();
    }

    public static void stop(){
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
        if (scheduledExecutorService != null) {
            try {
                scheduledExecutorService.shutdown();
                if(!scheduledExecutorService.awaitTermination(5000, TimeUnit.MILLISECONDS)){
                    scheduledExecutorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduledExecutorService.shutdownNow();
            }
        }
        scheduledExecutorService = null;
    }

    private static class LocalCacheData{
        private String key;
        private Object val;
        private long timeoutTime;

        public LocalCacheData() {
        }

        public LocalCacheData(String key, Object val, long timeoutTime) {
            this.key = key;
            this.val = val;
            this.timeoutTime = timeoutTime;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Object getVal() {
            return val;
        }

        public void setVal(Object val) {
            this.val = val;
        }

        public long getTimeoutTime() {
            return timeoutTime;
        }

        public void setTimeoutTime(long timeoutTime) {
            this.timeoutTime = timeoutTime;
        }
    }

    /**
     * 创建定时任务每分钟清理一次缓存
     */
    static{
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(LocalCacheUtil::cleanTimeoutCache, 0, 1, TimeUnit.MINUTES);
    }

    /**
     * set cache
     * @param key
     * @param val
     * @param cacheTime
     * @return
     */
    public static boolean set(String key, Object val, long cacheTime){
        // set new cache
        if (key==null || key.trim().length()==0) {
            return false;
        }
        if (val == null) {
            remove(key);
        }
        if (cacheTime <= 0) {
            remove(key);
        }
        long timeoutTime = System.currentTimeMillis() + cacheTime;
        LocalCacheData localCacheData = new LocalCacheData(key, val, timeoutTime);
        cacheRepository.put(localCacheData.getKey(), localCacheData);
        return true;
    }

    /**
     * remove cache
     *
     * @param key
     * @return
     */
    public static boolean remove(String key){
        if (key==null || key.trim().length()==0) {
            return false;
        }
        cacheRepository.remove(key);
        return true;
    }

    /**
     * get cache
     *
     * @param key
     * @return
     */
    public static Object get(String key){
        if (key==null || key.trim().length()==0) {
            return null;
        }
        LocalCacheData localCacheData = cacheRepository.get(key);
        if (localCacheData!=null && System.currentTimeMillis()<localCacheData.getTimeoutTime()) {
            return localCacheData.getVal();
        } else {
            remove(key);
            return null;
        }
    }

    /**
     * clean timeout cache
     */
    public static void cleanTimeoutCache(){
        if (!cacheRepository.keySet().isEmpty()) {
            for (String key: cacheRepository.keySet()) {
                LocalCacheData localCacheData = cacheRepository.get(key);
                if (localCacheData!=null && System.currentTimeMillis()>=localCacheData.getTimeoutTime()) {
                    cacheRepository.remove(key);
                }
            }
        }
    }

    /**
     * 判断当前数据是否已过期
     * @param key
     * @return
     */
    private static boolean isExpire(String key){
        if(key.isEmpty()){
            return false;
        }
        if(cacheRepository.containsKey(key)){
            LocalCacheData localCacheData = cacheRepository.get(key);
            return localCacheData != null && System.currentTimeMillis() >= localCacheData.getTimeoutTime();
        }
        return false;
    }

    /**
     * 获取当前缓存大小
     */
    public static int getCacheSize(){
        cleanTimeoutCache();
        return cacheRepository.size();
    }

}
