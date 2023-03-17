package com.example.ApplicationService.controller;

import com.example.ApplicationService.domain.DigitalDocument;
import com.example.ApplicationService.domain.WorkFlow;
import com.example.ApplicationService.exception.ApplicationExistException;
import com.example.ApplicationService.exception.DataNotFoundException;
import com.example.ApplicationService.exception.DocumentExistException;
import com.example.ApplicationService.exception.InvalidInputException;
import com.example.ApplicationService.response.DigitalDocumentResponse;
import com.example.ApplicationService.service.ApplicationService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("employee/application")
public class ApplicationController {

    @Value("${security.jwt.token.key}")
    private String key;
    private ApplicationService applicationService;

    private HashSet<String> possibleStatus = new HashSet<>();

    @Autowired
    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
        this.possibleStatus.add("Pending");
        this.possibleStatus.add("Rejected");
        this.possibleStatus.add("Approved");
        this.possibleStatus.add("Incomplete");

    }
    @GetMapping("test")
    public String test(){
        return "test application service";
    }


    private Integer getCurrentUserID(HttpServletRequest request) {
        String prefixedToken = request.getHeader("Authorization");
        String token = prefixedToken.substring(7);
        Claims claims = null;
        try {
            claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Integer userId = (Integer) claims.get("userId");
        return userId;
    }
    @GetMapping("/applicationstatus")
    public ResponseEntity<String> checkApplicationStatus(HttpServletRequest request) {
        Integer id = getCurrentUserID(request);
        String applicationStatus = applicationService.checkApplication(id);
        return ResponseEntity.ok(applicationStatus);
    }

    @PostMapping("/addapplication")
    public ResponseEntity<String> addApplication(HttpServletRequest request) {
        Integer id = getCurrentUserID(request);
        Optional<WorkFlow> possibleApplication = applicationService.getApplicaionByUserId(id);
        if (possibleApplication.isPresent()) {
            throw new ApplicationExistException("There is an application in process, Go to previous application forum");
        }
        applicationService.addApplication(id);
        return ResponseEntity.ok("Go To Application Forum!");
    }

    @PutMapping("/submit")
    public ResponseEntity<String> submitApplication(HttpServletRequest request) {
        Integer id = getCurrentUserID(request);
        List<DigitalDocument> alldocuments = applicationService.getAllDocumentView();
        List<DigitalDocument> allUserDocuments = applicationService.getUserDocumentByID(id); // get his/her own documents.
        int requiredAmount = 0;
        for (DigitalDocument each : alldocuments) {
            if (each.getDocumentIsRequired()) {
                requiredAmount++;
            }
        }
        if (requiredAmount > allUserDocuments.size()) {
            return ResponseEntity.ok("You have to submit all the Digital Document before submission! Go to Documentation Page.");
        }
        applicationService.submitApplication(id);
        return ResponseEntity.ok("submit successfully! your application is now pending and wait for a response"); // go to view all
    }

    @GetMapping("/viewall")
    public ResponseEntity<List<DigitalDocumentResponse>> viewAllFile(HttpServletRequest request) {
        Integer id = getCurrentUserID(request);
        List<DigitalDocument> allDocuments = applicationService.getAllDocumentView();        // get all documents from hr.
        List<DigitalDocument> allUserDocuments = applicationService.getUserDocumentByID(id); // get his/her own documents.
        if (allUserDocuments != null && allUserDocuments.size() > 0) {
            allDocuments.addAll(allUserDocuments);
        }
        List<DigitalDocumentResponse> responseList = allDocuments.stream()
                .map(document -> DigitalDocumentResponse.builder()
                        .documentType(document.getDocumentType())
                        .documentIsRequired(document.getDocumentIsRequired())
                        .documentDescription(document.getDocumentDescription())
                        .documentTitle(document.getDocumentTitle())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }


    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(HttpServletRequest request,
                                             @RequestParam(value = "file") MultipartFile file,
                                             @RequestParam(value = "type") String documentType) {
        Integer id = getCurrentUserID(request);
        HashSet<String> set = this.allDocumentType();
        if (!set.contains(documentType)) {
            throw new InvalidInputException("Invalid document type");
        }
        if (!checkIfRequired(documentType)) {
            throw new InvalidInputException("This type of document doesn't need submission");
        }

        String fileName = documentType + "_id" + id + ".pdf";
        Optional<DigitalDocument> digitalDocumentOptional = applicationService.getDigitalDocumentByTitle(fileName);
        if (digitalDocumentOptional.isPresent()) {
            throw new DocumentExistException("Upload failed! Please delete previous version before upload it again!");
        }
        applicationService.uploadFile(file, id, documentType, fileName);
        return ResponseEntity.ok("File uploaded successfully");
    }

    @DeleteMapping("delete")
    public ResponseEntity<String> deleteDigitalDocument(HttpServletRequest request,
                                                        @RequestParam("type") String documentType) {
        Integer id = getCurrentUserID(request);
        String fileName = documentType + "_id" + id + ".pdf";
        Optional<DigitalDocument> possibleDoc = applicationService.getDigitalDocumentByTitle(fileName);
        if (!possibleDoc.isPresent()) {
            throw new DataNotFoundException("No Document Found");
        }
        DigitalDocument document = possibleDoc.get();
        applicationService.deleteDigitalDocument(document);
        return ResponseEntity.ok("Your DigitalDocument: " + documentType + " has been successfully deleted!");
    }
    @GetMapping("/download/{type}/{version}")
    public ResponseEntity<String> downloadFile(HttpServletRequest request,
                                                          @PathVariable(value = "type") String documentType,
                                                          @PathVariable(value = "version") String documentVersion) {
        Integer id = getCurrentUserID(request);
        String fileName = "";
        HashSet<String> set = this.allDocumentType();
        if (!set.contains(documentType)) {
            throw new DataNotFoundException("No Document Found");
        }
        if (documentVersion.equals("Hr")) {
            fileName = documentType + "_original.pdf";
        } else if (documentVersion.equals("Employee")) {
            fileName = documentType + "_id" + id + ".pdf";
        } else  {
            return ResponseEntity.notFound().build();
        }
        Optional<DigitalDocument> possible = applicationService.getDigitalDocumentByTitle(fileName);
        if (!possible.isPresent()) {
            throw new DataNotFoundException("No such Document!");
        }
        byte[] data = applicationService.downloadFile(fileName);
        if (data == null) {
            throw new DataNotFoundException("No Document Found");
        }
        ByteArrayResource res = new ByteArrayResource(data);
        return ResponseEntity.ok("http://localhost:9000/application-service/downloadlink/" + documentType + "/" + documentVersion + "/" + id);
    }



    // may implement cache:
    public HashSet<String> allDocumentType() {
        List<DigitalDocument> allHRDocuments = applicationService.getAllDocumentView();
        HashSet<String> set = new HashSet<>();
        for (DigitalDocument each : allHRDocuments) {
            set.add(each.getDocumentType());
        }
        return set;
    }
    private Boolean checkIfRequired(String documentType) {
        List<DigitalDocument> allHRDocuments = applicationService.getAllDocumentView();
        DigitalDocument doc = allHRDocuments.stream().filter(each -> each.getDocumentType().equals(documentType)).findAny().get();
        return doc.getDocumentIsRequired();

    }
}
