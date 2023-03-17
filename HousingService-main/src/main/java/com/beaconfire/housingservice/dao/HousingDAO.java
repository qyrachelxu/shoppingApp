package com.beaconfire.housingservice.dao;

import com.beaconfire.housingservice.domain.ReportRequest;
import com.beaconfire.housingservice.domain.entity.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class HousingDAO {
    @Autowired
    SessionFactory sessionFactory;

    public List<House> getAllHousesByLandlordId(Integer landlordId){
        Session session;
        session = sessionFactory.getCurrentSession();
        String hql = "select distinct h from House h where h.landlord.id =:landlordId";
        Query<House> query = session.createQuery(hql);
        query.setParameter("landlordId", landlordId);
        List<House> houseList = query.getResultList();
        return houseList;
    }


    public String getAddressByHouseId(Integer houseId) {
        Session session;
        session = sessionFactory.getCurrentSession();
        House house = session.get(House.class,houseId);
        return house!=null?house.getAddress():null;
    }

    public Facility getFacilityByFacilityId(Integer facilityId){
        Session session;
        session = sessionFactory.getCurrentSession();
        Facility facility = session.get(Facility.class, facilityId);
        return facility;

    }

    public void addNewFacilityReport(ReportRequest report, String employeeId, Facility facility) {
        Session session;
        session = sessionFactory.getCurrentSession();
        FacilityReport facilityReport = FacilityReport
                .builder()
                .facility(facility)
                .employeeId(employeeId)
                .description(report.getDescription())
                .title(report.getTitle())
                .status("Open")
                .build();
        session.save(facilityReport);
        session.persist(facilityReport);
    }

    public List<FacilityReport> getAllFacilityReportsByHouseId(Integer houseId) {
        Session session;
        session = sessionFactory.getCurrentSession();
        String hql = "select fr from FacilityReport fr left join fetch fr.facilityReportDetails frd where fr.facility.house.id =: houseId group by fr.id";
        Query<FacilityReport> query = session.createQuery(hql);
        query.setParameter("houseId", houseId);
        List<FacilityReport> facilityReports=query.getResultList();
        return facilityReports;
    }

    public List<House> getAllHouses() {
        Session session;
        session = sessionFactory.getCurrentSession();
        String hql = "select h from House h left join fetch h.landlord hl";
        Query<House> query = session.createQuery(hql, House.class);
        return query.getResultList();
    }

    public Integer getNumberOfFacilityByHouseId(Integer houseId, String type) {
        Session session;
        session = sessionFactory.getCurrentSession();
        String hql = "select quantity from Facility f where f.house.id=:houseId and f.type=:type";
        Query<Integer> query = session.createQuery(hql);
        query.setParameter("houseId", houseId);
        query.setParameter("type", type);
        return query.getSingleResult();

    }


    public Landlord getLandlordByEmail(String email) {
        Session session;
        session = sessionFactory.getCurrentSession();
        String hql = "select l from Landlord l where l.email=:email";
        Query<Landlord> query = session.createQuery(hql,Landlord.class);
        query.setParameter("email", email);
        return query.getResultList().isEmpty()? null: query.getResultList().get(0);
    }

    public Integer addNewHouse(Landlord landlord, String address, Integer maxOccupant) {
        Session session;
        session = sessionFactory.getCurrentSession();
        House newHouse = House.builder().address(address).landlord(landlord).maxOccupant(maxOccupant).build();
        Integer newHouseId = (Integer) session.save(newHouse);
        session.persist(newHouse);
        return newHouseId;
    }

    public Integer addNewFacility(Facility facility) {
        Session session;
        session = sessionFactory.getCurrentSession();
        Integer newFacilityId = (Integer) session.save(facility);
        session.persist(facility);
        return newFacilityId;

    }

    public House getHouseByHouseId(Integer id) {
        Session session;
        session = sessionFactory.getCurrentSession();
        return session.get(House.class,id);
    }

    public Integer addNewLandlord(Landlord landlord) {
        Session session;
        session = sessionFactory.getCurrentSession();
        Integer newLandlordId = (Integer) session.save(landlord);
        session.persist(landlord);
        return newLandlordId;
    }

    public Landlord getLandlordByLandlordId(Integer newLandlordId) {
        Session session;
        session = sessionFactory.getCurrentSession();
        return session.get(Landlord.class,newLandlordId);
    }

    public void deleteHouseByHouseId(House house) {
        Session session;
        session = sessionFactory.getCurrentSession();
        session.delete(house);
    }

    public FacilityReport getFacilityReportById(Integer facilityReportId) {
        Session session;
        session = sessionFactory.getCurrentSession();
        FacilityReport fr =session.get(FacilityReport.class,facilityReportId);
        return fr;
    }

    public Integer addNewFacilityReportDetail(FacilityReportDetail frd) {
        Session session;
        session = sessionFactory.getCurrentSession();
        Integer newFrdId = (Integer) session.save(frd);
        session.persist(frd);
        return newFrdId;
    }

    public FacilityReportDetail getFacilityReportDetailById(Integer frdId) {
        Session session;
        session = sessionFactory.getCurrentSession();
        FacilityReportDetail frd =session.get(FacilityReportDetail.class, frdId);
        return frd;
    }

    public List<Integer> getFacilityIdsByHouseId(Integer houseId) {
        Session session;
        session = sessionFactory.getCurrentSession();
        String hql = "select f.id from Facility f where f.house.id=:houseId";
        Query<Integer> query = session.createQuery(hql,Integer.class);
        query.setParameter("houseId", houseId);
        return query.getResultList();

    }

    public void updateFacilityReportStatus(String status, Integer frId) {
        Session session;
        session = sessionFactory.getCurrentSession();
        FacilityReport facilityReport = session.get(FacilityReport.class,frId);
        facilityReport.setStatus(status);
        session.update(facilityReport);
    }
}
