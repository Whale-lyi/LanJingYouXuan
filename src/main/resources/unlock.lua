-- KEYS[1] 为锁的key，ARGV[1]为线程标识
if (redis.call('get', KEYS[1]) == ARGV[1]) then
    -- 一致则删除锁
    return redis.call('del', KEYS[1])
end
-- 不一致直接返回
return 0