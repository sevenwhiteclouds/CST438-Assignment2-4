package com.cst438.controller;

import com.cst438.domain.*;
import com.cst438.dto.EnrollmentDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.cst438.test.utils.TestUtils.fromJsonString;
import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@SpringBootTest
class StudentControllerUnitTests {

    @Autowired
    MockMvc mvc;

    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CourseRepository courseRepository;

    // test student ID with clean slate
    static final int studentId = 5;

    @Test // student enrolls into a section
    void studentEnrollment() throws Exception {
        User student = userRepository.findById(studentId).orElse(null);
        Section section = sectionRepository.findById(11).orElse(null);

        assertNotNull(student);
        assertNotNull(section);

        MockHttpServletResponse response;

        response = mvc.perform(
                MockMvcRequestBuilders
                    .post("/enrollments/sections/{secId}?studentId={studentId}", section.getSectionNo(), student.getId())
                    .accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();

        assertEquals(200, response.getStatus());
        EnrollmentDTO result = fromJsonString(response.getContentAsString(), EnrollmentDTO.class);

        assertNotEquals(0, result.enrollmentId());

        assertEquals(section.getCourse().getCourseId(), result.courseId());
        assertEquals(section.getTerm().getSemester(), result.semester());
        assertEquals(section.getSectionNo(), result.sectionNo());

        Enrollment e = enrollmentRepository.findById(result.enrollmentId()).orElse(null);
        assertNotNull(e);

        response = mvc.perform(
                MockMvcRequestBuilders
                    .delete("/enrollments/{eId}", result.enrollmentId()))
            .andReturn()
            .getResponse();

        assertEquals(200, response.getStatus());

        e = enrollmentRepository.findById(result.enrollmentId()).orElse(null);
        assertNull(e);
    }

    @Test // student attempts to enroll in a section but fails because the student is already enrolled
    void studentAlreadyEnrolled() throws Exception {
        User student = userRepository.findById(studentId).orElse(null);
        Section section = sectionRepository.findById(11).orElse(null);

        assertNotNull(student);
        assertNotNull(section);

        MockHttpServletResponse response;

        response = mvc.perform(
                MockMvcRequestBuilders
                    .post("/enrollments/sections/{secId}?studentId={studentId}", section.getSectionNo(), student.getId())
                    .accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();

        assertEquals(200, response.getStatus());
        EnrollmentDTO result = fromJsonString(response.getContentAsString(), EnrollmentDTO.class);

        assertNotEquals(0, result.enrollmentId());

        Enrollment e = enrollmentRepository.findById(result.enrollmentId()).orElse(null);
        assertNotNull(e);

        response = mvc.perform(
                MockMvcRequestBuilders
                    .post("/enrollments/sections/{secId}?studentId={studentId}", section.getSectionNo(), student.getId())
                    .accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();

        assertEquals(404, response.getStatus());
        assertEquals("Student is already enrolled in section " + section.getSectionNo(), response.getErrorMessage());

        response = mvc.perform(
                MockMvcRequestBuilders
                    .delete("/enrollments/{eId}", result.enrollmentId()))
            .andReturn()
            .getResponse();

        assertEquals(200, response.getStatus());

        e = enrollmentRepository.findById(result.enrollmentId()).orElse(null);
        assertNull(e);

    }

    @Test // student attempts to enroll in a section but the section number is invalid
    void studentEnrollmentInvalidSection() throws Exception {
        MockHttpServletResponse response;

        response = mvc.perform(
                MockMvcRequestBuilders
                    .post("/enrollments/sections/{secId}?studentId={studentId}", -1, studentId)
                    .accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();

        assertEquals(404, response.getStatus());
        String message = response.getErrorMessage();

        assertEquals("This section does not exist.", message);
    }

    @Test // student attempts to enroll in a section, but it is past the add deadline
    void studentEnrollmentPastDeadline() throws Exception {
        User student = userRepository.findById(studentId).orElse(null);
        Section section = sectionRepository.findById(5).orElse(null);

        assertNotNull(student);
        assertNotNull(section);

        MockHttpServletResponse response;

        response = mvc.perform(
                MockMvcRequestBuilders
                    .post("/enrollments/sections/{secId}?studentId={studentId}", section.getSectionNo(), student.getId())
                    .accept(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();

        assertEquals(404, response.getStatus());
        assertEquals("The add deadline for this section has passed.", response.getErrorMessage());

    }

}