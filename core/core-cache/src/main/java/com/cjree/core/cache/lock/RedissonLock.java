package com.cjree.core.cache.lock;

import com.cjree.core.cache.base.Lock;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * Redisson锁实现
 */
@Slf4j
@Component
public class RedissonLock implements Lock {
    private final RedissonClient redissonClient;
    private final ConcurrentMap<String, Thread> lockingThreads = new ConcurrentHashMap<>();
    private static final long DEFAULT_LOCK_DURATION = 30; // 默认锁持有时间 30 秒
    private static final long SPIN_INTERVAL = 50; // 自旋间隔 50ms
    public RedissonLock(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public boolean lock(String key) {
        return lock(key, DEFAULT_LOCK_DURATION);
    }

    @Override
    public boolean lock(String key, long spinDuration) {
        long endTime = System.currentTimeMillis() + spinDuration;
        RLock lock = redissonClient.getLock(key);

        while (System.currentTimeMillis() < endTime) {
            if (lock.tryLock()) {
                recordLockingThread(key);
                return true;
            }
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(SPIN_INTERVAL));
        }
        return false;
    }

    @Override
    public boolean lockInterruptibly(String key) throws InterruptedException {
        return lockInterruptibly(key, Long.MAX_VALUE);
    }

    @Override
    public boolean lockInterruptibly(String key, long spinDuration) throws InterruptedException {
        long endTime = System.currentTimeMillis() + spinDuration;
        RLock lock = redissonClient.getLock(key);

        while (System.currentTimeMillis() < endTime) {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }

            if (lock.tryLock(SPIN_INTERVAL, TimeUnit.MILLISECONDS)) {
                recordLockingThread(key);
                return true;
            }
        }
        return false;
    }

    @Override
    public long getDefaultLockDuration() {
        return DEFAULT_LOCK_DURATION;
    }

    @Override
    public boolean tryLock(String key) {
        RLock lock = redissonClient.getLock(key);
        if (lock.tryLock()) {
            recordLockingThread(key);
            return true;
        }
        return false;
    }

    @Override
    public void unlock(String key) {
        RLock lock = redissonClient.getLock(key);
        try {
            lock.forceUnlock();
        } finally {
            clearLockingThread(key);
        }
    }

    @Override
    public void tryUnlock(String key) {
        RLock lock = redissonClient.getLock(key);
        try {
            // 只有锁未过期且被当前线程持有时才释放
            if (lock.isHeldByCurrentThread() && lock.isLocked()) {
                lock.unlock();
            }
        } finally {
            clearLockingThread(key);
        }
    }

    // ==================== 辅助方法 ====================

    /**
     * 记录持有锁的线程
     */
    private void recordLockingThread(String key) {
        lockingThreads.put(key, Thread.currentThread());
    }

    /**
     * 清除锁关联的线程记录
     */
    private void clearLockingThread(String key) {
        lockingThreads.remove(key);
    }

    /**
     * 获取持有锁的线程
     */
    public Thread getLockingThread(String key) {
        return lockingThreads.get(key);
    }

    /**
     * 带过期时间的锁获取
     * @param key 锁键
     * @param leaseTime 锁持有时间(毫秒)
     * @param waitTime 等待时间(毫秒)
     * @return 是否获取成功
     */
    public boolean tryLock(String key, long leaseTime, long waitTime) throws InterruptedException {
        RLock lock = redissonClient.getLock(key);
        if (lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS)) {
            recordLockingThread(key);
            return true;
        }
        return false;
    }

    /**
     * 安全释放锁（推荐使用）
     */
    public void safeUnlock(String key) {
        try {
            tryUnlock(key);
        } catch (Exception e) {
            // 记录日志但避免抛出异常
        }
    }

    /**
     * 检查锁是否被当前线程持有
     */
    public boolean isHeldByCurrentThread(String key) {
        RLock lock = redissonClient.getLock(key);
        return lock.isHeldByCurrentThread();
    }
}
