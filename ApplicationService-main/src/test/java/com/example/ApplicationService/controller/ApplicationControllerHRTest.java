package com.example.ApplicationService.controller;


import aj.org.objectweb.asm.TypeReference;
import com.example.ApplicationService.domain.DigitalDocument;
import com.example.ApplicationService.domain.WorkFlow;
import com.example.ApplicationService.response.DigitalDocumentResponse;
import com.example.ApplicationService.service.ApplicationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApplicationControllerHR.class)
public class ApplicationControllerHRTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ApplicationService applicationService;

    @Mock
    ApplicationService applicationServiceBean;
    @MockBean
    private HttpServletRequest request;

    @InjectMocks
    ApplicationControllerHR applicationControllerHR;


    @Test
    public void testViewAllFile() throws Exception {
        // Create mock data for the applicationService
        List<DigitalDocument> mockAllDocuments = new ArrayList<>();
        DigitalDocument document1 = new DigitalDocument();
        document1.setDocumentType("Type1");
        document1.setDocumentIsRequired(false);
        document1.setDocumentDescription("Description1");
        document1.setDocumentTitle("Title1");
        DigitalDocument document2 = new DigitalDocument();
        document2.setDocumentType("Type2");
        document2.setDocumentIsRequired(true);
        document2.setDocumentDescription("Description2");
        document2.setDocumentTitle("Title2");
        mockAllDocuments.add(document1);
        mockAllDocuments.add(document2);
        when(applicationService.getAllDocumentView()).thenReturn(mockAllDocuments);

        List<DigitalDocument> mockUserDocuments = new ArrayList<>();
        DigitalDocument document3 = new DigitalDocument();
        document3.setDocumentType("Type3");
        document3.setDocumentIsRequired(true);
        document3.setDocumentDescription("Description3");
        document3.setDocumentTitle("Title3");
        mockUserDocuments.add(document3);
        when(applicationService.getUserDocumentByID(Mockito.anyInt())).thenReturn(mockUserDocuments);

        // Send a GET request to the "/application/viewall" endpoint
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/hr/application/viewall")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        // Verify that the response status is OK
        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
    }

    @Test
    public void testViewAllApplicationHR() throws Exception {
        List<WorkFlow> mockWorkFlows = new ArrayList<>();
        Timestamp a = new Timestamp(123);
        Timestamp b = new Timestamp(123);
        Timestamp c = new Timestamp(123);
        Timestamp d = new Timestamp(123);

        mockWorkFlows.add(new WorkFlow(1, 1,a , b, "Pending", ""));
        mockWorkFlows.add(new WorkFlow(2, 2, b, c, "Approved", ""));

        when(applicationService.getAllApplication()).thenReturn(mockWorkFlows);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/hr/application/viewallapplication")
                .accept(MediaType.APPLICATION_JSON)).andReturn();

        // Verify that the response status is OK
        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);

        verify(applicationService).getAllApplication();
    }

}
