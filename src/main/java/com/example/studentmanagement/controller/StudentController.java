package com.example.studentmanagement.controller;

import com.example.studentmanagement.dto.StudentRequest;
import com.example.studentmanagement.dto.StudentResponse;
import com.example.studentmanagement.service.StudentServiceInterface;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentServiceInterface studentService;

    @GetMapping("/health")
    public String health() {
        return "Student Management API is running!";
    }

    // ==================== STUDENT MANAGEMENT CRUD OPERATIONS ====================

    // CREATE - Add a new student
    @PostMapping
    public ResponseEntity<StudentResponse> createStudent(@Valid @RequestBody StudentRequest request) {
        try {
            StudentResponse response = studentService.createStudent(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // READ - Get all students
    @GetMapping
    public ResponseEntity<List<StudentResponse>> getAllStudents() {
        List<StudentResponse> students = studentService.getAllStudents();
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    // READ - Get student by ID
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id) {
        try {
            StudentResponse student = studentService.getStudentById(id);
            return new ResponseEntity<>(student, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // UPDATE - Update student by ID
    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> updateStudent(@PathVariable Long id, 
                                                        @Valid @RequestBody StudentRequest request) {
        try {
            StudentResponse updatedStudent = studentService.updateStudent(id, request);
            return new ResponseEntity<>(updatedStudent, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // DELETE - Delete student by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable Long id) {
        try {
            studentService.deleteStudent(id);
            return new ResponseEntity<>("Student deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Student not found", HttpStatus.NOT_FOUND);
        }
    }

    // ==================== ADDITIONAL SEARCH OPERATIONS ====================

    // Search students by name
    @GetMapping("/search")
    public ResponseEntity<List<StudentResponse>> searchStudentsByName(@RequestParam String name) {
        List<StudentResponse> students = studentService.searchStudentsByName(name);
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    // Get students by course
    @GetMapping("/course/{course}")
    public ResponseEntity<List<StudentResponse>> getStudentsByCourse(@PathVariable String course) {
        List<StudentResponse> students = studentService.getStudentsByCourse(course);
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    // Get student by email
    @GetMapping("/email/{email}")
    public ResponseEntity<StudentResponse> getStudentByEmail(@PathVariable String email) {
        try {
            StudentResponse student = studentService.getStudentByEmail(email);
            return new ResponseEntity<>(student, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}
