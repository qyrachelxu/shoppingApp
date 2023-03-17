package com.example.ApplicationService.response;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DigitalDocumentResponse {
    private String documentType;
    private Boolean documentIsRequired;
    private String documentDescription;
    private String documentTitle;
}
