package com.example.courseregistration.course.service;

import com.example.courseregistration.course.dto.CourseResponseDto;
import com.example.courseregistration.course.repository.CourseRepository;
import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.example.courseregistration.common.config.CacheConfig.COURSE_LIST_CACHE;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Transactional(readOnly = true)
    public List<CourseResponseDto> getAllCoursesNoCache() {
        return courseRepository.findAll()
                .stream()
                .map(CourseResponseDto::from)
                .toList();
    }

    @Cacheable(cacheNames = COURSE_LIST_CACHE, key = "'allCourses'")
    @Transactional(readOnly = true)
    public List<CourseResponseDto> getAllCoursesWithCaffeineCache() {
        return courseRepository.findAll()
                .stream()
                .map(CourseResponseDto::from)
                .toList();
    }
}
