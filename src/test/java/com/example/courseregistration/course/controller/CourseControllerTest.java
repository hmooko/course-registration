package com.example.courseregistration.course.controller;

import com.example.courseregistration.course.dto.CacheOverheadResponseDto;
import com.example.courseregistration.course.service.CourseCacheOverheadService;
import com.example.courseregistration.course.service.CourseService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CourseControllerTest {

    private final CourseService courseService = mock(CourseService.class);
    private final CourseCacheOverheadService courseCacheOverheadService = mock(CourseCacheOverheadService.class);
    private final CourseController courseController = new CourseController(
            courseService,
            courseCacheOverheadService
    );

    @Test
    void measureCacheOverheadReturnsServiceResult() {
        CacheOverheadResponseDto expected = new CacheOverheadResponseDto(
                100,
                0.11,
                0.01,
                0.12,
                7.70,
                22.24,
                0.00,
                29.94
        );
        when(courseCacheOverheadService.measureDefault()).thenReturn(expected);

        CacheOverheadResponseDto actual = courseController.measureCacheOverhead();

        assertThat(actual).isEqualTo(expected);
    }
}
