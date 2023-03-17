package com.example.EmployeeService.controller;

import com.example.EmployeeService.Exception.EmailNotFoundException;
import com.example.EmployeeService.Exception.EmployeeNotFoundException;
import com.example.EmployeeService.Exception.RoommateNotFoundException;
import com.example.EmployeeService.Exception.ValidationNotPassException;
import com.example.EmployeeService.domain.Car;
import com.example.EmployeeService.domain.Employee;
import com.example.EmployeeService.domain.PersonalDocument;
import com.example.EmployeeService.domain.VisaStatus;
import com.example.EmployeeService.dto.application.ApplicationDTO;
import com.example.EmployeeService.dto.houseComposite.EmployeeInfo;
import com.example.EmployeeService.dto.houseComposite.RoommateDTO;
import com.example.EmployeeService.dto.profile.NameDTO;
import com.example.EmployeeService.dto.profile.ProfileDTO;
import com.example.EmployeeService.dto.profile.document.DocumentDTO;
import com.example.EmployeeService.response.ApplicationResponse;
import com.example.EmployeeService.response.GetOPTResponse;
import com.example.EmployeeService.response.MessageResponse;
import com.example.EmployeeService.security.AuthUserDetail;
import com.example.EmployeeService.service.EmployeeService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/employee")
@Api(value = "EmployeeController")
public class EmployeeController {

    @Value("${security.jwt.token.key}")
    private String key;

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService){
        this.employeeService = employeeService;
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

    //add new application
    @PostMapping("/apply")
    @PreAuthorize("hasAuthority('employee')")
    @ApiOperation(value = "employeee start application" , response = MessageResponse.class)
    public MessageResponse fillApplicationForm(@Valid @RequestBody Employee employee, BindingResult bindingResult){
        // perform validation check
        if (bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();
            StringBuilder sb = new StringBuilder();
            errors.forEach(error -> sb.append("ValidationError in " + error.getObjectName() + ": " + error.getDefaultMessage()));
            return MessageResponse.builder().message("Missing required field").build();
        }

        if(employeeService.findEmployeeByUserIDOrEmail(employee.getUserID(), employee.getEmail()) != null) {
            return MessageResponse.builder().message("Duplicate ID or Email").build();
        }

        employeeService.addNewEmployee(employee);
        return MessageResponse.builder().message("success").build();
    }

    //edit application
    @PostMapping("/edit")
    @PreAuthorize("hasAuthority('employee')")
    public Employee editApplication(HttpServletRequest request, @Valid @RequestBody Employee employee, BindingResult bindingResult){// perform validation check
        Integer currentUserID = getCurrentUserID(request);
        if(currentUserID != employee.getUserID()) {
            throw new ValidationNotPassException("You don't have permission");
        }
        if (bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();
            StringBuilder sb = new StringBuilder();
            errors.forEach(error -> sb.append("ValidationError in " + error.getObjectName() + ": " + error.getDefaultMessage()));
            throw new ValidationNotPassException(sb.toString());
        }

        return employeeService.editEmployee(employee);
    }

//    employee can view all documents in pending status
    @GetMapping("/{userID}/personaldocuments")
    @PreAuthorize("hasAuthority('employee')")
    public List<PersonalDocument> getPersonalDocuments(HttpServletRequest request, @PathVariable int userID) {
        Integer currentUserID = getCurrentUserID(request);
        if(currentUserID.equals(userID)) {
            return employeeService.getPersonalDocuments(currentUserID);
        }else{
            throw new ValidationNotPassException("You don't have permission");
        }
    }

    //employee can view its profile
    @GetMapping("/{userID}/profile")
    @PreAuthorize("hasAuthority('employee')")
    public ProfileDTO getEmployeeProfile(HttpServletRequest request, @PathVariable int userID) {
        Integer currentUserID = getCurrentUserID(request);
        if(currentUserID.equals(userID)) {
            return employeeService.findProfileByUserID(userID);
        }else{
            throw new ValidationNotPassException("You don't have permission");
        }
    }

    //see the application info for the employee(txt & doc)
    @GetMapping("/{userID}/application")
    @PreAuthorize("hasAuthority('employee')")
    public ApplicationResponse getEmployeeApplication(HttpServletRequest request, @PathVariable int userID) {
        Integer currentUserID = getCurrentUserID(request);
        if(currentUserID.equals(userID)) {
            return ApplicationResponse.builder()
                    .messageResponse(new MessageResponse("Please wait for HR to review your application"))
                    .application(employeeService.findApplicationByUserID(userID))
                    .build();
        }else{
            throw new ValidationNotPassException("You don't have permission");
        }
    }

    @GetMapping("/{userID}/applicationinfo")
    @PreAuthorize("hasAuthority('employee')")
    public ApplicationDTO getEmployeeApplicationComposite(HttpServletRequest request, @PathVariable int userID) {
        Integer currentUserID = getCurrentUserID(request);
        if(currentUserID == userID) {
            return employeeService.findApplicationByUserID(userID);
        }else{
            throw new ValidationNotPassException("You don't have permission");
        }
    }
    @GetMapping("/{userID}/employeeId")
    public String getEmployeeIdByUserID(@PathVariable int userID){
        System.out.println("Reached EmployeeService");
        return employeeService.getObjectIdByUserId(userID);
    }
    //user can update profile
    @PutMapping("/{userID}/updateProfile")
    @PreAuthorize("hasAuthority('employee')")
    public MessageResponse updateProfile(HttpServletRequest request,
                                         @PathVariable int userID,
                                         @Valid @RequestBody ProfileDTO profileDTO,
                                         BindingResult bindingResult,
                                         @RequestParam String button,
                                         @RequestParam boolean decision) {
        Integer currentUserID = getCurrentUserID(request);
            if (!currentUserID.equals(userID) || bindingResult.hasErrors()) {
                List<FieldError> errors = bindingResult.getFieldErrors();
                StringBuilder sb = new StringBuilder();
                errors.forEach(error -> sb.append("ValidationError in " + error.getObjectName() + ": " + error.getDefaultMessage()));
                throw new ValidationNotPassException(sb.toString() + "You don't have permission");
            }

            if(button.toLowerCase().equals("cancel")) {
                if(decision) return new MessageResponse("All changes have been discarded.");
                else return new MessageResponse("Continue to edit...");
            }
            else if(button.toLowerCase().equals("save")) {
                if(!decision) return new MessageResponse("Continue to edit...");
                employeeService.updateProfileName(userID, profileDTO);
                return new MessageResponse("successfully updated");
            }
            return new MessageResponse("Invalid request");
    }

    //employee/getOPT, get OPT documents if the employee is on OPT
    @GetMapping("/{userID}/getOPT")
    public GetOPTResponse getOPT(HttpServletRequest request, @PathVariable int userID) {
        Employee employee = employeeService.findEmployeeByUserID(userID);
        Integer currentUserID = getCurrentUserID(request);
        String currentUserRole = getCurrentuserAuthority(request);
        if(currentUserID.equals(userID) || currentUserRole.equals("hr")) {
        //check if on OPT
        Optional<VisaStatus> optional = employee.getVisaStatuses().stream().filter(v -> v.getVisaType().equals("F-1 OPT") || v.getVisaType().equals("F-1 STEM OPT")).findAny();
        if(!optional.isPresent()) return GetOPTResponse.builder()
                .messageResponse(new MessageResponse("Not on OPT"))
                .build();

        List<DocumentDTO> documentDTOList = new ArrayList<>();
        List<String> optList = Arrays.asList("OPT Receipt", "OPT EAD", "I-983", "I-20", "OPT STEM Receipt", "OPT STEM EAD");
        for(String str: optList) {
            Optional<PersonalDocument> tmpOptional = employee.getPersonalDocuments().stream().filter(d -> d.getTitle().equals(str)).findAny();
            if(tmpOptional.isPresent()) {
                PersonalDocument personalDocument = tmpOptional.get();
                if(personalDocument.getDocumentStatus().equals("Pending"))
                    return GetOPTResponse.builder()
                            .approvedDocuments(documentDTOList)
                            .messageResponse(new MessageResponse("Waiting for HR to approve your " + str))
                            .build();
                else if(personalDocument.getDocumentStatus().equals("Approved")) {
                    documentDTOList.add(DocumentDTO.builder()
                            .title(personalDocument.getTitle())
                            .path(personalDocument.getPath())
                            .personalDocumentId(personalDocument.getPersonalDocumentId())
                            .build());
                }
                else if(personalDocument.getDocumentStatus().equals("Rejected"))
                    return GetOPTResponse.builder()
                            .approvedDocuments(documentDTOList)
                            .messageResponse(new MessageResponse(str + "is rejected. Feedback: " + personalDocument.getComment()))
                            .build();
            }
            else {
                return GetOPTResponse.builder()
                        .approvedDocuments(documentDTOList)
                        .messageResponse(new MessageResponse("Please upload a copy of your " + str))
                        .build();
            }
        }

        return GetOPTResponse.builder()
                .approvedDocuments(documentDTOList)
                .messageResponse(new MessageResponse("All OPT documents have been approved"))
                .build();
        }else{
            throw new ValidationNotPassException("You don't have permission");
        }
    }

    //composite: get all roommates
    @GetMapping("/{employeeId}/roommates")
    public List<RoommateDTO> getRoommates(@PathVariable String employeeId) {
        return employeeService.findRoommatesByEmployeeId(employeeId);
    }

    //composite: get residents by houseID
    @GetMapping("/house/{houseID}/residents")
    public List<EmployeeInfo> getNumOfResidents(@PathVariable int houseID) {
        List<Employee> employees = employeeService.findResidentsByHouseID(houseID);
        List<EmployeeInfo> employeeInfos = new ArrayList<>();
        employees.forEach(e -> {
            employeeInfos.add(EmployeeInfo.builder()
                            .legalFullName(e.getFirstName() + " " + e.getLastName())
                            .carList(e.getCar())
                            .phoneNumber(e.getCellPhone())
                            .email(e.getEmail())
                            .houseId(e.getHouseID())
                    .build());
        });
        return employeeInfos;
    }

    //composite: get employee by employee id
    @GetMapping("/{employeeId}")
    public EmployeeInfo getEmployeeByEmployeeId(@PathVariable String employeeId) {
        Employee employee = employeeService.findEmployeeByEmployeeId(employeeId);
         if(employee == null) throw new EmployeeNotFoundException("Employee Not Found. Please provide correct id");

        String legalName = employee.getFirstName() + " "+
                employee.getLastName();
        Integer houseId = employee.getHouseID();
        return EmployeeInfo.builder()
                .userId(employee.getUserID())
                .houseId(houseId)
                .legalFullName(legalName)
                .phoneNumber(employee.getCellPhone())
                .email(employee.getEmail())
                .carList(employee.getCar())
                .build();
    }
}
