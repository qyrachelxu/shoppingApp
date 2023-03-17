package com.example.EmployeeService.domain;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Id;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "employee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Employee {
    @Id
    private ObjectId _id;

    private int userID;

    @NotBlank(message = "firstName is required")
    private String firstName;

    @NotBlank(message = "lastName is required")
    private String lastName;

    private String middleName;

    private String preferedName;

    private String email;

    @NotBlank(message = "cellPhone is required")
    private String cellPhone;

    private String alternatePhone;

    private String gender;

    @NotBlank(message = "ssn is required")
    private String ssn;

    @NotBlank(message = "dob is required")
    private String dob;

    private String startDate;

    private String endDate;

    @NotBlank(message = "driverLicense is required")
    private String driverLicense;

    @NotBlank(message = "driverLicenseExpiration is required")
    private String driverLicenseExpiration;

    private int houseID;

    //new
    @NotNull(message = "greencardOrCitizen is required")
    private boolean greencardOrCitizen;
//    private String profilePicture = "default";

    private List<Car> car = new ArrayList<>();

    @Valid
    private List<Reference> reference = new ArrayList<>();

    @NotEmpty(message = "contacts is required")
    @Valid
    private List<Contact> contacts = new ArrayList<>();

    @NotEmpty(message = "addresses is required")
    private List<Address> addresses = new ArrayList<>();

    private List<VisaStatus> visaStatuses = new ArrayList<>();

    //Need S3 bucket to put the files
    private List<PersonalDocument> personalDocuments = new ArrayList<>();
}

