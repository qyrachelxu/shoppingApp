package com.xiang.authentication.domain.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewMessageRequest {
    private String title;
    private String description;
}
