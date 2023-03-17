package com.beaconfire.housingservice.controller;

import com.beaconfire.housingservice.domain.ReportRequest;
import com.beaconfire.housingservice.domain.entity.*;
import com.beaconfire.housingservice.service.HousingService;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HousingController.class)
public class HousingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    HousingService housingService;

    @Test
    public void testGetAddressByHouseId_success() throws Exception {
        Integer houseId = 1;
        String address = "123 Main St";

        // Mock the behavior of the housingService.getAddressByHouseId() method
        when(housingService.getAddressByHouseId(houseId)).thenReturn(address);

        // Perform the GET request
        MvcResult result = mockMvc.perform(get("/housing/{houseId}/address", houseId))
                .andExpect(status().isOk())
                .andReturn();
//        // Verify that the response is correct
        assertEquals(address,result.getResponse().getContentAsString());

    }

    @Test
    public void testGetAddressByHouseId_notFound() throws Exception {
        Integer houseId = 1;
        when(housingService.getAddressByHouseId(houseId)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/houses/" + houseId + "/address"))
                .andExpect(status().isNotFound());
    }
//
    @Test
    public void testCreateFacilityReport_success() throws Exception {

        ReportRequest report = new ReportRequest();
        String employeeId = "123";
        Integer facilityId = 1;
        Facility facility = new Facility();
        when(housingService.getFacilityByFacilityId(facilityId)).thenReturn(facility);

        mockMvc.perform(MockMvcRequestBuilders.post("/housing/employee/" + employeeId + "/facility/" + facilityId + "/report")
                        .content(new Gson().toJson(report))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testViewExistingReportsByHouseId_success() throws Exception {
        // Define test data and expectations
        Integer houseId = 1;
        FacilityReport facilityReport = new FacilityReport();
        facilityReport.setTitle("Report");
        facilityReport.setDescription("report test");
        facilityReport.setEmployeeId("123");
        facilityReport.setStatus("Open");

        List<FacilityReportDetail> facilityReportDetails = new ArrayList<>();
        FacilityReportDetail facilityReportDetail = new FacilityReportDetail();
        facilityReportDetail.setComment("comment");
        facilityReportDetail.setEmployeeId("123");
        facilityReportDetails.add(facilityReportDetail);
        facilityReport.setFacilityReportDetails(facilityReportDetails);
        List<FacilityReport> facilityReports = Arrays.asList(facilityReport);
        when(housingService.getFacilityReportsByHouseId(houseId)).thenReturn(facilityReports);

        // Call the controller method under test
        mockMvc.perform(get("/housing/view/house/{houseId}/reports", houseId))
                .andExpect(status().isOk());
    }

    @Test
    public void testViewAllHouses() throws Exception {
        // Set up mock data
        List<House> houses = new ArrayList<>();
        houses.add(House.builder().landlord(Landlord.builder().build()).build());
//        houses.add(new House(2, "456 Oak St", new Landlord(2, "Jane", "Smith", "jsmith@example.com", "987-654-3210")));
        when(housingService.getAllHouses()).thenReturn(houses);

        // Make request and check response
        mockMvc.perform(get("/housing/hr/view/houses"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetFacilityInfo() throws Exception {
        Integer houseId = 1;
        Integer pageNumber = 0;
        Integer pageSize = 3;
        Integer numberOfBeds = 2;
        Integer numberOfMattress = 3;
        Integer numberOfTables = 1;
        Integer numberOfChairs = 4;
        List<FacilityReport> reports = Arrays.asList(
                FacilityReport.builder().build()
        );
        Page<FacilityReport> page = new PageImpl<>(reports);
        when(housingService.getNumberOfFacilityByHouseId(houseId, "Bed")).thenReturn(numberOfBeds);
        when(housingService.getNumberOfFacilityByHouseId(houseId, "Mattress")).thenReturn(numberOfMattress);
        when(housingService.getNumberOfFacilityByHouseId(houseId, "Table")).thenReturn(numberOfTables);
        when(housingService.getNumberOfFacilityByHouseId(houseId, "Chair")).thenReturn(numberOfChairs);
        when(housingService.getReportsByPage(pageNumber, pageSize, houseId)).thenReturn(page);

        mockMvc.perform(get("/housing/hr/view/house/{houseId}/facilityInfo?pageNumber={pageNumber}&pageSize={pageSize}", houseId, pageNumber, pageSize))
                .andExpect(status().isOk());
    }


}
