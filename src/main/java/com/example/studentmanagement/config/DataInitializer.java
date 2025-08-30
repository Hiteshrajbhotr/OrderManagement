package com.example.studentmanagement.config;

import com.example.studentmanagement.model.Student;
import com.example.studentmanagement.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public void run(String... args) throws Exception {
        // Initialize with sample data if database is empty
        if (studentRepository.count() == 0) {
            initializeSampleData();
        }
    }

    private void initializeSampleData() {
        // Create sample students
        Student student1 = new Student();
        student1.setFirstName("Alice");
        student1.setLastName("Johnson");
        student1.setEmail("alice.johnson@example.com");
        student1.setDateOfBirth(LocalDate.of(1999, 5, 15));
        student1.setPhoneNumber("1234567890");
        student1.setCourse("Computer Science");

        Student student2 = new Student();
        student2.setFirstName("Bob");
        student2.setLastName("Smith");
        student2.setEmail("bob.smith@example.com");
        student2.setDateOfBirth(LocalDate.of(2000, 8, 22));
        student2.setPhoneNumber("0987654321");
        student2.setCourse("Mathematics");

        Student student3 = new Student();
        student3.setFirstName("Carol");
        student3.setLastName("Davis");
        student3.setEmail("carol.davis@example.com");
        student3.setDateOfBirth(LocalDate.of(1998, 12, 3));
        student3.setPhoneNumber("5555555555");
        student3.setCourse("Physics");

        Student student4 = new Student();
        student4.setFirstName("David");
        student4.setLastName("Wilson");
        student4.setEmail("david.wilson@example.com");
        student4.setDateOfBirth(LocalDate.of(2001, 3, 10));
        student4.setPhoneNumber("7777777777");
        student4.setCourse("Computer Science");

        // Save sample data
        studentRepository.save(student1);
        studentRepository.save(student2);
        studentRepository.save(student3);
        studentRepository.save(student4);

        System.out.println("Sample student data initialized successfully!");
    }
}
