package com.example.courseregistration.course.controller;

import com.example.courseregistration.course.dto.CourseResponseDto;
import com.example.courseregistration.course.service.CourseService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/no-cache")
    public List<CourseResponseDto> getCoursesNoCache() {
        return courseService.getAllCoursesNoCache();
    }

    @GetMapping("/caffeine")
    public List<CourseResponseDto> getCoursesWithCaffeineCache() {
        return courseService.getAllCoursesWithCaffeineCache();
    }
}
