package com.beaconfire.housingservice.service;

import com.beaconfire.housingservice.dao.HousingDAO;
import com.beaconfire.housingservice.domain.HouseInput;
import com.beaconfire.housingservice.domain.ReportRequest;
import com.beaconfire.housingservice.domain.entity.*;
import com.beaconfire.housingservice.repository.FacilityReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;


@Service
public class HousingService {

    private final HousingDAO housingDAO;

    private final FacilityReportRepository reportRepository;

   @Autowired
    public HousingService(HousingDAO housingDAO, FacilityReportRepository facilityReportRepository) {
        this.housingDAO = housingDAO;
        this.reportRepository = facilityReportRepository;
    }

//    @Autowired
//    public void setHousingDAO(HousingDAO housingDAO) {
//        this.housingDAO = housingDAO;
//    }

    @Transactional
    public List<House> getAllHousesByLandlordId(Integer landlordId){
        return housingDAO.getAllHousesByLandlordId(landlordId);
    }

    @Transactional
    public String getAddressByHouseId(Integer houseId) {
        return housingDAO.getAddressByHouseId(houseId);
    }
    @Transactional
    public void addNewFacilityReport(ReportRequest report, String employeeId, Facility facility) {
        housingDAO.addNewFacilityReport(report,employeeId, facility);
    }

    @Transactional
    public Facility getFacilityByFacilityId(Integer facilityId){
        return housingDAO.getFacilityByFacilityId(facilityId);
    }
    @Transactional
    public List<FacilityReport> getFacilityReportsByHouseId(Integer houseId) {
        return housingDAO.getAllFacilityReportsByHouseId(houseId);

    }
    @Transactional
    public List<House> getAllHouses() {
        return housingDAO.getAllHouses();
    }

    @Transactional
    public Page<FacilityReport> getReportsByPage(Integer pageNumber, Integer pageSize, Integer houseId) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdTime").descending());
        return reportRepository.findAllByOrderByCreatedTimeDesc(pageable,houseId);
    }
    @Transactional
    public Integer getNumberOfFacilityByHouseId(Integer houseId, String type) {
        return housingDAO.getNumberOfFacilityByHouseId(houseId,type);
    }
    @Transactional
    public House addNewHouse(HouseInput houseRequest) {
        Landlord landlord;
        landlord = housingDAO.getLandlordByEmail(houseRequest.getLandlord().getEmail());
        if(landlord == null) {
            //add landlord
            Integer newLandlordId = housingDAO.addNewLandlord(houseRequest.getLandlord());
            landlord = housingDAO.getLandlordByLandlordId(newLandlordId);
        }

        Integer newHouseId = housingDAO.addNewHouse(landlord,houseRequest.getAddress(),houseRequest.getMaxOccupant());
        House newHouse=housingDAO.getHouseByHouseId(newHouseId);
        houseRequest.getFacilityInputList()
                    .forEach(facilityInput -> housingDAO.addNewFacility(
                            Facility.builder()
                                    .house(newHouse)
                                    .description(facilityInput.getDescription())
                                    .type(facilityInput.getType())
                                    .quantity(facilityInput.getQuantity())
                                    .build()));
            return newHouse;

    }
    @Transactional
    public void deleteHouse(Integer houseId) {
        House house = housingDAO.getHouseByHouseId(houseId);
        housingDAO.deleteHouseByHouseId(house);
    }
    @Transactional
    public FacilityReportDetail addCommentToFacilityReport(String comment, String employeeId, Integer facilityReportId) {
        FacilityReport facilityReport = housingDAO.getFacilityReportById(facilityReportId);
        FacilityReportDetail frd = FacilityReportDetail.builder()
                .facilityReport(facilityReport)
                .comment(comment)
                .employeeId(employeeId)
                .build();
        facilityReport.getFacilityReportDetails().add(frd);
        return frd;
//        Integer addedFrdId= housingDAO.addNewFacilityReportDetail(frd);
//        FacilityReportDetail addedFrd = housingDAO.getFacilityReportDetailById(addedFrdId);
//        return addedFrd;
    }
    @Transactional
    public  FacilityReportDetail updateCommentToFacilityReport(String comment, Integer frdId) {
        FacilityReportDetail frd = housingDAO.getFacilityReportDetailById(frdId);
        frd.setComment(comment);
        return frd;

    }
    @Transactional
    public Boolean checkFacilityHouseMatch(Integer houseId, Integer facilityId) {
        List<Integer> facilityIds = housingDAO.getFacilityIdsByHouseId(houseId);
        return facilityIds != null && facilityIds.contains(facilityId);


    }
    @Transactional
    public void updateFacilityReportStatus(String status, Integer frId) {
        housingDAO.updateFacilityReportStatus(status,frId);
    }

    @Transactional
    public String getFacilityReportDetailByFrdId(Integer frdId) {
       String employeeId = housingDAO.getFacilityReportDetailById(frdId).getEmployeeId();
       return employeeId;
    }
}
