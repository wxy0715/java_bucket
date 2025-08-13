package com.cjree.core.cache.base;

/**
 * 锁接口
 */
public interface Lock {

    /**
     * 阻塞性的获取锁, 不响应中断
     */
    boolean lock(String key);

    /**
     * 阻塞性的获取锁, 不响应中断
     */
    boolean lock(String key, long spinDuration);

    /**
     * 阻塞性的获取锁, 响应中断
     */
    boolean lockInterruptibly(String key) throws InterruptedException;

    /**
     * 阻塞性的获取锁, 响应中断
     */
    boolean lockInterruptibly(String key, long spinDuration) throws InterruptedException;

    /**
     * 获取锁的默认持续时间
     */
    long getDefaultLockDuration();

    /**
     * 尝试获取锁, 获取不到立即返回, 不阻塞
     */
    boolean tryLock(String key);

    /**
     * 强制释放锁（不管锁有没有过期）
     */
    void unlock(String key);

    /**
     * 尝试释放锁（过期才释放，未过期不释放）
     */
    void tryUnlock(String key);

}
