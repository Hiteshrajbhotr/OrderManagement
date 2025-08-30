package com.example.studentmanagement.controller;

import com.example.studentmanagement.dto.StudentRequest;
import com.example.studentmanagement.dto.StudentResponse;
import com.example.studentmanagement.service.StudentServiceInterface;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class StudentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StudentServiceInterface studentService;

    @InjectMocks
    private StudentController studentController;

    private ObjectMapper objectMapper;

    private StudentRequest studentRequest;
    private StudentResponse studentResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(studentController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // For LocalDate/LocalDateTime support
        
        studentRequest = new StudentRequest();
        studentRequest.setFirstName("John");
        studentRequest.setLastName("Doe");
        studentRequest.setEmail("john.doe@example.com");
        studentRequest.setDateOfBirth(LocalDate.of(2000, 1, 15));
        studentRequest.setPhoneNumber("1234567890");
        studentRequest.setCourse("Computer Science");

        studentResponse = new StudentResponse();
        studentResponse.setId(1L);
        studentResponse.setFirstName("John");
        studentResponse.setLastName("Doe");
        studentResponse.setEmail("john.doe@example.com");
        studentResponse.setDateOfBirth(LocalDate.of(2000, 1, 15));
        studentResponse.setPhoneNumber("1234567890");
        studentResponse.setCourse("Computer Science");
        studentResponse.setEnrollmentDate(LocalDate.now());
        studentResponse.setCreatedAt(LocalDateTime.now());
        studentResponse.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/students/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Student Management API is running!"));
    }

    @Test
    void testCreateStudent_Success() throws Exception {
        when(studentService.createStudent(any(StudentRequest.class))).thenReturn(studentResponse);

        mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(studentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        verify(studentService, times(1)).createStudent(any(StudentRequest.class));
    }

    @Test
    void testCreateStudent_BadRequest() throws Exception {
        when(studentService.createStudent(any(StudentRequest.class)))
                .thenThrow(new RuntimeException("Email already exists"));

        mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(studentRequest)))
                .andExpect(status().isBadRequest());

        verify(studentService, times(1)).createStudent(any(StudentRequest.class));
    }

    @Test
    void testGetAllStudents() throws Exception {
        List<StudentResponse> students = Arrays.asList(studentResponse);
        when(studentService.getAllStudents()).thenReturn(students);

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"));

        verify(studentService, times(1)).getAllStudents();
    }

    @Test
    void testGetStudentById_Success() throws Exception {
        when(studentService.getStudentById(1L)).thenReturn(studentResponse);

        mockMvc.perform(get("/api/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"));

        verify(studentService, times(1)).getStudentById(1L);
    }

    @Test
    void testGetStudentById_NotFound() throws Exception {
        when(studentService.getStudentById(1L))
                .thenThrow(new RuntimeException("Student not found"));

        mockMvc.perform(get("/api/students/1"))
                .andExpect(status().isNotFound());

        verify(studentService, times(1)).getStudentById(1L);
    }

    @Test
    void testUpdateStudent_Success() throws Exception {
        when(studentService.updateStudent(eq(1L), any(StudentRequest.class))).thenReturn(studentResponse);

        mockMvc.perform(put("/api/students/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(studentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"));

        verify(studentService, times(1)).updateStudent(eq(1L), any(StudentRequest.class));
    }

    @Test
    void testUpdateStudent_NotFound() throws Exception {
        when(studentService.updateStudent(eq(1L), any(StudentRequest.class)))
                .thenThrow(new RuntimeException("Student not found"));

        mockMvc.perform(put("/api/students/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(studentRequest)))
                .andExpect(status().isNotFound());

        verify(studentService, times(1)).updateStudent(eq(1L), any(StudentRequest.class));
    }

    @Test
    void testDeleteStudent_Success() throws Exception {
        doNothing().when(studentService).deleteStudent(1L);

        mockMvc.perform(delete("/api/students/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Student deleted successfully"));

        verify(studentService, times(1)).deleteStudent(1L);
    }

    @Test
    void testDeleteStudent_NotFound() throws Exception {
        doThrow(new RuntimeException("Student not found")).when(studentService).deleteStudent(1L);

        mockMvc.perform(delete("/api/students/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Student not found"));

        verify(studentService, times(1)).deleteStudent(1L);
    }

    @Test
    void testSearchStudentsByName() throws Exception {
        List<StudentResponse> students = Arrays.asList(studentResponse);
        when(studentService.searchStudentsByName("John")).thenReturn(students);

        mockMvc.perform(get("/api/students/search")
                .param("name", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].firstName").value("John"));

        verify(studentService, times(1)).searchStudentsByName("John");
    }

    @Test
    void testGetStudentsByCourse() throws Exception {
        List<StudentResponse> students = Arrays.asList(studentResponse);
        when(studentService.getStudentsByCourse("Computer Science")).thenReturn(students);

        mockMvc.perform(get("/api/students/course/Computer Science"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].course").value("Computer Science"));

        verify(studentService, times(1)).getStudentsByCourse("Computer Science");
    }

    @Test
    void testGetStudentByEmail_Success() throws Exception {
        when(studentService.getStudentByEmail("john.doe@example.com")).thenReturn(studentResponse);

        mockMvc.perform(get("/api/students/email/john.doe@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        verify(studentService, times(1)).getStudentByEmail("john.doe@example.com");
    }

    @Test
    void testGetStudentByEmail_NotFound() throws Exception {
        when(studentService.getStudentByEmail("john.doe@example.com"))
                .thenThrow(new RuntimeException("Student not found"));

        mockMvc.perform(get("/api/students/email/john.doe@example.com"))
                .andExpect(status().isNotFound());

        verify(studentService, times(1)).getStudentByEmail("john.doe@example.com");
    }
}
