package com.xiang.authentication.domain.POJO;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RolePOJO {
    private Integer id;

    private String roleName;

    private String roleDescription;

    private Timestamp createDate;

    private Timestamp lastModificationDate;
}
