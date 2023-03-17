package com.example.ApplicationService.service;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.example.ApplicationService.dao.ApplicationDao;
import com.example.ApplicationService.domain.DigitalDocument;
import com.example.ApplicationService.domain.WorkFlow;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.service.Service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.apache.commons.io.IOUtils;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.transaction.Transactional;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ApplicationServiceTest {

    @Mock
    AmazonS3 s3Client;

    @Mock
    private ApplicationDao applicationDao;

    @InjectMocks
    private ApplicationService applicationService;

    @Test
    @Transactional
    public void testaddApplication() {
        applicationDao.addApplication(99);
        verify(applicationDao, times(1)).addApplication(99);
    }

    @Test
    @Transactional
    public void testUploadFileHR() throws Exception {
        // Create a mock MultipartFile
        File testFile = ResourceUtils.getFile("classpath:test.pdf");
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.pdf", "application/pdf", Files.readAllBytes(testFile.toPath()));

        // Mock S3 client to verify file upload
        when(s3Client.putObject(any(PutObjectRequest.class))).thenReturn(null);

        // Call the uploadFileHR method
        applicationService.uploadFileHR(mockFile, true, "Contract", "test.pdf");

        // Verify that the file was uploaded to S3
        ArgumentCaptor<PutObjectRequest> putObjectRequestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(putObjectRequestCaptor.capture());
        PutObjectRequest putObjectRequest = putObjectRequestCaptor.getValue();
        assertEquals("test.pdf", putObjectRequest.getKey());

        // Verify that the document was added to the database
        verify(applicationDao).addDocumentHR(eq("Contract"), eq("test.pdf"), anyString(), eq("HR Version"), eq(true));
    }

    @Test
    @Transactional
    public void testUploadFile() throws Exception {
        File testFile = ResourceUtils.getFile("classpath:test.pdf");
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.pdf", "application/pdf", Files.readAllBytes(testFile.toPath()));

        when(s3Client.putObject(any(PutObjectRequest.class))).thenReturn(null);

        applicationService.uploadFile(mockFile, 1, "Contract", "test.pdf");

        ArgumentCaptor<PutObjectRequest> putObjectRequestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(putObjectRequestCaptor.capture());
        PutObjectRequest putObjectRequest = putObjectRequestCaptor.getValue();
        assertEquals("test.pdf", putObjectRequest.getKey());

        verify(applicationDao).addDocument(eq("Contract"), eq("test.pdf"), anyString(), eq("User Edition"));
    }

    @Test
    public void testCheckApplication_NoPreviousSubmission() {
        Integer userId = 1;
        when(applicationDao.getAllApplication()).thenReturn(new ArrayList<>());

        String result = applicationService.checkApplication(userId);

        assertEquals("No previous submission. Go to Application Page", result);
    }

    @Test
    public void testCheckApplication_PreviousSubmissionWithStatusPending() {
        Integer userId = 99;
        WorkFlow workFlow = new WorkFlow(99, userId,  null, null, "Pending" , "xx");
        List<WorkFlow> allApplications = Arrays.asList(workFlow);
        when(applicationDao.getAllApplication()).thenReturn(allApplications);

        String result = applicationService.checkApplication(userId);

        assertEquals("Please wait for HR to review your application. Go to Pending Page", result);
    }
    @Test
    @Transactional
    public void testCheckApplication_PreviousSubmissionApproved() {
        Integer userId = 99;
        WorkFlow workFlow = new WorkFlow(99, userId,  null, null, "Approved" , "xx");
        List<WorkFlow> allApplications = Arrays.asList(workFlow);
        when(applicationDao.getAllApplication()).thenReturn(allApplications);
        String result = applicationService.checkApplication(userId);
        assertEquals("Approved! Go to HomePage", result);
    }

    @Test
    @Transactional
    public void testCheckApplication_PreviousSubmissionRejected() {
        Integer userId = 99;
        WorkFlow workFlow = new WorkFlow(99, userId,  null, null, "Rejected" , "xx");
        List<WorkFlow> allApplications = Arrays.asList(workFlow);
        when(applicationDao.getAllApplication()).thenReturn(allApplications);
        String result = applicationService.checkApplication(userId);
        assertEquals("Rejected! Go to Rejected Page", result);
    }

    @Test
    @Transactional
    public void testCheckApplication_PreviousSubmissionIncomplete() {
        Integer userId = 99;
        WorkFlow workFlow = new WorkFlow(99, userId,  null, null, "Incomplete" , "xx");
        List<WorkFlow> allApplications = Arrays.asList(workFlow);
        when(applicationDao.getAllApplication()).thenReturn(allApplications);
        String result = applicationService.checkApplication(userId);
        assertEquals("You last application wasn't finished. Go back to your Application", result);
    }

    @Test
    @Transactional
    public void testSubmitApplication() {
        // Arrange
        Integer userId = 99;
        WorkFlow mostRecentWorkFlow = new WorkFlow();
        mostRecentWorkFlow.setWorkflowId(1);
        mostRecentWorkFlow.setUserId(userId);
        mostRecentWorkFlow.setWorkflowDateCreated(new Timestamp(System.currentTimeMillis() - 100000));
        mostRecentWorkFlow.setWorkflowDateModified(new Timestamp(System.currentTimeMillis() - 50000));
        mostRecentWorkFlow.setWorkflowStatus("Approved");
        mostRecentWorkFlow.setWorkflowComment("Reviewed");
        List<WorkFlow> allApplications = new ArrayList<>();
        allApplications.add(mostRecentWorkFlow);
        ApplicationDao applicationDao = mock(ApplicationDao.class);
        when(applicationDao.getAllApplication()).thenReturn(allApplications);
        doNothing().when(applicationDao).submitApplication(userId);

        ApplicationService service = new ApplicationService(applicationDao, s3Client);

        service.submitApplication(userId);

        verify(applicationDao, times(1)).submitApplication(userId);
    }

    @Test
    @Transactional
    public void testGetAllDocumentView() {
        List<DigitalDocument> allDocuments = new ArrayList<>();
        DigitalDocument doc1 = new DigitalDocument();
        doc1.setDocumentId(1);
        doc1.setDocumentType("Contract");
        doc1.setDocumentIsRequired(true);
        doc1.setDocumentDescription("HR Version");
        doc1.setDocumentTitle("HR_CONTRACT.pdf");
        doc1.setDocumentPath("ASD");
        DigitalDocument doc2 = new DigitalDocument();
        doc2.setDocumentId(2);
        doc2.setDocumentType("Information");
        doc2.setDocumentIsRequired(true);
        doc2.setDocumentDescription("asd");
        doc2.setDocumentTitle("asd.pdf");
        doc2.setDocumentPath("ASD");
        allDocuments.add(doc1);
        allDocuments.add(doc2);

        when(applicationDao.getAllDocument()).thenReturn(allDocuments);

        List<DigitalDocument> filteredDocuments = applicationService.getAllDocumentView();

        assertEquals(1, filteredDocuments.size());
        assertEquals("HR Version", filteredDocuments.get(0).getDocumentDescription());
    }

    @Test
    @Transactional
    public void testGetUserDocumentByID() {
        // Set up test data
        List<DigitalDocument> allDocuments = new ArrayList<>();
        DigitalDocument doc1 = new DigitalDocument();
        doc1.setDocumentId(1);
        doc1.setDocumentType("Contract");
        doc1.setDocumentIsRequired(true);
        doc1.setDocumentDescription("User Version");
        doc1.setDocumentTitle("Contract_id99.pdf");
        doc1.setDocumentPath("ASD");

        DigitalDocument doc2 = new DigitalDocument();
        doc2.setDocumentId(2);
        doc2.setDocumentType("Information");
        doc2.setDocumentIsRequired(true);
        doc2.setDocumentDescription("asd");
        doc2.setDocumentTitle("asd.pdf");
        doc2.setDocumentPath("ASD");
        allDocuments.add(doc1);
        allDocuments.add(doc2);

        when(applicationDao.getAllDocument()).thenReturn(allDocuments);
        List<DigitalDocument> filteredDocuments = applicationService.getUserDocumentByID(99);

        // Verify that the correct documents were returned
        assertEquals(1, filteredDocuments.size());
        assertEquals("Contract_id99.pdf", filteredDocuments.get(0).getDocumentTitle());
        assertEquals(1, filteredDocuments.get(0).getDocumentId());
    }

    @Test
    @Transactional
    public void testgetAllDigitalDocuments() {
        List<DigitalDocument> allDocuments = new ArrayList<>();
        DigitalDocument doc1 = new DigitalDocument();
        doc1.setDocumentId(1);
        doc1.setDocumentType("Contract");
        doc1.setDocumentIsRequired(true);
        doc1.setDocumentDescription("User Version");
        doc1.setDocumentTitle("Contract_id99.pdf");
        doc1.setDocumentPath("ASD");
        allDocuments.add(doc1);

        when(applicationDao.getAllDocument()).thenReturn(allDocuments);
        List<DigitalDocument> res = applicationService.getAllDigitalDocuments();

        assertEquals(1, res.size());
        assertEquals(1, res.get(0).getDocumentId());
    }

    @Test
    @Transactional
    public void testGetDigitalDocumentByTitle() {
        DigitalDocument document = new DigitalDocument();
        document.setDocumentId(1);
        document.setDocumentType("pdf");
        document.setDocumentIsRequired(true);
        document.setDocumentPath("s3://my-bucket/docs/doc1.pdf");
        document.setDocumentDescription("Test document");
        document.setDocumentTitle("doc1.pdf");

        when(applicationDao.getAllDocument()).thenReturn(Arrays.asList(document));

        Optional<DigitalDocument> result = applicationService.getDigitalDocumentByTitle("doc1.pdf");
        assertTrue(result.isPresent());
        assertEquals(result.get().getDocumentTitle(), "doc1.pdf");
    }

    @Test
    @Transactional
    public void testDeleteDigitalDocument() {
        DigitalDocument document = new DigitalDocument();
        document.setDocumentId(1);
        document.setDocumentType("pdf");
        document.setDocumentIsRequired(true);
        document.setDocumentPath("s3://my-bucket/docs/doc1.pdf");
        document.setDocumentDescription("Test document");
        document.setDocumentTitle("test.pdf");

        when(applicationDao.getAllDocument()).thenReturn(Arrays.asList(document));

        applicationService.deleteDigitalDocument(document);

        assertEquals(applicationDao.getAllDocument().size(), 1);

        verify(applicationDao).deleteDigitalDocument(eq(document));
    }

    @Test
    @Transactional
    public void testGetApplicationComment() {
        List<WorkFlow> workFlows = new ArrayList<>();
        WorkFlow testApplication = new WorkFlow();
        testApplication.setUserId(123);
        testApplication.setWorkflowComment("Test comment");
        testApplication.setWorkflowId(123);
        testApplication.setWorkflowStatus("Rejected");
        workFlows.add(testApplication);

        when(applicationDao.getAllApplication()).thenReturn(Arrays.asList(testApplication));

        String comment = applicationService.getApplicationComment(123);

        assertEquals("Test comment", comment);

        String nonExistentComment = applicationService.getApplicationComment(456);

        assertEquals("No Application Found", nonExistentComment);
    }

    @Test
    @Transactional
    public void testGetUserIdList() {
        // Create some test data

        List<WorkFlow> allApplcaition = new ArrayList<>();
        WorkFlow workflow1 = new WorkFlow();
        workflow1.setUserId(1);
        workflow1.setWorkflowStatus("Approved");

        WorkFlow workflow2 = new WorkFlow();
        workflow2.setUserId(2);
        workflow2.setWorkflowStatus("Pending");

        WorkFlow workflow3 = new WorkFlow();
        workflow3.setUserId(3);
        workflow3.setWorkflowStatus("Approved");

        allApplcaition.add(workflow1);
        allApplcaition.add(workflow2);
        allApplcaition.add(workflow3);
        when(applicationDao.getAllApplication()).thenReturn(allApplcaition);

        // Call the method being tested
        List<Integer> userIdList = applicationService.getUserIdList("Approved");

        // Verify the results
        assertEquals(2, userIdList.size());
        assertTrue(userIdList.contains(1));
        assertTrue(userIdList.contains(3));
    }

    @Test
    @Transactional
    public void testGetApplicationStatusWithValidUserId() {
        // Mock the data returned by the DAO
        List<WorkFlow> allApplications = new ArrayList<>();
        WorkFlow workFlow = new WorkFlow();
        workFlow.setUserId(1234);
        workFlow.setWorkflowStatus("Pending");
        allApplications.add(workFlow);
        when(applicationDao.getAllApplication()).thenReturn(allApplications);

        // Call the method being tested
        String result = applicationService.getApplicationStatus(1234);

        // Verify the result
        assertEquals("Pending", result);
    }

    @Test
    @Transactional
    public void testGetApplicationStatusWithInvalidUserId() {
        List<WorkFlow> allApplications = new ArrayList<>();
        WorkFlow workFlow = new WorkFlow();
        workFlow.setUserId(1234);
        workFlow.setWorkflowStatus("Pending");
        allApplications.add(workFlow);
        when(applicationDao.getAllApplication()).thenReturn(allApplications);

        String result = applicationService.getApplicationStatus(5678);

        assertEquals("No Application Found", result);
    }

    @Test
    @Transactional
    public void testGetApplicationStatusWithNoApplications() {
        List<WorkFlow> allApplications = new ArrayList<>();
        when(applicationDao.getAllApplication()).thenReturn(allApplications);

        String result = applicationService.getApplicationStatus(1234);

        assertEquals("No Application Found", result);
    }


    @Test
    @Transactional
    public void testUpdateApplication() {
        WorkFlow curApplication = new WorkFlow();
        curApplication.setUserId(999);
        curApplication.setWorkflowStatus("Pending");

        String status = "Approved";
        String comment = "Application approved";
        applicationService.updateApplication(curApplication, status, comment);

        verify(applicationDao).updateApplication(curApplication, status, comment);
    }

    @Test
    public void testGetDigitalDocumentByID() {
        DigitalDocument document1 = new DigitalDocument(1, "Contract", true, "asd", "Hr", "Contract_id99.pdf");
        DigitalDocument document2 = new DigitalDocument(2, "Contract", true, "asd", "Hr", "Contract_id92.pdf");
        DigitalDocument document3 = new DigitalDocument(3, "Agreement", true, "asd", "Hr", "Agreement_id91.pdf");

        List<DigitalDocument> allDocuments = Arrays.asList(document1, document2, document3);
        when(applicationService.getAllDigitalDocuments()).thenReturn(allDocuments);

        Integer documentID = 3;
        Optional<DigitalDocument> result = applicationService.getDigitalDocumentByID(documentID);

        assertEquals(document3, result.get());
    }

    @Test
    @Transactional
    public void testGetApplicaionByUserId() {
        WorkFlow application1 = new WorkFlow(1, 99, null, null, "Approved" , "good");
        WorkFlow application2 = new WorkFlow(4, 92, null, null, "Approved" , "asd");

        List<WorkFlow> allApplications = Arrays.asList(application1, application2);
        when(applicationDao.getAllApplication()).thenReturn(allApplications);

        Integer userID = 99;
        Optional<WorkFlow> result = applicationService.getApplicaionByUserId(userID);
        assertEquals(application1, result.get());
    }
}


