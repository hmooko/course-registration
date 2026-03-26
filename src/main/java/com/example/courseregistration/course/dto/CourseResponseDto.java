package com.example.courseregistration.course.dto;

import com.example.courseregistration.course.domain.Course;

public record CourseResponseDto(
        Long id,
        String courseCode,
        String title,
        String professor,
        int capacity,
        int enrolledCount
) {

    public static CourseResponseDto from(Course course) {
        return new CourseResponseDto(
                course.getId(),
                course.getCourseCode(),
                course.getTitle(),
                course.getProfessor(),
                course.getCapacity(),
                course.getEnrolledCount()
        );
    }
}
