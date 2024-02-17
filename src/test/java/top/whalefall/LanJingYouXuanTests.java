package top.whalefall;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.whalefall.service.impl.ShopServiceImpl;
import top.whalefall.utils.CacheClient;

import javax.annotation.Resource;

import java.util.concurrent.TimeUnit;

import static top.whalefall.utils.RedisConstants.CACHE_SHOP_KEY;

@SpringBootTest
public class LanJingYouXuanTests {

    @Resource
    private ShopServiceImpl shopService;
    @Resource
    private CacheClient cacheClient;

    @Test
    void testSaveShop() {
        cacheClient.setWithLogicalExpire(CACHE_SHOP_KEY + 1, shopService.getById(1L), 2L, TimeUnit.SECONDS);
    }
}
