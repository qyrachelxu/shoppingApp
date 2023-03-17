package com.example.EmployeeService.controller;

import com.example.EmployeeService.domain.*;
import com.example.EmployeeService.dto.application.ApplicationDTO;
import com.example.EmployeeService.dto.application.ApplicationFormDTO;
import com.example.EmployeeService.dto.houseComposite.EmployeeInfo;
import com.example.EmployeeService.dto.profile.ProfileDTO;
import com.example.EmployeeService.response.ApplicationResponse;
import com.example.EmployeeService.service.EmployeeService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean       // this annotation is provided by Spring Boot
    EmployeeService employeeService;

    @Test
    public void testGetEmployeeApplication() throws Exception {

        int userID = 1;
        ApplicationDTO application = new ApplicationDTO();

        when(employeeService.findApplicationByUserID(userID)).thenReturn(application);

        mockMvc.perform(MockMvcRequestBuilders.get("/employee/{userID}/application", userID))
                .andExpect(status().isOk());
    }

    @Test
    public void getOPT_shouldReturnGetOPTResponse() throws Exception {
        int userID = 1;
        Employee employee = new Employee();
        employee.setUserID(userID);
        VisaStatus visaStatus = new VisaStatus();
        visaStatus.setVisaType("F-1 OPT");
        employee.setVisaStatuses(Collections.singletonList(visaStatus));
        PersonalDocument personalDocument1 = new PersonalDocument();
        personalDocument1.setTitle("OPT Receipt");
        personalDocument1.setDocumentStatus("Approved");
        PersonalDocument personalDocument2 = new PersonalDocument();
        personalDocument2.setTitle("OPT EAD");
        personalDocument2.setDocumentStatus("Pending");
        List<PersonalDocument> personalDocuments = Arrays.asList(personalDocument1, personalDocument2);
        employee.setPersonalDocuments(personalDocuments);

        when(employeeService.findEmployeeByUserID(userID)).thenReturn(employee);

        mockMvc.perform(MockMvcRequestBuilders.get("/employee/{userID}/getOPT", userID))
                .andExpect(status().isOk());
    }

    @Test
    public void updateProfile_ValidInput_Success() throws Exception {
        int userId = 1;
        ProfileDTO profileDTO = new ProfileDTO();
        String button = "save";
        boolean decision = true;
        String requestBody = new ObjectMapper().writeValueAsString(profileDTO);

        // Mock the employeeService behavior
        Mockito.doNothing().when(employeeService).updateProfileName(userId, profileDTO);

        // Perform the request and verify the response
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .put("/employee/" + userId + "/updateProfile")
                        .param("button", button)
                        .param("decision", String.valueOf(decision))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }

    @Test
    public void testGetEmployeeByEmployeeId() throws Exception {
        // Set up mock data
        String employeeId = "12345";
        Employee employee = new Employee();
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setCellPhone("555-555-5555");
        employee.setEmail("john.doe@example.com");
        employee.setHouseID(123);
        List<Car> carList = new ArrayList<>();
        carList.add(new Car("Toyota", "Camry", "2018"));
        employee.setCar(carList);

        when(employeeService.findEmployeeByEmployeeId(employeeId)).thenReturn(employee);

        // Perform the request and assert the response
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/employee/" + employeeId))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void testGetPersonalDocuments() throws Exception {

        List<PersonalDocument> personalDocuments = Arrays.asList(new PersonalDocument(), new PersonalDocument());
        when(employeeService.getPersonalDocuments(1)).thenReturn(personalDocuments);

        // create the controller instance and set the employee service mock
//        controller.setEmployeeService(employeeService);

        // perform the request and get the response
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/employee/{userID}/personaldocuments", 1)
                        .header("Authorization", "Bearer some-token"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void testGetEmployeeProfile() throws Exception {

        ProfileDTO profileDTO = new ProfileDTO();
//        profileDTO.setName("John Doe");
        when(employeeService.findProfileByUserID(1)).thenReturn(profileDTO);

        // perform the request and get the response
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/employee/{userID}/profile", 1)
                        .header("Authorization", "Bearer some-token"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void testGetNumOfResidents() throws Exception {

        // create a list of employees to be returned by the mock service
        List<Employee> employees = new ArrayList<>();
        Employee employee1 = new Employee();
        employee1.setFirstName("John");
        employee1.setLastName("Doe");
        employee1.setCellPhone("555-1234");
        employee1.setEmail("john.doe@example.com");
        employee1.setHouseID(1);
        employees.add(employee1);

        Employee employee2 = new Employee();
        employee2.setFirstName("Jane");
        employee2.setLastName("Doe");
        employee2.setCellPhone("555-5678");
        employee2.setEmail("jane.doe@example.com");
        employee2.setHouseID(1);
        employees.add(employee2);

        // set up the mock service to return the list of employees when findResidentsByHouseID is called
        when(employeeService.findResidentsByHouseID(1)).thenReturn(employees);

        // perform the request and get the response
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/employee/house/{houseID}/residents", 1))
                .andExpect(status().isOk())
                .andReturn();
    }
}
