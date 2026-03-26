package com.example.courseregistration.course.dto;

import com.example.courseregistration.course.domain.Course;
import java.io.Serializable;

public record CourseResponseDto(
        Long id,
        String courseCode,
        String title,
        String professor,
        int capacity,
        int enrolledCount
) implements Serializable {

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
