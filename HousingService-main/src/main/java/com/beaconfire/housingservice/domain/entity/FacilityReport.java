package com.beaconfire.housingservice.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="facility_report")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "facility_id")
    @JsonIgnore
    private Facility facility;

    @Column(name="employee_id")
    private String employeeId;

    @Column(name="title")
    private String title;

    @Column(name="description")
    private String description;
    @Column(name = "created_time", updatable = false)
    private Timestamp createdTime;

    @Column(name="status")
    private String status;

    @PrePersist
    protected void onCreate() {
        createdTime = Timestamp.from(Instant.now());
    }

    @OneToMany(mappedBy = "facilityReport", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<FacilityReportDetail> facilityReportDetails = new ArrayList<>();






}
