package com.example.ApplicationService.controller;


import com.example.ApplicationService.domain.DigitalDocument;
import com.example.ApplicationService.exception.DataNotFoundException;
import com.example.ApplicationService.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Optional;

@Controller
@RequestMapping("downloadlink")
public class DownloadController {

    ApplicationService applicationService;
    @Autowired
    public DownloadController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }
    @GetMapping("/{type}/{version}/{userid}")
    public ResponseEntity<ByteArrayResource> downloadFileEmployee(@PathVariable(value = "userid") Integer userId,
                                               @PathVariable(value = "type") String documentType,
                                               @PathVariable(value = "version") String documentVersion) {
        String fileName = "";
        if (documentVersion.equals("Hr")) {
            fileName = documentType + "_original.pdf";
        } else if (documentVersion.equals("Employee")) {
            fileName = documentType + "_id" + userId + ".pdf";
        } else  {
            return ResponseEntity.notFound().build();
        }
        byte[] data = applicationService.downloadFile(fileName);
        if (data == null) {
            throw new DataNotFoundException("No Document Found");
        }
        ByteArrayResource res = new ByteArrayResource(data);
        return ResponseEntity.ok()
                .contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=" + fileName)
                .body(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ByteArrayResource> downloadFileHR(@PathVariable(value = "id") Integer documentID) {

        Optional<DigitalDocument> possibleDoc = applicationService.getDigitalDocumentByID(documentID);
        if (!possibleDoc.isPresent()) { // exception handler
            throw new DataNotFoundException("No Document Found for the user!");
        }
        String fileName = possibleDoc.get().getDocumentTitle();
        byte[] data = applicationService.downloadFile(fileName);
        if (data == null) {
            return ResponseEntity.notFound().build();
        }
        ByteArrayResource res = new ByteArrayResource(data);
        return ResponseEntity.ok()
                .contentLength(data.length)
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=" + fileName)
                .body(res);
    }

}
