package top.whalefall.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import top.whalefall.dto.Result;
import top.whalefall.entity.Shop;
import top.whalefall.mapper.ShopMapper;
import top.whalefall.service.IShopService;
import org.springframework.stereotype.Service;
import top.whalefall.utils.CacheClient;
import top.whalefall.utils.RedisData;

import javax.annotation.Resource;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static top.whalefall.utils.RedisConstants.*;

@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private CacheClient cacheClient;

//    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(5);

    @Override
    public Result queryById(Long id) {
        // 缓存穿透
        // Shop shop = queryWithPassThrough(id);
//         Shop shop = cacheClient.queryWithPassThrough(CACHE_SHOP_KEY, id, Shop.class, this::getById, CACHE_SHOP_TTL, TimeUnit.MINUTES);

        // 互斥锁解决缓存击穿
        // Shop shop = queryWithMutex(id);

        // 逻辑过期解决缓存击穿
        // Shop shop = queryWithLogicalExpire(id);
        Shop shop = cacheClient.queryWithLogicalExpire(CACHE_SHOP_KEY, LOCK_SHOP_KEY, id, Shop.class, this::getById, CACHE_SHOP_TTL, TimeUnit.MINUTES);

        if (shop == null) {
            return Result.fail("店铺不存在");
        }
        return Result.ok(shop);
    }

//    /**
//     * 逻辑过期解决缓存击穿
//     * @param id
//     * @return
//     */
//    private Shop queryWithLogicalExpire(Long id) {
//        // 查询商户缓存
//        String key = CACHE_SHOP_KEY + id;
//        String shopJson = stringRedisTemplate.opsForValue().get(key);
//        // Redis中不存在直接返回空
//        if (StrUtil.isBlank(shopJson)) {
//            return null;
//        }
//        // 缓存中存在, 需要判断过期时间
//        RedisData redisData = JSONUtil.toBean(shopJson, RedisData.class);
//        Shop shop = JSONUtil.toBean((JSONObject) redisData.getData(), Shop.class);
//        LocalDateTime expireTime = redisData.getExpireTime();
//        // 未过期，返回商铺信息
//        if (expireTime.isAfter(LocalDateTime.now())) {
//            return shop;
//        }
//        // 过期，尝试获取互斥锁
//        String lockKey = LOCK_SHOP_KEY + id;
//        if (tryLock(lockKey)) {
//            // 成功获取互斥锁, 进行Double Check
//            shopJson = stringRedisTemplate.opsForValue().get(key);
//            redisData = JSONUtil.toBean(shopJson, RedisData.class);
//            shop = JSONUtil.toBean((JSONObject) redisData.getData(), Shop.class);
//            expireTime = redisData.getExpireTime();
//            // Double Check显示未过期，说明重建完成，返回商铺信息
//            if (expireTime.isAfter(LocalDateTime.now())) {
//                return shop;
//            }
//            // 新建线程进行缓存重建，本线程返回旧数据
//            CACHE_REBUILD_EXECUTOR.submit(() -> {
//                try {
//                    // 重建缓存
//                    saveShop2Redis(id, 1800L);
//                } finally {
//                    // 释放锁
//                    unlock(lockKey);
//                }
//            });
//        }
//        // 获取锁失败，直接返回旧数据
//        return shop;
//    }

//    /**
//     * 解决缓存穿透、缓存击穿(互斥锁)
//     * @param id
//     * @return
//     */
//    private Shop queryWithMutex(Long id) {
//        // 查询商户缓存
//        String key = CACHE_SHOP_KEY + id;
//        String shopJson = stringRedisTemplate.opsForValue().get(key);
//        if (StrUtil.isNotBlank(shopJson)) {
//            return JSONUtil.toBean(shopJson, Shop.class);
//        }
//        // 命中空值
//        if (shopJson != null) {
//            return null;
//        }
//        // 缓存未命中, 实现缓存重建
//        // 获取互斥锁，判断是否成功
//        String lockKey = LOCK_SHOP_KEY + id;
//        Shop shop = null;
//        try {
//            if (!tryLock(lockKey)) {
//                // 失败，休眠并重试
//                Thread.sleep(50);
//                return queryWithMutex(id);
//            }
//            // 获取锁成功，DoubleCheck
//            shopJson = stringRedisTemplate.opsForValue().get(key);
//            if (StrUtil.isNotBlank(shopJson)) {
//                return JSONUtil.toBean(shopJson, Shop.class);
//            }
//            // 查询数据库
//            shop = getById(id);
//            // 模拟重建延时
//            Thread.sleep(200);
//            if (shop == null) {
//                // 将空值写入redis, 避免缓存穿透
//                stringRedisTemplate.opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
//                return null;
//            }
//            // 写入redis
//            stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(shop), CACHE_SHOP_TTL, TimeUnit.MINUTES);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        } finally {
//            // 释放锁
//            unlock(lockKey);
//        }
//        return shop;
//    }

//    /**
//     * 缓存穿透示例
//     * @param id
//     * @return
//     */
//    private Shop queryWithPassThrough(Long id) {
//        // 查询商户缓存
//        String key = CACHE_SHOP_KEY + id;
//        String shopJson = stringRedisTemplate.opsForValue().get(key);
//        if (StrUtil.isNotBlank(shopJson)) {
//            return JSONUtil.toBean(shopJson, Shop.class);
//        }
//        // 命中空值
//        if (shopJson != null) {
//            return null;
//        }
//        // 缓存中不存在, 查询数据库
//        Shop shop = getById(id);
//        if (shop == null) {
//            // 将空值写入redis, 避免缓存穿透
//            stringRedisTemplate.opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
//            return null;
//        }
//        // 写入redis
//        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(shop), CACHE_SHOP_TTL, TimeUnit.MINUTES);
//
//        return shop;
//    }

    @Override
    @Transactional
    public Result update(Shop shop) {
        Long id = shop.getId();
        if (id == null) {
            return Result.fail("店铺id不能为空");
        }
        // 更新数据库
        updateById(shop);
        // 删除缓存
        stringRedisTemplate.delete(CACHE_SHOP_KEY + id);

        return Result.ok();
    }
}
