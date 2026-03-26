package com.example.courseregistration.common.init;

import com.example.courseregistration.course.domain.Course;
import com.example.courseregistration.course.repository.CourseRepository;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DummyDataGenerator {

    private static final int COURSE_COUNT = 10_000;

    private final CourseRepository courseRepository;

    public DummyDataGenerator(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @PostConstruct
    public void generateCourses() {
        if (courseRepository.count() > 0) {
            return;
        }

        List<Course> courses = new ArrayList<>(COURSE_COUNT);
        for (int i = 1; i <= COURSE_COUNT; i++) {
            String courseCode = "CS" + String.format("%05d", i);
            courses.add(new Course(
                    courseCode,
                    "컴퓨터공학특강 " + i,
                    "Professor " + ((i % 200) + 1),
                    30 + (i % 71),
                    i % 30
            ));
        }
        courseRepository.saveAll(courses);
    }
}
