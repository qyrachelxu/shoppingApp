package com.example.ApplicationService.response;


import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationStatusResponse {
    private Integer applicationId;
    private Integer userId;
    private Timestamp dateCreated;
    private Timestamp dateModified;
    private String status;
    private String comments;

}
