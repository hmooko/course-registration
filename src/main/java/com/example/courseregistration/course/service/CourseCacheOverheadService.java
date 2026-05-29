package com.example.courseregistration.course.service;

import com.example.courseregistration.course.dto.CacheOverheadResponseDto;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.stereotype.Service;

import static com.example.courseregistration.common.config.CacheConfig.COURSE_LIST_CACHE;
import static com.example.courseregistration.common.config.RedisCacheConfig.REDIS_COURSE_LIST_CACHE;

@Service
public class CourseCacheOverheadService {

    private static final String COURSE_LIST_KEY = "allCourses";
    private static final int DEFAULT_ITERATIONS = 100;

    private final CourseService courseService;
    private final CacheManager caffeineCacheManager;
    private final RedisConnectionFactory redisConnectionFactory;
    private final JdkSerializationRedisSerializer redisValueSerializer = new JdkSerializationRedisSerializer();

    public CourseCacheOverheadService(
            CourseService courseService,
            @Qualifier("cacheManager") CacheManager caffeineCacheManager,
            RedisConnectionFactory redisConnectionFactory
    ) {
        this.courseService = courseService;
        this.caffeineCacheManager = caffeineCacheManager;
        this.redisConnectionFactory = redisConnectionFactory;
    }

    public CacheOverheadResponseDto measureDefault() {
        return measure(DEFAULT_ITERATIONS);
    }

    public CacheOverheadResponseDto measure(int iterations) {
        if (iterations <= 0) {
            throw new IllegalArgumentException("iterations must be greater than zero");
        }

        warmUpCaches();

        Cache caffeineCache = caffeineCacheManager.getCache(COURSE_LIST_CACHE);
        if (caffeineCache == null) {
            throw new IllegalStateException("Caffeine cache is not configured: " + COURSE_LIST_CACHE);
        }

        byte[] redisKey = (REDIS_COURSE_LIST_CACHE + "::" + COURSE_LIST_KEY)
                .getBytes(StandardCharsets.UTF_8);

        long caffeineLookupNanos = 0;
        long caffeineTotalNanos = 0;
        long redisGetNanos = 0;
        long redisDeserializationNanos = 0;
        long redisTotalNanos = 0;

        try (RedisConnection connection = redisConnectionFactory.getConnection()) {
            for (int i = 0; i < iterations; i++) {
                long caffeineStart = System.nanoTime();
                Object caffeineValue = caffeineCache.get(COURSE_LIST_KEY, Object.class);
                long caffeineLookupEnd = System.nanoTime();
                if (caffeineValue == null) {
                    throw new IllegalStateException("Caffeine cache value is empty");
                }
                long caffeineEnd = System.nanoTime();

                long redisStart = System.nanoTime();
                byte[] redisValue = connection.stringCommands().get(redisKey);
                long redisGetEnd = System.nanoTime();
                if (redisValue == null) {
                    throw new IllegalStateException("Redis cache value is empty");
                }

                Object deserializedValue = redisValueSerializer.deserialize(redisValue);
                long redisDeserializationEnd = System.nanoTime();
                if (deserializedValue == null) {
                    throw new IllegalStateException("Redis cache deserialized value is empty");
                }
                long redisEnd = System.nanoTime();

                caffeineLookupNanos += caffeineLookupEnd - caffeineStart;
                caffeineTotalNanos += caffeineEnd - caffeineStart;
                redisGetNanos += redisGetEnd - redisStart;
                redisDeserializationNanos += redisDeserializationEnd - redisGetEnd;
                redisTotalNanos += redisEnd - redisStart;
            }
        }

        return new CacheOverheadResponseDto(
                iterations,
                toAverageMillis(caffeineLookupNanos, iterations),
                toAverageMillis(caffeineTotalNanos - caffeineLookupNanos, iterations),
                toAverageMillis(caffeineTotalNanos, iterations),
                toAverageMillis(redisGetNanos, iterations),
                toAverageMillis(redisDeserializationNanos, iterations),
                toAverageMillis(redisTotalNanos - redisGetNanos - redisDeserializationNanos, iterations),
                toAverageMillis(redisTotalNanos, iterations)
        );
    }

    private void warmUpCaches() {
        courseService.getAllCoursesWithCaffeineCache();
        courseService.getAllCoursesWithRedisCache();
    }

    private double toAverageMillis(long nanos, int iterations) {
        return nanos / 1_000_000.0 / iterations;
    }
}
