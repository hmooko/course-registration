package com.example.courseregistration.course.service;

import com.example.courseregistration.course.dto.CourseResponseDto;
import com.example.courseregistration.course.repository.CourseRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
