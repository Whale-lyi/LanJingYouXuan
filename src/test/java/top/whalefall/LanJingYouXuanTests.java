package top.whalefall;

import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import top.whalefall.service.impl.ShopServiceImpl;
import top.whalefall.utils.CacheClient;
import top.whalefall.utils.RedisIdWorker;

import javax.annotation.Resource;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

import static top.whalefall.utils.RedisConstants.CACHE_SHOP_KEY;

@SpringBootTest
public class LanJingYouXuanTests {

    @Resource
    private ShopServiceImpl shopService;
    @Resource
    private CacheClient cacheClient;
    @Resource
    private RedisIdWorker redisIdWorker;
    @Resource
    private RedissonClient redissonClient;

    @Test
    void testRedisson() throws InterruptedException {
        // 获取可重入锁，指定锁名称
        RLock lock = redissonClient.getLock("anyLock");
        // 尝试获取锁，参数分别为：获取锁的最大等待时间（期间会重试），锁自动释放时间，时间单位
        boolean isLock = lock.tryLock(1, 10, TimeUnit.SECONDS);
        if (isLock) {
            try {
                System.out.println("执行业务");
            } finally {
                lock.unlock();
            }
        }
    }

    private ExecutorService es = Executors.newFixedThreadPool(500);
    @Test
    void testIdWorker() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(300);
        Set<Long> set = new CopyOnWriteArraySet<>();
        Runnable task = () -> {
            for (int i = 0; i < 100; i++) {
                long id = redisIdWorker.nextId("order");
                set.add(id);
                System.out.println("id = " + id);
            }
            countDownLatch.countDown();
        };
        long begin = System.currentTimeMillis();
        for (int i = 0; i < 300; i++) {
            es.submit(task);
        }
        countDownLatch.await();
        long end = System.currentTimeMillis();
        System.out.println("time = " + (end - begin));
        System.out.println("size = " + set.size());
    }
    @Test
    void testSaveShop() {
        cacheClient.setWithLogicalExpire(CACHE_SHOP_KEY + 1, shopService.getById(1L), 2L, TimeUnit.SECONDS);
    }
}
