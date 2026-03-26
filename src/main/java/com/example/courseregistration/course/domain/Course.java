package com.example.courseregistration.course.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String courseCode;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 50)
    private String professor;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private int enrolledCount;

    protected Course() {
    }

    public Course(String courseCode, String title, String professor, int capacity, int enrolledCount) {
        this.courseCode = courseCode;
        this.title = title;
        this.professor = professor;
        this.capacity = capacity;
        this.enrolledCount = enrolledCount;
    }

    public Long getId() {
        return id;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getTitle() {
        return title;
    }

    public String getProfessor() {
        return professor;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getEnrolledCount() {
        return enrolledCount;
    }
}
