package com.cst438.controller;

import com.cst438.dto.AssignmentDTO;
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
import static com.cst438.test.utils.TestUtils.fromJsonString;
import static com.cst438.test.utils.TestUtils.asJsonString;

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

    @Test
    public void newAssignmentSuccess() throws Exception {
        AssignmentDTO test = new AssignmentDTO(0, "yessir", "2024-03-15", "cst363", 1, 8);

        MockHttpServletResponse res = mvc.perform(MockMvcRequestBuilders.post("/assignments")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(test)))
            .andReturn()
            .getResponse();

        AssignmentDTO returned = fromJsonString(res.getContentAsString(), AssignmentDTO.class);

        // doesn't require a message assert because it returns a dto if successful
        assertEquals(200, res.getStatus());
        assertNotEquals(test.id(), returned.id());
        assertEquals(test.title(), returned.title());
        assertEquals(test.dueDate(), returned.dueDate());
        assertEquals(test.courseId(), returned.courseId());
        assertEquals(test.secId(), returned.secId());
        assertEquals(test.secNo(), returned.secNo());
    }

    @Test
    public void newAssignmentPastEndDate() throws Exception {
        // end date is 2024-05-17
        AssignmentDTO test = new AssignmentDTO(0, "yessir2", "2024-05-18", "cst363", 1, 8);

        MockHttpServletResponse res = mvc.perform(MockMvcRequestBuilders.post("/assignments")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(test)))
            .andReturn()
            .getResponse();

        assertEquals(400, res.getStatus());
        assertEquals("Bad date", res.getErrorMessage());
    }

    @Test
    public void newAssignmentInvalidSecNo() throws Exception {
        // section number should be 8 and not 911
        AssignmentDTO test = new AssignmentDTO(0, "yessir3", "2024-03-15", "cst363", 1, 911);

        MockHttpServletResponse res = mvc.perform(MockMvcRequestBuilders.post("/assignments")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(test)))
            .andReturn()
            .getResponse();

        assertEquals(400, res.getStatus());
        assertEquals("Bad section number", res.getErrorMessage());
    }
}