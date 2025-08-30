package com.example.studentmanagement.service;

import com.example.studentmanagement.dto.StudentRequest;
import com.example.studentmanagement.dto.StudentResponse;
import com.example.studentmanagement.model.Student;
import com.example.studentmanagement.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentService implements StudentServiceInterface {

    @Autowired
    private StudentRepository studentRepository;

    // Create a new student
    public StudentResponse createStudent(StudentRequest request) {
        // Check if email already exists
        if (studentRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Student with email " + request.getEmail() + " already exists");
        }

        Student student = new Student();
        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());
        student.setEmail(request.getEmail());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setPhoneNumber(request.getPhoneNumber());
        student.setCourse(request.getCourse());

        Student savedStudent = studentRepository.save(student);
        return convertToResponse(savedStudent);
    }

    // Get all students
    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get student by ID
    public StudentResponse getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
        return convertToResponse(student);
    }

    // Update student
    public StudentResponse updateStudent(Long id, StudentRequest request) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));

        // Check if email is being changed and if new email already exists
        if (!existingStudent.getEmail().equals(request.getEmail()) && 
            studentRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Student with email " + request.getEmail() + " already exists");
        }

        existingStudent.setFirstName(request.getFirstName());
        existingStudent.setLastName(request.getLastName());
        existingStudent.setEmail(request.getEmail());
        existingStudent.setDateOfBirth(request.getDateOfBirth());
        existingStudent.setPhoneNumber(request.getPhoneNumber());
        existingStudent.setCourse(request.getCourse());

        Student updatedStudent = studentRepository.save(existingStudent);
        return convertToResponse(updatedStudent);
    }

    // Delete student
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new RuntimeException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }

    // Search students by name
    public List<StudentResponse> searchStudentsByName(String name) {
        return studentRepository.findByFullNameContaining(name)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get students by course
    public List<StudentResponse> getStudentsByCourse(String course) {
        return studentRepository.findByCourse(course)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get student by email
    public StudentResponse getStudentByEmail(String email) {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found with email: " + email));
        return convertToResponse(student);
    }

    // Helper method to convert Student entity to StudentResponse DTO
    private StudentResponse convertToResponse(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                student.getDateOfBirth(),
                student.getPhoneNumber(),
                student.getCourse(),
                student.getEnrollmentDate(),
                student.getCreatedAt(),
                student.getUpdatedAt()
        );
    }
}
