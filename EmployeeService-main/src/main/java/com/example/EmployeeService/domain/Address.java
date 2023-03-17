package com.example.EmployeeService.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Address {

    //String or Integer or ObjectId?
    private String addressId;

    @NotBlank(message = "addressLine1 is required")
    private String addressLine1;

    private String addressLine2;

    @NotBlank(message = "city is required")
    private String city;

    @NotBlank(message = "state is required")
    private String state;

    @NotBlank(message = "zipcode is required")
    private String zipcode;
}