package com.example.EmployeeService.controller;

import com.example.EmployeeService.Exception.ValidationNotPassException;
import com.example.EmployeeService.domain.PersonalDocument;
import com.example.EmployeeService.service.EmployeeService;
import com.example.EmployeeService.service.StorageService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/employee")
@Api(value = "EmployeeStorageController")
public class StorageController {

    @Value("${security.jwt.token.key}")
    private String key;
    @Autowired
    private StorageService storageService;

    @Autowired
    private EmployeeService employeeService;

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

    @PostMapping("/{userID}/uploadpersonaldocument")
    @PreAuthorize("hasAuthority('employee')")
    public ResponseEntity<PersonalDocument> uploadDocument(HttpServletRequest request,
                                                           @PathVariable int userID,
                                                           @RequestParam(value = "file") MultipartFile file,
                                                           @RequestParam String type,
                                                           @RequestParam("title") String title,
                                                           @RequestParam("comment") String comment,
                                                           @RequestParam(value = "documentStatus", defaultValue = "Pending")String documentStatus
    ) throws IOException {
        Integer currentUserID = getCurrentUserID(request);
        if(currentUserID.equals(userID)) {
            PersonalDocument personalDocument = storageService.addPersonalDocument(userID, file, type, title, comment, documentStatus);
            return ResponseEntity.ok(personalDocument);
        }else{
            throw new ValidationNotPassException("You don't have permission");
        }
    }

    @PutMapping("/{userID}/updatepersonaldocument")
    @PreAuthorize("hasAuthority('employee')")
    public ResponseEntity<PersonalDocument> updatePersonalDocuments(
            HttpServletRequest request,
            @PathVariable("userID") int userID,
            @RequestParam("oldTitle") String oldTitle,
            @RequestParam("type") String type,
            @RequestParam("newTitle") String newTitle,
            @RequestParam("file") MultipartFile file,
            @RequestParam("comment") String comment,
            @RequestParam(value = "documentStatus", defaultValue = "Pending")String documentStatus
            ) throws IOException {

        Integer currentUserID = getCurrentUserID(request);
        if(currentUserID.equals(userID)) {
            PersonalDocument personalDocument = storageService.updatePersonalDocuments(userID, oldTitle, type, newTitle, file, comment, documentStatus);
            return ResponseEntity.ok(personalDocument);
        }else{
            throw new ValidationNotPassException("You don't have permission");
        }
    }

    //localhost:9000/employee-service/employee/download/personaldocument/filenameFromS3
    @GetMapping("/download/personaldocument/{fileName}")
    @PreAuthorize("hasAuthority('employee')")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileName) {
        byte[] data = storageService.downloadFile(fileName);
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @GetMapping("/{userID}/personal-documents")
    @PreAuthorize("hasAuthority('employee')")
    public List<PersonalDocument> getPersonalDocuments(@PathVariable("userID") int userID) {
        return employeeService.getPersonalDocuments(userID);
    }
}