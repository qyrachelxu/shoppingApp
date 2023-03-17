package com.beaconfire.compositeservice.domain.EmployeeService;

import lombok.*;

//import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Employee {

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

    private int houseID;

    //new
    private boolean greencardOrCitizen;
    private String profilePicture = "default";

    private List<Car> car = new ArrayList<>();
    private List<Reference> reference = new ArrayList<>();

    private List<Contact> contacts = new ArrayList<>();

    private List<Address> addresses = new ArrayList<>();

    private List<VisaStatus> visaStatuses = new ArrayList<>();

    //Need S3 bucket to put the files
    private List<PersonalDocument> personalDocuments = new ArrayList<>();
}

