package com.example.courseregistration.course.repository;

import com.example.courseregistration.course.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
