package top.whalefall.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {
    @Bean
    public RedissonClient redissonClient() {
        // 配置类
        Config config = new Config();
        // 添加单点地址，可以使用config.useClusterServers()添加集群地址
        config.useSingleServer().setAddress("redis://172.30.160.247:6379").setPassword("200214");
        // 创建客户端
        return Redisson.create(config);
    }
}
