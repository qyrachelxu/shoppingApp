package com.beaconfire.housingservice.service;

import com.beaconfire.housingservice.dao.HousingDAO;
import com.beaconfire.housingservice.domain.FacilityInput;
import com.beaconfire.housingservice.domain.HouseInput;
import com.beaconfire.housingservice.domain.ReportRequest;
import com.beaconfire.housingservice.domain.entity.*;
import com.beaconfire.housingservice.repository.FacilityReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.awt.print.Pageable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.codehaus.groovy.runtime.DefaultGroovyMethods.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HousingServiceTest {
    @InjectMocks
    HousingService housingService;
    @Mock
    private FacilityReportRepository reportRepository;
    @Mock
    HousingDAO housingDAO;

    Landlord mocklandlord;
    List<House> houses =new ArrayList<>();
    House house1;
    House house2;
    Facility facility;

    @BeforeEach
    public void setUp() {
        mocklandlord = Landlord.builder()
                .id(134)
                .firstname("Sabella")
                .lastname("Su")
                .email("sabellaSu@example.com")
                .cellphone("123-456-7890")
                .build();

        house1 = House.builder()
                .id(1)
                .address("999 swe St")
                .maxOccupant(2)
                .landlord(mocklandlord)
                .build();
        house2 = House.builder()
                .id(2)
                .address("456 Oak Ave")
                .maxOccupant(4)
                .landlord(mocklandlord)
                .build();
        facility = Facility.builder()
                .id(13)
                .type("Washer/Dryer")
                .quantity(2)
                .house(house1)
                .description("Test description")
                .build();
        houses.add(house1);
        houses.add(house2);
        mocklandlord.setHouseList(houses);

    }

    @Test
    public void testGetAllHousesByLandlordId() {
        Integer landlordId = 134;

        when(housingDAO.getAllHousesByLandlordId(landlordId)).thenReturn(houses);
        List<House> result = housingService.getAllHousesByLandlordId(landlordId);
        assertEquals(houses, result);
    }

    @Test
    public void testGetAddressByHouseId() {
        Integer houseId = 1;
        String expectedAddress = "999 Arrow St";

        when(housingDAO.getAddressByHouseId(houseId)).thenReturn(expectedAddress);

        String result = housingService.getAddressByHouseId(houseId);

        assertEquals(expectedAddress, result);
    }

    @Test
    public void testAddNewFacilityReport() {
        ReportRequest report = new ReportRequest();
        String employeeId = "123";
        Facility facility = new Facility();

        housingService.addNewFacilityReport(report, employeeId, facility);

        verify(housingDAO).addNewFacilityReport(report, employeeId, facility);
    }

    @Test
    public void testGetFacilityByFacilityId() {
        Integer facilityId = 1;
        Facility expectedFacility = new Facility();

        when(housingDAO.getFacilityByFacilityId(facilityId)).thenReturn(expectedFacility);

        Facility result = housingService.getFacilityByFacilityId(facilityId);

        assertEquals(expectedFacility, result);
    }

    @Test
    public void testGetFacilityReportsByHouseId() {
        Integer houseId = 1;
        List<FacilityReport> expectedReports = new ArrayList<>();

        when(housingDAO.getAllFacilityReportsByHouseId(houseId)).thenReturn(expectedReports);

        List<FacilityReport> result = housingService.getFacilityReportsByHouseId(houseId);

        assertEquals(expectedReports, result);
    }

    @Test
    public void testGetAllHouses() {
        List<House> expectedHouses = new ArrayList<>();

        when(housingDAO.getAllHouses()).thenReturn(expectedHouses);

        List<House> result = housingService.getAllHouses();

        assertEquals(expectedHouses, result);
    }


//    @Test
//    public void testAddCommentToFacilityReport() {
//        // given
//        String comment = "testComment";
//        String employeeId = "testEmployee";
//        Integer facilityReportId = 1;
//        FacilityReport facilityReport = FacilityReport.builder().id(facilityReportId).build();
//        FacilityReportDetail expectedFrd = FacilityReportDetail.builder()
//                .comment(comment)
//                .employeeId(employeeId)
//                .facilityReport(facilityReport)
//                .build();
//        when(housingDAO.getFacilityReportById(facilityReportId)).thenReturn(facilityReport);
//
//        // when
//        FacilityReportDetail result = housingService.addCommentToFacilityReport(comment, employeeId, facilityReportId);
//
//        // then
//        assertEquals(expectedFrd, result);
//        assertTrue(facilityReport.getFacilityReportDetails().contains(expectedFrd));
//    }

    @Test
    public void testUpdateCommentToFacilityReport() {
        // given
        Integer frdId = 1;
        String comment = "Updated comment";
        FacilityReportDetail frd = FacilityReportDetail.builder().id(frdId).comment("Original comment").build();
        when(housingDAO.getFacilityReportDetailById(frdId)).thenReturn(frd);

        // when
        FacilityReportDetail result = housingService.updateCommentToFacilityReport(comment, frdId);

        // then
        assertEquals(comment, result.getComment());
        verify(housingDAO).getFacilityReportDetailById(frdId);
    }

    @Test
    public void testCheckFacilityHouseMatch() {
        // given
        Integer houseId = 1;
        Integer facilityId = 1;
        List<Integer> facilityIds = new ArrayList<>();
        facilityIds.add(facilityId);
        when(housingDAO.getFacilityIdsByHouseId(houseId)).thenReturn(facilityIds);

        // when
        Boolean result = housingService.checkFacilityHouseMatch(houseId, facilityId);

        // then
        assertTrue(result);
    }

    @Test
    public void testUpdateFacilityReportStatus() {
        // given
        Integer frId = 1;
        String status = "Test status";

        // when
        housingService.updateFacilityReportStatus(status, frId);

        // then
        verify(housingDAO).updateFacilityReportStatus(status, frId);
    }
//    @Test
//    public void testGetReportsByPage() {
//        // given
//        Integer pageNumber = 1;
//        Integer pageSize = 10;
//        Integer houseId = 1;
//        Page<FacilityReport> expectedPage = new PageImpl<>(Collections.singletonList(new FacilityReport()),
//                PageRequest.of(pageNumber, pageSize), 1);
//
//        when(reportRepository.findAllByOrderByCreatedTimeDesc(true, eq(houseId)))
//                .thenReturn(expectedPage);
//
//        // when
//        Page<FacilityReport> resultPage = housingService.getReportsByPage(pageNumber, pageSize, houseId);
//        // then
//        assertEquals(expectedPage,resultPage);
//
//    }

    @Test
    public void testGetNumberOfFacilityByHouseId() {
        // given
        Integer houseId = 1;
        String type = "Washer/Dryer";
        Integer expectedCount = 2;
        when(housingDAO.getNumberOfFacilityByHouseId(eq(houseId), eq(type)))
                .thenReturn(expectedCount);

        // when
        Integer resultCount = housingService.getNumberOfFacilityByHouseId(houseId, type);

        // then
        assertEquals(expectedCount,resultCount);

    }

    @Test
    public void testAddNewHouseWithExistingLandlord() {
        // given
        HouseInput houseRequest = HouseInput
                .builder()
                .build();
        houseRequest.setAddress("123 Main St");
        houseRequest.setMaxOccupant(3);
        houseRequest.setLandlord(mocklandlord);
        houseRequest.setFacilityInputList(Collections.singletonList(
                FacilityInput.builder()
                        .type("Air Conditioning")
                        .quantity(1)
                        .description("Test description")
                        .build()));
        when(housingDAO.getLandlordByEmail(eq(mocklandlord.getEmail())))
                .thenReturn(mocklandlord);
        when(housingDAO.addNewHouse(eq(mocklandlord), eq(houseRequest.getAddress()), eq(houseRequest.getMaxOccupant())))
                .thenReturn(3);
        when(housingDAO.getHouseByHouseId(eq(3)))
                .thenReturn(house1);

        // when
        House resultHouse = housingService.addNewHouse(houseRequest);

        // then
        assertEquals(house1,resultHouse);
    }


}
