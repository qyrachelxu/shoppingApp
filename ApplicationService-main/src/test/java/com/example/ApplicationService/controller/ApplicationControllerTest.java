package com.example.ApplicationService.controller;

import static org.assertj.core.api.Assertions.assertThat;
import com.example.ApplicationService.domain.DigitalDocument;
import com.example.ApplicationService.domain.WorkFlow;
import com.example.ApplicationService.exception.ApplicationExistException;
import com.example.ApplicationService.response.DigitalDocumentResponse;
import com.example.ApplicationService.service.ApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApplicationController.class)
public class ApplicationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ApplicationService applicationService;

    @Mock
    ApplicationService applicationService2;
    @MockBean
    private HttpServletRequest request;

    @InjectMocks
    ApplicationController applicationController;

    private HashSet<String> possibleStatus = new HashSet<>();
    private HashSet<String> possibleDocTypes = new HashSet<>();

    @BeforeEach
    public void setup() {
        this.possibleStatus.add("Pending");
        this.possibleStatus.add("Rejected");
        this.possibleStatus.add("Approved");
        this.possibleStatus.add("Incomplete");

        this.possibleDocTypes.add("Contract");
        this.possibleDocTypes.add("Information");
    }

    @Test
    public void testTest() throws Exception {
        mockMvc.perform(get("/employee/application/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("test application service"));
    }

    @Test
    public void testCheckApplicationStatus() throws Exception {
        Integer userID = 99;
        String applicationStatus = "Pending";

        when(applicationService.checkApplication(userID)).thenReturn(applicationStatus);

        mockMvc.perform(MockMvcRequestBuilders.get("/employee/application/applicationstatus"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void testAddApplicationSuccess() throws Exception {

        when(applicationService.getApplicaionByUserId(99)).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.post("/employee/application/addapplication"))
                .andExpect(status().isOk()).andExpect(content().string("Go To Application Forum!"));

        verify(applicationService, times(1)).addApplication(99);
    }

    @Test
    public void testAddApplicationFail() throws Exception {
        WorkFlow testworkFlow = new WorkFlow();
        testworkFlow.setUserId(99);
        testworkFlow.setWorkflowStatus("XXX");
        when(applicationService.getApplicaionByUserId(4)).thenReturn(Optional.of(testworkFlow));
        mockMvc.perform(MockMvcRequestBuilders.post("/employee/application/addapplication"))
                .andExpect(status().isOk());

        verify(applicationService, times(0)).addApplication(123);
    }

    @Test
    public void testSubmitApplicationSuccess() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.put("/employee/application/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("submit successfully! Go To DigitalDocument Page and Submit the Required document"));

        verify(applicationService, times(1)).submitApplication(99);
    }
    @Test
    public void testViewAllFile() throws Exception {
        List<DigitalDocument> allDocuments = new ArrayList<>();
        allDocuments.add(new DigitalDocument(1, "Contract", true, "xxx.pdf", "HR Version", "Contract_origin.pdf"));
        List<DigitalDocument> userDocuments = new ArrayList<>();
        userDocuments.add(new DigitalDocument(2, "Contract", true, "xxxx.pdf", "User Edition", "Contract_id2.pdf"));
        Integer userId = 99;

        when(request.getAttribute("userId")).thenReturn(userId);
        when(applicationService.getAllDocumentView()).thenReturn(allDocuments);
        when(applicationService.getUserDocumentByID(userId)).thenReturn(userDocuments);

        mockMvc.perform(MockMvcRequestBuilders.get("/employee/application/viewall"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        // assert the response

    }

    @Test
    public void testallDocumentType() throws Exception {

        List<DigitalDocument> allDocuments = new ArrayList<>();
        allDocuments.add(new DigitalDocument(1, "Contract", true, "xxx.pdf", "HR Version", "Contract_origin.pdf"));

        when(applicationService2.getAllDocumentView()).thenReturn(allDocuments);
        HashSet<String> res = applicationController.allDocumentType();
        assertThat(res).hasSize(1);
    }
}
