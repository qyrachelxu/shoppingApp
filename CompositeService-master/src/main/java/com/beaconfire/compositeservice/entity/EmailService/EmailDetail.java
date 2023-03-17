package com.beaconfire.compositeservice.entity.EmailService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
