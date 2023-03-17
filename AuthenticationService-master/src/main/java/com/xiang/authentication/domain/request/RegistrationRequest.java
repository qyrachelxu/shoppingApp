package com.xiang.authentication.domain.request;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {
    private Integer id;
    private String username;
    private String password;
    private String email;
}
