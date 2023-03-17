package com.beaconfire.compositeservice.domain.EmployeeService;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PersonalDocument {

    private String personalDocumentId;

    private String type;

    private String path;

    private String title;

    private String comment;

    private String createDate;

    //new
    private String documentStatus;
}
