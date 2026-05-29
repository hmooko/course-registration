package com.example.courseregistration.course.dto;

public record CacheOverheadResponseDto(
        int iterations,
        double caffeineLookupAverageMs,
        double caffeineOtherAverageMs,
        double caffeineTotalAverageMs,
        double redisGetAverageMs,
        double redisDeserializationAverageMs,
        double redisOtherAverageMs,
        double redisTotalAverageMs
) {
}
