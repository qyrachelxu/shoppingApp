package com.beaconfire.housingservice.repository;

import com.beaconfire.housingservice.domain.entity.FacilityReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilityReportRepository extends JpaRepository<FacilityReport, Long> {
    @Query(value = "SELECT fr FROM FacilityReport fr left join fetch fr.facilityReportDetails frd where fr.facility.house.id =:houseId",
    countQuery = "select count(fr) from FacilityReport fr left join fr.facilityReportDetails frd where fr.facility.house.id =:houseId")
    Page<FacilityReport> findAllByOrderByCreatedTimeDesc(Pageable pageable, @Param("houseId") Integer houseId);



}
