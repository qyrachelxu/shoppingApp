package com.beaconfire.housingservice.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="facility")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Facility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "house_id")
    @JsonIgnore
    private House house;

    @Column(name="type")
    private String type;

    @Column(name="description")
    private String description;

    @Column(name="quantity")
    private Integer quantity;

    @OneToMany(mappedBy = "facility", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @ToString.Exclude
    private List<FacilityReport> facilityReports= new ArrayList<>();
}
