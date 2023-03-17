package com.beaconfire.housingservice.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name="facility_report_detail")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityReportDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "facility_report_id")
    @JsonIgnore
    private FacilityReport facilityReport;

    @Column(name = "employee_id")
    private String employeeId;

    @Column(name = "comment")
    private String comment;

    @Column(name = "created_time", updatable = false)
    private Timestamp createdTime;

    @Version
    @Column(name = "last_modification_time")
    private Timestamp lastModificationTime;

    @PrePersist
    protected void onCreate() {
        createdTime = Timestamp.from(Instant.now());
        lastModificationTime= Timestamp.from(Instant.now());
    }

    @PreUpdate
    protected void onUpdate() {
        lastModificationTime = Timestamp.from(Instant.now());
    }


}
