package com.example.EmailService.domain;

import lombok.*;

import java.util.Date;

// Annotations
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
// Class
public class EmailDetail {

    // Class data members
    private String recipient;
    private String msgBody;
    private String subject;
}
