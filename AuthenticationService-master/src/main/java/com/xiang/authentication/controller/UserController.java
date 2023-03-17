package com.xiang.authentication.controller;

import com.xiang.authentication.domain.response.MessageResponse;
import com.xiang.authentication.domain.response.ServiceStatus;
import com.xiang.authentication.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.xiang.authentication.domain.POJO.RolePOJO;
import com.xiang.authentication.domain.POJO.UserPOJO;
import com.xiang.authentication.domain.entity.Role;
import com.xiang.authentication.domain.entity.User;
import com.xiang.authentication.domain.entity.UserRole;
import com.xiang.authentication.exception.ZeroOrManyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("authentication")
@ApiOperation("anthtication")
public class UserController {
    private UserService userService;
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/userRole/{username}")
    @PreAuthorize("hasAuthority('hr')")
    public ResponseEntity<UserPOJO> getUserRoleByUsername(@PathVariable String username) throws ZeroOrManyException {
        User user = userService.getUserByUsername(username);
        List<UserRole> userRoles = userService.getUserRoleByUser(user);
        List<RolePOJO> rolePOJOS = new ArrayList<>();
        for (UserRole ur: userRoles) {
            Role role = userService.getRoleById(ur.getRoleId().getId());
            RolePOJO rolePOJO = new RolePOJO(role.getId(), role.getRoleName(), role.getRoleDescription(),
                    role.getCreateDate(), role.getLastModificationDate());
            rolePOJOS.add(rolePOJO);
        }
        UserPOJO userPOJO = new UserPOJO(user.getId(), user.getUsername(), user.getEmail(), user.getCreateDate(),
                user.getLastModificationDate(), user.getActiveFlag(), rolePOJOS);
        return new ResponseEntity<>(userPOJO, HttpStatus.OK);
    }

    @PutMapping("/update/password")
    @PreAuthorize("hasAuthority('employee')")
    public MessageResponse updatePassword(@RequestBody String password) throws ZeroOrManyException {
        userService.updatePassword(password);
        return MessageResponse.builder()
                .serviceStatus( ServiceStatus.builder().success(true).build())
                .message("Password changed successfully")
                .build();
    }

}
