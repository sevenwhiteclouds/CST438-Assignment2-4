package com.cst438.controller;

import com.cst438.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.sql.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
@SpringBootTest
class StudentControllerUnitTests {

    @Autowired
    MockMvc mvc;

    @MockBean
    SectionRepository sectionRepository;

    @MockBean
    EnrollmentRepository enrollmentRepository;

    @MockBean
    UserRepository userRepository;

    @Test // student enrolls into a section
    void studentEnrollment() throws Exception {

        /*
        This test needs complete isolation due to the data's unpredictability.
        Not only will future database changes break tests but terms have set
        add/drop deadlines. Using the current DB (as of 3/25/2024) will cause
        this test to fail **within 1 month of implementation**
        */

        // Create all necessary mock data
        Term mockTerm = createMockTerm();
        Course mockCourse = createMockCourse();
        Section mockSection = createMockSection(mockCourse, mockTerm);
        User mockStudent = createMockStudent();
        Enrollment mockEnrollment = createMockEnrollment(mockSection, mockStudent);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(mockStudent));

        MockHttpServletResponse response;
        int studentId = userRepository.findById(0).get().getId();

        assertEquals(mockStudent.getId(), studentId);

        int secId = mockSection.getSecId();

        when(sectionRepository.findById(anyInt())).thenReturn(Optional.of(mockSection));
        when(enrollmentRepository.save(any())).thenReturn(mockEnrollment);

        response = mvc.perform(
                MockMvcRequestBuilders
                    .post("/enrollments/sections/{secId}?studentId={studentId}", secId, studentId)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();

        assertEquals(200, response.getStatus());
    }


    @Test // student attempts to enroll in a section but fails because the student is already enrolled
    void studentAlreadyEnrolled() throws Exception {


    }

    @Test // student attempts to enroll in a section but the section number is invalid
    void studentEnrollmentInvalidSection() throws Exception {
        MockHttpServletResponse response;
        User mockStudent = createMockStudent();

        response = mvc.perform(
                MockMvcRequestBuilders
                    .post("/enrollments/sections/{secId}?studentId={studentId}", -1, mockStudent.getId())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();

        assertEquals(404, response.getStatus());
        String message = response.getErrorMessage();

        assertEquals("This section does not exist.", message);
    }

    // TODO: Complete this unit test
    @Test // student attempts to enroll in a section, but it is past the add deadline
    void studentEnrollmentPastDeadline() throws Exception {

    }

    private Term createMockTerm() {
        Term mockTerm = new Term();
        mockTerm.setYear(2024);
        mockTerm.setSemester("Spring");
        mockTerm.setAddDate(Date.valueOf("2000-01-01"));
        mockTerm.setAddDeadline(Date.valueOf("2100-01-01"));
        mockTerm.setDropDeadline(Date.valueOf("2200-01-01"));
        mockTerm.setStartDate(Date.valueOf("2100-01-05"));
        mockTerm.setEndDate(Date.valueOf("2200-01-05"));

        return mockTerm;
    }

    private Course createMockCourse() {
        Course mockCourse = new Course();
        mockCourse.setCourseId("cst000");
        mockCourse.setTitle("Typewriting 101");
        mockCourse.setCredits(3);

        return mockCourse;
    }

    private Section createMockSection(Course mockCourse, Term mockTerm) {
        Section mockSection = new Section();
        mockSection.setCourse(mockCourse);
        mockSection.setTerm(mockTerm);
        mockSection.setSecId(1);
        mockSection.setBuilding("202");
        mockSection.setRoom("12");
        mockSection.setTimes("M W 2:00-4:00 pm");
        mockSection.setInstructor_email("everg@csumb.edu");

        return mockSection;
    }

    private User createMockStudent() {
        User mockStudent = new User();
        mockStudent.setType("STUDENT");
        mockStudent.setName("TEST USER");
        mockStudent.setEmail("TESTUSER@csumb.edu");
        mockStudent.setPassword("Password");

        return mockStudent;
    }

    private Enrollment createMockEnrollment(Section mockSection, User user) {
        Enrollment mockEnrollment = new Enrollment();
        mockEnrollment.setSection(mockSection);
        mockEnrollment.setGrade("NULL");
        mockEnrollment.setUser(user);

        return mockEnrollment;
    }
}