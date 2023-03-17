package com.example.EmployeeService.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.example.EmployeeService.Exception.DocumentNotFoundException;
import com.example.EmployeeService.Exception.EmployeeNotFoundException;
import com.example.EmployeeService.domain.Employee;
import com.example.EmployeeService.domain.PersonalDocument;
import com.example.EmployeeService.repository.EmployeeRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class StorageService {
    @Value("${application.bucket.name}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3Client;

    @Autowired
    private EmployeeRepo employeeRepo;

    public PersonalDocument addPersonalDocument(Integer userID, MultipartFile file, String type, String title, String comment, String documentStatus) throws IOException {
        Employee employee = employeeRepo.findByUserID(userID);
        if (employee == null) {
            throw new EmployeeNotFoundException(userID);
        }

        for (PersonalDocument personalDocument : employee.getPersonalDocuments()) {
            if (personalDocument.getTitle().equals(title)) {
                throw new DocumentNotFoundException("Upload fail. Document exists.");
            }
        }

        String fileName = uploadFile(file);

        PersonalDocument personalDocument = new PersonalDocument();
        personalDocument.setType(type);
        personalDocument.setTitle(title);
        personalDocument.setComment(comment);
        personalDocument.setCreateDate(String.valueOf(LocalDate.now()));
        personalDocument.setPath(fileName);
        personalDocument.setDocumentStatus("Pending");

        List<PersonalDocument> personalDocuments = employee.getPersonalDocuments();
        personalDocuments.add(personalDocument);
        employee.setPersonalDocuments(personalDocuments);

        employeeRepo.save(employee);

        return personalDocument;
    }

    public PersonalDocument updatePersonalDocuments(Integer userID, String oldTitle, String newType, String newTitle, MultipartFile file, String comment, String documentStatus) throws IOException {
        Employee employee = employeeRepo.findByUserID(userID);
        if (employee == null) {
            throw new EmployeeNotFoundException(userID);
        }

        PersonalDocument personalDocumentToDelete = null;
        PersonalDocument personalDocumentToUpdate = null;
        for (PersonalDocument personalDocument : employee.getPersonalDocuments()) {
            if(personalDocument.getTitle().equals(oldTitle) && personalDocument.getDocumentStatus().equals("Approved")) {
                throw new DocumentNotFoundException("Update fail. Document has been approved.");
            }
            if (personalDocument.getTitle().equals(oldTitle)) {
                personalDocumentToDelete = personalDocument;
            } else if (personalDocument.getTitle().equals(newTitle)) {
                personalDocumentToUpdate = personalDocument;
            }
        }

        if (personalDocumentToDelete != null) {
            employee.getPersonalDocuments().remove(personalDocumentToDelete);
        }

        if (file != null) {
            String fileName = uploadFile(file);
            if (personalDocumentToUpdate == null) {
                personalDocumentToUpdate = new PersonalDocument();
                personalDocumentToUpdate.setTitle(newTitle);
                personalDocumentToUpdate.setCreateDate(String.valueOf(LocalDate.now()));
                employee.getPersonalDocuments().add(personalDocumentToUpdate);
            }
            personalDocumentToUpdate.setType(newType);
            personalDocumentToUpdate.setComment(comment);
            personalDocumentToUpdate.setPath(fileName);
            personalDocumentToUpdate.setDocumentStatus(documentStatus);
        }

        employeeRepo.save(employee);

        return personalDocumentToUpdate;
    }

    public String uploadFile(MultipartFile file) {
        File fileObj = convertMultiPartFileToFile(file);
        String fileName = file.getOriginalFilename();
        s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
        fileObj.delete();
        return "Your file" + fileName + "has successfully uploaded! Please wait for HR to approve.";
    }

    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.error("Error converting multipartFile to file", e);
        }
        return convertedFile;
    }

    public byte[] downloadFile(String fileName) {
        S3Object s3Object = s3Client.getObject(bucketName, fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try {
            byte[] content = IOUtils.toByteArray(inputStream);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}