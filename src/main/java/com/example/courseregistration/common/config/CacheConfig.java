package com.example.courseregistration.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final String COURSE_LIST_CACHE = "courseListCache";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(COURSE_LIST_CACHE);
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(1)
                .maximumSize(10)
                .expireAfterWrite(Duration.ofMinutes(5)));
        return cacheManager;
    }
}
