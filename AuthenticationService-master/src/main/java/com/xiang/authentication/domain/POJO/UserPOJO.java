package com.xiang.authentication.domain.POJO;

import lombok.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPOJO {
    private Integer id;
    private String username;
    private String email;
    private Timestamp createDate;
    private Timestamp lastModificationDate;
    private Boolean activeFlag;
    private List<RolePOJO> roles;

    public UserPOJO(Integer id, String username, String email, Timestamp createDate, Timestamp lastModificationDate, Boolean activeFlag) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.createDate = createDate;
        this.lastModificationDate = lastModificationDate;
        this.activeFlag = activeFlag;
    }
}
