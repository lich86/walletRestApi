package com.chervonnaya.wallet.config;

import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.Redisson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class RedissonConfig {


    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() throws IOException {
        ClassPathResource resource = new ClassPathResource("singleNodeConfig.json");
        try (InputStream inputStream = resource.getInputStream()) {
            Config config = Config.fromJSON(inputStream);
            return Redisson.create(config);
        }
    }
}
