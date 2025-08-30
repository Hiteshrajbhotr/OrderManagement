package com.example.studentmanagement.repository;

import com.example.studentmanagement.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // Find student by email
    Optional<Student> findByEmail(String email);

    // Find students by first name (case-insensitive)
    List<Student> findByFirstNameContainingIgnoreCase(String firstName);

    // Find students by last name (case-insensitive)
    List<Student> findByLastNameContainingIgnoreCase(String lastName);

    // Find students by course
    List<Student> findByCourse(String course);

    // Custom query to find students by full name
    @Query("SELECT s FROM Student s WHERE " +
           "LOWER(CONCAT(s.firstName, ' ', s.lastName)) LIKE LOWER(CONCAT('%', :fullName, '%'))")
    List<Student> findByFullNameContaining(@Param("fullName") String fullName);

    // Check if email exists (for validation)
    boolean existsByEmail(String email);

    // Find students enrolled in a specific year
    @Query("SELECT s FROM Student s WHERE YEAR(s.enrollmentDate) = :year")
    List<Student> findByEnrollmentYear(@Param("year") int year);
}
