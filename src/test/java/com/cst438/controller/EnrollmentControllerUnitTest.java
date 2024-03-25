package com.cst438.controller;

import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.dto.EnrollmentDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static com.cst438.test.utils.TestUtils.asJsonString;
import static com.cst438.test.utils.TestUtils.fromJsonString;
import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@SpringBootTest
public class EnrollmentControllerUnitTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Test
    public void addGrade() throws Exception{

        MockHttpServletResponse response;

        EnrollmentDTO enrollment = new EnrollmentDTO(
                1,
                "A",
                3,
                "thomas edison",
                "Introduction to Database",
                "tedison@csumb.edu",
                "363",
                1,
                1,
                "052",
                "100",
                "M W 10:00-11:50",
                4,
                2023,
                "Fall");

                response = mvc.perform(
                        MockMvcRequestBuilders
                                .put("/enrollments")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(enrollment)))
                        .andReturn().getResponse();

        assertEquals(200, response.getStatus());

        EnrollmentDTO result = fromJsonString(response.getContentAsString(), EnrollmentDTO.class);
        Enrollment e = enrollmentRepository.findById(result.enrollmentId()).orElse(null);
        assertNotNull(e);
        assertEquals("A", e.getGrade());
    }
}
