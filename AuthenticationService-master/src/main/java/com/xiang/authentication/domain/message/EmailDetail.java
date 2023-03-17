package com.xiang.authentication.domain.message;

import lombok.*;

// Annotations
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
// Class
public class EmailDetail {

    // Class data members
    private String recipient;
    private String msgBody;
    private String subject;
}
