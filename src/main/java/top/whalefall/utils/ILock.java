package top.whalefall.utils;

public interface ILock {
    /**
     * 尝试获取锁
     * @param timeoutSec 锁持有的超时时间，国企自动释放
     * @return 是否获取锁成功
     */
    boolean tryLock(long timeoutSec);

    /**
     * 释放锁
     */
    void unlock();
}
