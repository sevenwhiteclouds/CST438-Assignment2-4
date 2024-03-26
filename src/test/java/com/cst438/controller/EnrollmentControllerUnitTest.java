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

        Enrollment e1 = enrollmentRepository.findById(2).orElse(null);
        Enrollment e2 = enrollmentRepository.findById(3).orElse(null);

        // create DTOs with grades set to A
        List<EnrollmentDTO> dto_list =  new ArrayList<>();
        dto_list.add(new EnrollmentDTO(
                2, "A", 3, "thomas edison", "Introduction to Database", "tedison@csumb.edu", "cst363",
                1, 8, "052", "104", "M W 10:00-11:50", 4, 2024, "Spring"));
        dto_list.add(new EnrollmentDTO(
                3, "A", 3, "thomas edison", "Software Engineering", "tedison@csumb.edu", "cst438",
                1, 10, "052", "222", "T Th 12:00-1:50", 4, 2024, "Spring"));

        // issue the PUT request
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .put("/enrollments")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(dto_list)))
                .andReturn().getResponse();

        // check the response code for 200 meaning OK
        assertEquals(200, response.getStatus());

        //return Data converted from String to DTO
        //EnrollmentDTO result = fromJsonString(response.getContentAsString(), EnrollmentDTO.class);

        // check the database for expected value
        for (EnrollmentDTO dto : dto_list){
            Enrollment e = enrollmentRepository.findById(dto.enrollmentId()).orElse(null);
            assertNotNull(e);
            assertEquals(dto.grade(), e.getGrade());
        }

        // clean up after test
        // place original grade back
        List<EnrollmentDTO> cleanUp =  new ArrayList<>();
        cleanUp.add(new EnrollmentDTO(
                2, e1.getGrade(), 3, "thomas edison", "Introduction to Database", "tedison@csumb.edu", "cst363",
                1, 8, "052", "104", "M W 10:00-11:50", 4, 2024, "Spring"));
        cleanUp.add(new EnrollmentDTO(
                3, e2.getGrade(), 3, "thomas edison", "Software Engineering", "tedison@csumb.edu", "cst438",
                1, 10, "052", "222", "T Th 12:00-1:50", 4, 2024, "Spring"));

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

        // check the database for deletion
        for (EnrollmentDTO clean : cleanUp){
            Enrollment enroll = enrollmentRepository.findById(clean.enrollmentId()).orElse(null);
            assertNotNull(enroll);
            assertEquals(clean.grade(), enroll.getGrade());
        }
    }
}
