package com.example.studentmanagement.service;

import com.example.studentmanagement.dto.StudentRequest;
import com.example.studentmanagement.dto.StudentResponse;

import java.util.List;

public interface StudentServiceInterface {
    
    StudentResponse createStudent(StudentRequest request);
    
    StudentResponse getStudentById(Long id);
    
    List<StudentResponse> getAllStudents();
    
    StudentResponse updateStudent(Long id, StudentRequest request);
    
    void deleteStudent(Long id);
    
    List<StudentResponse> searchStudentsByName(String name);
    
    List<StudentResponse> getStudentsByCourse(String course);
    
    StudentResponse getStudentByEmail(String email);
}
