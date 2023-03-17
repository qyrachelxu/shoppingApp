package com.xiang.authentication.domain.message;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
public class SimpleMessage implements Serializable{
    private String title;
    private String description;
}
