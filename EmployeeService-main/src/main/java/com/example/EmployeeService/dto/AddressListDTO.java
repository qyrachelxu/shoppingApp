package com.example.EmployeeService.dto;

import com.example.EmployeeService.domain.Address;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AddressListDTO {
    private List<Address> addresses;
}
