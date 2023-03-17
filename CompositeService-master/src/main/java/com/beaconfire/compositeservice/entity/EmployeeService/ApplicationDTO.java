package com.beaconfire.compositeservice.entity.EmployeeService;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ApplicationDTO {
    private ApplicationFormDTO form;
    private List<DocumentDTO> documents;
}
