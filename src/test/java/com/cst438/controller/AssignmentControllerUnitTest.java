package com.cst438.controller;

import com.cst438.dto.GradeDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@SpringBootTest
public class AssignmentControllerUnitTest {

    @Autowired
    MockMvc mvc;

    // Unit test to grade invalid assignment
    @Test
    public void getAssignmentGradesThrowsExceptionWithBadId() throws Exception {

        MockHttpServletResponse response;

        // issue the GET request
        response = mvc.perform(
                        MockMvcRequestBuilders
                                .get("/assignments/5/grades") // Give an assignmentId that does not exist
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        // response should be 404, NOT_FOUND
        assertEquals(404, response.getStatus());

        // check the expected error message
        String message = response.getErrorMessage();
        assertEquals("assignment not found ", message);
    }

    // Unit test to grade assignment
    @Test
    public void gradeAssignmentWithValidId() throws Exception {

        MockHttpServletResponse responseGet;
        MockHttpServletResponse responsePut;

        // issue the GET request
        responseGet = mvc.perform(
                        MockMvcRequestBuilders
                                .get("/assignments/1/grades") // Give an assignmentId that does exist
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        // check the response code for 200 meaning OK
        assertEquals(200, responseGet.getStatus());

        // return data converted from String
        List<GradeDTO> result = fromJsonString(responseGet.getContentAsString(), List.class);

        // issue the PUT request
        responsePut = mvc.perform(
                        MockMvcRequestBuilders
                                .put("/grades")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(result)))
                .andReturn()
                .getResponse();

        // check the response code for 200 meaning OK
        assertEquals(200, responsePut.getStatus());
    }


    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T  fromJsonString(String str, Class<T> valueType ) {
        try {
            return new ObjectMapper().readValue(str, valueType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
