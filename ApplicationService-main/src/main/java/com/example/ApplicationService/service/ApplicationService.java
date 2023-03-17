package com.example.ApplicationService.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.example.ApplicationService.dao.ApplicationDao;
import com.example.ApplicationService.domain.DigitalDocument;
import com.example.ApplicationService.domain.WorkFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ApplicationService {

    @Value("${application.bucket.name}")
    private String bucketName;

    private AmazonS3 s3Client;

    private ApplicationDao applicationDao;

    @Autowired
    public ApplicationService(ApplicationDao applicationDao,  AmazonS3 s3Client) {
        this.applicationDao = applicationDao;
        this.s3Client = s3Client;
    }

    @Transactional
    public void uploadFileHR(MultipartFile file, Boolean required, String fileType, String fileName) {
        File fileObj = convertMultiPartFileToFile(file);
        s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
        String filePath = fileObj.getPath(); // Get the path of the uploaded file
        applicationDao.addDocumentHR(fileType, fileName, filePath, "HR Version", required);
        fileObj.delete();
    }
    @Transactional
    public void uploadFile(MultipartFile file, Integer userID, String fileType, String fileName) {
        File fileObj = convertMultiPartFileToFile(file);
        s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
        String filePath = fileObj.getPath(); // Get the path of the uploaded file
        applicationDao.addDocument(fileType, fileName, filePath, "User Edition");
        fileObj.delete();
    }
    @Transactional
    public void addApplication(Integer userID) {
        applicationDao.addApplication(userID);
    }

    @Transactional
    public String checkApplication(Integer userId) {
        List<WorkFlow> allApplications = applicationDao.getAllApplication();
        Optional<WorkFlow> possibleApplication = allApplications.stream()
                .filter(application -> application.getUserId().equals(userId)).findAny();
        if (!possibleApplication.isPresent()) {
            return "No previous submission. Go to Application Page";
        }
        WorkFlow curApplication = possibleApplication.get();
        if (curApplication.getWorkflowStatus().equals("Approved")) {
            return "Approved! Go to HomePage";
        } else if (curApplication.getWorkflowStatus().equals("Rejected")) {
            return "Rejected! Go to Rejected Page";
        } else if (curApplication.getWorkflowStatus().equals("Pending")) {
            return "Please wait for HR to review your application. Go to Pending Page";
        } else  {
            return  "You last application wasn't finished. Go back to your Application";
        }
    }

    @Transactional
    public void submitApplication(Integer userID1) {
        applicationDao.submitApplication(userID1);
    }

    @Transactional
    public List<DigitalDocument> getAllDocumentView() {
        List<DigitalDocument> alldocument = applicationDao.getAllDocument();
        return alldocument.stream().filter(each -> each.getDocumentDescription().equals("HR Version")).collect(Collectors.toList());
    }

    @Transactional
    public List<DigitalDocument> getUserDocumentByID(Integer id) {
        List<DigitalDocument> alldocument = applicationDao.getAllDocument();
        List<DigitalDocument> userDocuments = alldocument.stream()
                .filter(doc -> doc.getDocumentTitle().endsWith("_id" + id + ".pdf"))
                .collect(Collectors.toList());
        return userDocuments;
    }
    @Transactional
    public List<DigitalDocument> getAllDigitalDocuments() {
        return applicationDao.getAllDocument();
    }
    @Transactional
    public void updateApplication(WorkFlow curApplication, String status, String comment) {
        applicationDao.updateApplication(curApplication, status, comment);
    }
    @Transactional
    public Optional<DigitalDocument> getDigitalDocumentByTitle(String fileName) {
        List<DigitalDocument> alldocuments = this.getAllDigitalDocuments();
        return alldocuments.stream().filter(each -> each.getDocumentTitle().equals(fileName)).findAny();
    }
    @Transactional
    public void deleteDigitalDocument(DigitalDocument document) {
        String fileName = document.getDocumentTitle();
        s3Client.deleteObject(bucketName, fileName);
        applicationDao.deleteDigitalDocument(document);
    }
    @Transactional
    public Optional<DigitalDocument> getDigitalDocumentByID(Integer documentID) {
        List<DigitalDocument> alldocuments = this.getAllDigitalDocuments();
        return alldocuments.stream().filter(each -> each.getDocumentId().equals(documentID)).findAny();
    }

    @Transactional
    public Optional<WorkFlow> getApplicaionByUserId(Integer id) {
        List<WorkFlow> allApplications = applicationDao.getAllApplication();
        return allApplications.stream().filter(each -> each.getUserId().equals(id)).findAny();
    }
    @Transactional
    public String getApplicationStatus(Integer userID) {
        List<WorkFlow> allApplications = applicationDao.getAllApplication();
        Optional<WorkFlow> possibleApplication = allApplications.stream()
                .filter(application -> application.getUserId().equals(userID)).findAny();
        if (!possibleApplication.isPresent()) {
            return "No Application Found";
        }
        WorkFlow curApplication = possibleApplication.get();
        return curApplication.getWorkflowStatus();
    }
    @Transactional
    public String getApplicationComment(Integer userID) {
        List<WorkFlow> allApplications = applicationDao.getAllApplication();
        Optional<WorkFlow> possibleApplication = allApplications.stream()
                .filter(application -> application.getUserId().equals(userID)).findAny();
        if (!possibleApplication.isPresent()) {
            return "No Application Found";
        }
        WorkFlow curApplication = possibleApplication.get();
        return curApplication.getWorkflowComment();
    }
    @Transactional
    public void updateApplicationTime(Integer userID) {
        applicationDao.updateApplicationTime(userID);
    }

    @Transactional
    public List<Integer> getUserIdList(String status) {
        List<WorkFlow> allApplications = applicationDao.getAllApplication();
        return allApplications
                .stream()
                .filter(each -> each.getWorkflowStatus().equals(status))
                .map(WorkFlow::getUserId)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<WorkFlow> getAllApplication() {
        return applicationDao.getAllApplication();
    }
    public byte[] downloadFile(String fileName) {
        try (S3Object s3Object = s3Client.getObject(bucketName, fileName)) {
            S3ObjectInputStream inputStream = s3Object.getObjectContent();
            byte[] content = IOUtils.toByteArray(inputStream);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException ie) {
            ie.printStackTrace();
        }
        return convertedFile;
    }

}

