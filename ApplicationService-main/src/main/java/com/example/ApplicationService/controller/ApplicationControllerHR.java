package com.example.ApplicationService.controller;


import com.example.ApplicationService.domain.DigitalDocument;
import com.example.ApplicationService.domain.WorkFlow;
import com.example.ApplicationService.exception.DataNotFoundException;
import com.example.ApplicationService.exception.DocumentExistException;
import com.example.ApplicationService.exception.InvalidInputException;
import com.example.ApplicationService.response.ApplicationStatusResponse;
import com.example.ApplicationService.response.DigitalDocumentResponse;
import com.example.ApplicationService.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("hr/application")
public class ApplicationControllerHR {
    private ApplicationService applicationService;

    private HashSet<String> possibleStatus = new HashSet<>();

    @GetMapping("/test")
    public String test(){
        return "test HRRR";
    }

    @Autowired
    public ApplicationControllerHR(ApplicationService applicationService) {
        this.applicationService = applicationService;
        this.possibleStatus.add("Pending");
        this.possibleStatus.add("Rejected");
        this.possibleStatus.add("Approved");
        this.possibleStatus.add("Incomplete");

    }

    @GetMapping("/viewallapplication")
    public ResponseEntity<List<ApplicationStatusResponse>> viewAllApplicationHR() {
        List<WorkFlow> allApplication = applicationService.getAllApplication();
        Collections.sort(allApplication, Comparator
                                            .comparing(WorkFlow::getWorkflowDateModified)
                                            .reversed()
                                            .thenComparing(WorkFlow::getWorkflowStatus));
        List<ApplicationStatusResponse> res = new ArrayList<>();
        for (WorkFlow each : allApplication) {
            res.add(ApplicationStatusResponse.builder()
                            .applicationId(each.getWorkflowId())
                            .userId(each.getUserId())
                            .dateCreated(each.getWorkflowDateCreated())
                            .dateModified(each.getWorkflowDateModified())
                            .status(each.getWorkflowStatus())
                            .comments(each.getWorkflowComment())
                            .build());
        }
        return ResponseEntity.ok(res);
    }

    @GetMapping("/viewall")
    public ResponseEntity<List<DigitalDocument>> viewAllFileHR() {
        List<DigitalDocument> res = applicationService.getAllDigitalDocuments();
        Collections.sort(res, Comparator.comparing(DigitalDocument::getDocumentDescription)
                .thenComparing(DigitalDocument::getDocumentType));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/viewone")
    public ResponseEntity<List<DigitalDocumentResponse>> viewOneFileHR(@RequestParam("userid") Integer id) {
        List<DigitalDocument> allUserDocuments = applicationService.getUserDocumentByID(id); // get his/her own documents.
        if (allUserDocuments == null || allUserDocuments.size() == 0) {
            throw new DataNotFoundException("No Digital Document found with the given userid");
        }
        List<DigitalDocumentResponse> responseList = allUserDocuments.stream()
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
    public ResponseEntity<String> uploadFileHR(@RequestParam("file") MultipartFile file,
                                               @RequestParam("required") boolean documentRequired,
                                               @RequestParam("type") String documentType) {
        String fileNameOriginal = file.getOriginalFilename();
        if (fileNameOriginal == null || fileNameOriginal.isEmpty()) {
            throw new InvalidInputException("File name cannot be empty");
        }
        String fileName = documentType + "_" + "original.pdf";
        Optional<DigitalDocument> digitalDocumentOptional = applicationService.getDigitalDocumentByTitle(fileName);
        if (digitalDocumentOptional.isPresent()) {
            throw new DocumentExistException("Upload failed! Please delete previous version before upload it again!");
        }
        applicationService.uploadFileHR(file, documentRequired, documentType, fileName);
        return ResponseEntity.ok("File uploaded successfully");
    }

    @PutMapping("/updatestatus")
    public ResponseEntity<String> updateStatusHR(@RequestParam("userid") Integer id,
                                                 @RequestParam("result") String status,
                                                 @RequestParam("comment") String comment) {
        if (!this.possibleStatus.contains(status)) {
            throw new InvalidInputException("Bad Status Input!");
        }
        Optional<WorkFlow> possibleApplication = applicationService.getApplicaionByUserId(id); // id = 2;
        if (!possibleApplication.isPresent()) {
            throw new DataNotFoundException("No Application Found for the user!");
        }
        WorkFlow curApplication = possibleApplication.get();
        String prevStatus = curApplication.getWorkflowStatus();

        if (prevStatus.equals("Incomplete")) {
            return ResponseEntity.badRequest().body("The user has not submitted the application, please wait!");
        }

        applicationService.updateApplication(curApplication, status, comment);
        return ResponseEntity.ok(prevStatus + " changed to " + status + "   Comments added: " + comment);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteDigitalDocumentHR(@PathVariable("id") Integer documentID) {
        Optional<DigitalDocument> possibleDoc = applicationService.getDigitalDocumentByID(documentID);
        if (!possibleDoc.isPresent()) {
            throw new DataNotFoundException("No Such a Document Found for the Given ID!");
        }
        DigitalDocument document = possibleDoc.get();
        applicationService.deleteDigitalDocument(document);
        return ResponseEntity.ok("The document has been deleted!");
    }

    @GetMapping("download/{id}")
    public ResponseEntity<String> downloadFileHR(@PathVariable(value = "id") Integer documentID) {
        Optional<DigitalDocument> possibleDoc = applicationService.getDigitalDocumentByID(documentID);
        if (!possibleDoc.isPresent()) { // exception handler
            throw new DataNotFoundException("No Document Found for the user!");
        }
        return ResponseEntity.ok("http://localhost:9000/application-service/downloadlink/" +  documentID);
    }

}
