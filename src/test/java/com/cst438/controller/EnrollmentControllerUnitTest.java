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
import java.util.ArrayList;
import java.util.List;
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

        response = mvc.perform(
                MockMvcRequestBuilders
                        .get("/sections/8/enrollments")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // check the response code for 200 meaning OK
        assertEquals(200, response.getStatus());

        List<EnrollmentDTO> result = fromJsonString(response.getContentAsString(), List.class);

        // gets original data from database before updating it
        Enrollment e1 = enrollmentRepository.findById(2).orElse(null);

        // update grade to "A"
        result.add(new EnrollmentDTO(
                2, "A", 3, "thomas edison", "Introduction to Database", "tedison@csumb.edu", "cst363",
                1, 8, "052", "104", "M W 10:00-11:50", 4, 2024, "Spring"));


        // issue the PUT request for result; updating grade to "A"
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .put("/enrollments")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(result)))
                .andReturn().getResponse();

        // check the response code for 200 meaning OK
        assertEquals(200, response.getStatus());

        // check the database for expected value
        Enrollment e = enrollmentRepository.findById(2).orElse(null);
            assertNotNull(e);
            assertEquals("A", e.getGrade());

        // clean up after test
        // place original grade back
        List<EnrollmentDTO> cleanUp =  new ArrayList<>();
        cleanUp.add(new EnrollmentDTO(
                2, e1.getGrade(), 3, "thomas edison", "Introduction to Database", "tedison@csumb.edu", "cst363",
                1, 8, "052", "104", "M W 10:00-11:50", 4, 2024, "Spring"));

        // issue the PUT request
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .put("/enrollments")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(cleanUp)))
                .andReturn().getResponse();

        // check the response code for 200 meaning OK
        assertEquals(200, response.getStatus());

        // check the database to ensure grade has been return to original grade
        for (EnrollmentDTO clean : cleanUp){
            Enrollment enroll = enrollmentRepository.findById(clean.enrollmentId()).orElse(null);
            assertNotNull(enroll);
            assertEquals(clean.grade(), enroll.getGrade());
        }
    }
}
