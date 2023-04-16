package com.scaler.redisapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;


@Configuration
public class RedisConfiguration {
    @Bean
    public LettuceConnectionFactory getLettuceConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration("redis-13410.c89.us-east-1-3.ec2.cloud.redislabs.com", 13410);
        redisStandaloneConfiguration.setPassword("ilPo7Qxpexcvsb2UjONcjv2q9demgn3i");
        return new LettuceConnectionFactory(redisStandaloneConfiguration);

    }


}
