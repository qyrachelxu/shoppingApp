package com.beaconfire.housingservice.dao;

import com.beaconfire.housingservice.domain.ReportRequest;
import com.beaconfire.housingservice.domain.entity.Facility;
import com.beaconfire.housingservice.domain.entity.FacilityReport;
import com.beaconfire.housingservice.domain.entity.House;
import com.beaconfire.housingservice.domain.entity.Landlord;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.codehaus.groovy.runtime.DefaultGroovyMethods.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ActiveProfiles(value = "test")
@SpringBootTest
public class HousingDAOTest {

    @Autowired
    private HousingDAO housingDAO;

    @Mock
    private Session session;

    @Mock
    private SessionFactory sessionFactory;

    private Landlord landlord;
    private List<House> houses =new ArrayList<>();
    private House house1;
    private House house2;
    private Facility facility;

    @BeforeEach
    void setUp() {
        landlord = Landlord.builder()
                .id(134)
                .firstname("Sabella")
                .lastname("Su")
                .email("johndoe@example.com")
                .cellphone("123-456-7890")
                .build();

        house1 = House.builder()
                .id(1)
                .address("123 Main St")
                .maxOccupant(2)
                .landlord(landlord)
                .build();
        house2 = House.builder()
                .id(2)
                .address("456 Oak Ave")
                .maxOccupant(4)
                .landlord(landlord)
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
        landlord.setHouseList(houses);

    }
    @Test
    @Transactional
    public void testGetAllHousesByLandlordIdSuccess(){
        Integer landlordId = 1;
        List<House> houseList = housingDAO.getAllHousesByLandlordId(landlordId);
        assertEquals(landlordId, houseList.get(0).getLandlord().getId());
    }
    @Test
    @Transactional
    public void getAddressByHouseIdSuccess(){
        Integer houseId = 1;
        String address = housingDAO.getAddressByHouseId(houseId);
        assertEquals("2088 E Lakeshore Dr, Lake " +
                "Elsinore, CA",address);
    }

    @Test
    @Transactional
    public void getFacilityByFacilityIdSuccess(){
        Integer facilityId = 1;
        String description = housingDAO.getFacilityByFacilityId(facilityId).getDescription();
        assertEquals("A stainless steel refrigerator with side-by-side doors and an ice maker", description);
    }

    @Test
    @Transactional
    public void getAllFacilityReportsByHouseIdSuccess(){
        Integer houseId = 1;
        List<FacilityReport> facilityReports = housingDAO.getAllFacilityReportsByHouseId(houseId);
        assertEquals(true,facilityReports.size()>0);
        assertEquals(houseId,facilityReports.get(0).getFacility().getHouse().getId());



    }

    @Test
    @Transactional
    public void getAllHousesSuccess(){
        Integer resultLength = housingDAO.getAllHouses().size();
        assertEquals(true,resultLength>0);
    }

    @Test
    @Transactional
    public void getFacilityReportByIdSuccess(){
        Integer facilityReportId = 1;
        assertEquals(facilityReportId, housingDAO.getFacilityReportById(facilityReportId).getId());

    }

    @Test
    @Transactional
    public void testGetHouseByHouseId() {
        // create a mock house object
        House mockHouse = new House();
        mockHouse.setId(1);
        mockHouse.setAddress("2088 E Lakeshore Dr, Lake Elsinore, CA");
        mockHouse.setLandlord(new Landlord());

        // mock the behavior of the getCurrentSession method
        when(sessionFactory.getCurrentSession()).thenReturn(session);

        // mock the behavior of the get method of the session object
        when(session.get(House.class, 1)).thenReturn(mockHouse);

        // call the method being tested
        House result = housingDAO.getHouseByHouseId(1);

        // assert that the result matches the mock house object
        assertEquals(mockHouse.getAddress(), result.getAddress());
    }









}
