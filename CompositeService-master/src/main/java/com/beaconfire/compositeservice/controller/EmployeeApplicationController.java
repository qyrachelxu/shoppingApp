package com.beaconfire.compositeservice.controller;

import com.beaconfire.compositeservice.domain.EmployeeService.Employee;
import com.beaconfire.compositeservice.entity.EmployeeService.ApplicationDTO;
import com.beaconfire.compositeservice.entity.EmployeeService.EditRequest;
import com.beaconfire.compositeservice.entity.EmployeeService.EmployeeInfoDTO;
import com.beaconfire.compositeservice.entity.EmployeeService.Response.ApplicationDetailResponse;
import com.beaconfire.compositeservice.service.CompositeService;
import io.swagger.annotations.ApiOperation;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("composite/employee-application")
@ApiOperation("EmployeeApplicationController")
public class EmployeeApplicationController {
    @Value("${security.jwt.token.key}")
    private String key;

    private CompositeService compositeService;

    @Autowired
    public void setCompositeService(CompositeService compositeService) {
        this.compositeService = compositeService;
    }

    private Integer getCurrentUserID(HttpServletRequest request) {
        String prefixedToken = request.getHeader("Authorization");
        String token = prefixedToken.substring(7);
        Claims claims = null;
        try {
            claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            // handle exception
        }
        Integer userId = (Integer) claims.get("userId");
        return userId;
    }

    private String getCurrentuserAuthority(HttpServletRequest request) {
        String prefixedToken = request.getHeader("Authorization");
        String token = prefixedToken.substring(7);
        Claims claims = null;
        try {
            claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            // handle exception
        }
        List<LinkedHashMap<String, String>> permissions = (List<LinkedHashMap<String, String>>) claims.get("permissions");

        // convert the permission list to a list of GrantedAuthority
        List<GrantedAuthority> authorities = permissions.stream()
                .map(p -> new SimpleGrantedAuthority(p.get("authority")))
                .collect(Collectors.toList());
        String userAuthority = authorities.get(0).getAuthority();
        return userAuthority;
    }

    //hr get applications for different status
    @GetMapping("/hr/applications/{status}")
    public List<EmployeeInfoDTO> getApplications(@PathVariable String status){
        return compositeService.getApplications(status);
    }

    //hr can view certain pending application
    @GetMapping("hr/applications/Pending/{userID}")
    public ApplicationDetailResponse getPendingApplication(@PathVariable int userID) {
        return compositeService.getCertainPendingApplication(userID);
    }

//    @PostMapping("/employee-service/apply")
//    public ResponseEntity<String> apply(@RequestBody Employee employee){
//        return compositeService.apply(employee);
//    }

    @PostMapping("/employee-service/apply")
    public ResponseEntity<String> apply(@RequestBody Employee employee){
        System.out.println("controller");
        return compositeService.apply(employee);
    }

    @GetMapping("/employee-service/{userID}/result")
    public ResponseEntity<?> getEmployeeResult(HttpServletRequest request, @PathVariable int userID){
        int curId = getCurrentUserID(request);
        if(curId != userID) return ResponseEntity.ok("You don't have access to this page.");
        return compositeService.getProfile(userID);
    }

    @PostMapping ("/employee-service/update")
    public ResponseEntity<String> updateApplication(@RequestBody Employee employee){
        System.out.println("controller");
        return compositeService.editApplication(employee);
    }

//    @PostMapping ("/employee-service/{userID}/update")
//    public ResponseEntity<String> updateApplication(@RequestBody EditRequest request){
//        System.out.println("controller");
//        Employee employee = request.getEmployee();
//        return compositeService.editApplication(employee, request.getComment());
//    }

}
