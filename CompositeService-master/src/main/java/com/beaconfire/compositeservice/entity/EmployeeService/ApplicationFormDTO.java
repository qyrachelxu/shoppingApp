package com.beaconfire.compositeservice.entity.EmployeeService;

import com.beaconfire.compositeservice.domain.EmployeeService.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

//import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ApplicationFormDTO {
    @JsonIgnore
    private String employeeId;

    private int userID;

    private String firstName;

    private String lastName;

    private String middleName;

    private String preferedName;

    private String email;

    private String cellPhone;

    private String alternatePhone;

    private String gender;

    private String ssn;

    private String dob;

    private String startDate;

    private String endDate;

    private String driverLicense;

    private String driverLicenseExpiration;

    private String houseID;

    //new
    private boolean greencardOrCitizen;
    private String profilePicture = "default";
    private List<Car> car;
    private List<Reference> reference;

    private List<Contact> contacts = new ArrayList<>();

    private List<Address> addresses = new ArrayList<>();

    private List<VisaStatus> visaStatuses = new ArrayList<>();
}
