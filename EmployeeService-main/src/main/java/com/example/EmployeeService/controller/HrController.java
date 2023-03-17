package com.example.EmployeeService.controller;

import com.example.EmployeeService.Exception.EmailNotFoundException;
import com.example.EmployeeService.domain.Employee;
import com.example.EmployeeService.domain.PersonalDocument;
import com.example.EmployeeService.dto.application.ApplicationDTO;
import com.example.EmployeeService.dto.application.EmployeeInfoDTO;
import com.example.EmployeeService.dto.profile.ProfileDTO;
import com.example.EmployeeService.dto.profile.employment.EmployeeSummaryDTO;
import com.example.EmployeeService.dto.profile.employment.EmployeeVisaDTO;
import com.example.EmployeeService.service.EmployeeService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/hr")
@Api(value = "HrController")
public class HrController {

    @Value("${security.jwt.token.key}")
    private String key;
    private final EmployeeService employeeService;

    @Autowired
    public HrController(EmployeeService employeeService){
        this.employeeService = employeeService;
    }

    @GetMapping("test")
    public String getTest(){
        return "test hr employee service";
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

    // hr can view all applications for a list of users
    @GetMapping("/applications")
    @PreAuthorize("hasAuthority('hr')")
    public List<EmployeeInfoDTO> getApplications(@RequestParam List<Integer> userIDs) {
        return employeeService.findEmployeeInfoByUserIDs(userIDs);
    }

    //hr can view certain pending applications
    @GetMapping("/applications/pending/{userID}")
    @PreAuthorize("hasAuthority('hr')")
    public ApplicationDTO getCertainPendingApplication(@PathVariable int userID) {
        return employeeService.findApplicationByUserID(userID);
    }

    //hr can view profile by userid
    @GetMapping("/{userId}/profile")
    public ProfileDTO getEmployeeProfile(@PathVariable int userId) {
        return employeeService.findProfileByUserID(userId);
    }

    @GetMapping("/{userId}/viewprofile")
    @PreAuthorize("hasAuthority('hr')")
    public ProfileDTO getEmployeeProfileforhr(@PathVariable int userId) {
        return employeeService.findProfileByUserID(userId);
    }

    //hr can change opt document status and give comment
    @PutMapping("/{userID}/reviewdocument")
    @PreAuthorize("hasAuthority('hr')")
    public PersonalDocument reviewOPTDocument(@PathVariable int userID, @RequestParam String title, @RequestParam String status, @RequestParam String comment) {
        return employeeService.reviewOPTDocument(userID, title, status, comment);
    }

    //hr can view employees in page by basic information
    @GetMapping("/employees/sort")
    @PreAuthorize("hasAuthority('hr')")
    public List<EmployeeSummaryDTO> getEmployeeSummaries(@RequestParam(defaultValue = "0") int page) {
        return employeeService.getAllEmployeeSummaries(page);
    }

    //hr can view employees in page by visa
    @GetMapping("/employees/visa")
    @PreAuthorize("hasAuthority('hr')")
    public List<EmployeeVisaDTO> getAllEmployeeVisaStatus(@RequestParam(defaultValue = "0") int page) {
        return employeeService.getEmployeeByVisaStatus(page);
    }

    @GetMapping("/employee/name")
    @PreAuthorize("hasAuthority('hr')")
    public ResponseEntity<Employee> getEmployeeByName(@RequestParam String firstName, @RequestParam String lastName) {
        Employee employee = employeeService.getEmployeeByUsername(firstName, lastName);
        if (employee != null) {
            return ResponseEntity.ok(employee);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/employee/email")
    @PreAuthorize("hasAuthority('hr')")
    public ResponseEntity<Employee> getEmployeeByEmail(@RequestParam String email) {
        Employee employee = employeeService.getEmployeeByEmail(email);
        if (employee != null) {
            return ResponseEntity.ok(employee);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //??? 从employee换到这里是否合适？ composite的路径需要调整
    //composite: get email given userID
    @GetMapping("/{userID}/getemail")
    @PreAuthorize("hasAuthority('hr')")
    public String getEmailByUserID(@PathVariable int userID) {
        Employee employee = employeeService.findEmployeeByUserID(userID);
        String email = employee.getEmail();
        if(email == null || email.isEmpty()) {
            throw new EmailNotFoundException("No email found for the employee with userID: " + userID);
        }
        return email;
    }
}
