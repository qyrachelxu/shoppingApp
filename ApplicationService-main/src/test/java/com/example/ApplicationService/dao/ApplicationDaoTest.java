package com.example.ApplicationService.dao;

import com.example.ApplicationService.domain.DigitalDocument;
import com.example.ApplicationService.domain.WorkFlow;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static reactor.core.publisher.Mono.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ApplicationDaoTest {

    @Mock
    private SessionFactory sessionFactory;

    @InjectMocks
    private ApplicationDao applicationDao;

    @Mock
    private Session sessionTemp;

    private static final String WORKFLOW_TABLE_NAME = "workflow";
    private static final String DOCUMENT_TABLE_NAME = "digitaldocument";

    private static final WorkFlow WORKFLOW_1 = new WorkFlow(1,1,null, null, "Approved", "workflow1");
    private static final WorkFlow WORKFLOW_2 = new WorkFlow(2,2,null, null, "Rejected", "workflow2");

    @Test
    public void testGetAllDocument() {
        // Arrange
        List<DigitalDocument> expectedDocuments = new ArrayList<>();
        expectedDocuments.add(new DigitalDocument(1, "Contract", true, "xxx.pdf", "HR Version", "Contract_origin.pdf"));
        expectedDocuments.add(new DigitalDocument(2, "Contract", true, "xxx.pdf", "User Edition", "Contract_id2.pdf"));

        Session session = mock(Session.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        CriteriaQuery<DigitalDocument> cq = mock(CriteriaQuery.class);
        Root<DigitalDocument> root = mock(Root.class);
        Query<DigitalDocument> query = mock(Query.class);

        Mockito.when(sessionFactory.getCurrentSession()).thenReturn(session);
        Mockito.when(session.getCriteriaBuilder()).thenReturn(cb);
        Mockito.when(cb.createQuery(DigitalDocument.class)).thenReturn(cq);
        Mockito.when(cq.from(DigitalDocument.class)).thenReturn(root);
        Mockito.when(cq.select(root)).thenReturn(cq);
        Mockito.when(session.createQuery(cq)).thenReturn(query);
        Mockito.when(query.getResultList()).thenReturn(expectedDocuments);

        // Act
        List<DigitalDocument> actualDocuments = applicationDao.getAllDocument();

        // Assert
        assertEquals(expectedDocuments, actualDocuments);
    }

    @Test
    public void testgetAllApplication() {
        // Arrange
        List<WorkFlow> expectedWorkFlow = new ArrayList<>();
        expectedWorkFlow.add(WORKFLOW_1);
        expectedWorkFlow.add(WORKFLOW_2);

        Session session = mock(Session.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        CriteriaQuery<WorkFlow> cq = mock(CriteriaQuery.class);
        Root<WorkFlow> root = mock(Root.class);
        Query<WorkFlow> query = mock(Query.class);

        Mockito.when(sessionFactory.getCurrentSession()).thenReturn(session);
        Mockito.when(session.getCriteriaBuilder()).thenReturn(cb);
        Mockito.when(cb.createQuery(WorkFlow.class)).thenReturn(cq);
        Mockito.when(cq.from(WorkFlow.class)).thenReturn(root);
        Mockito.when(cq.select(root)).thenReturn(cq);
        Mockito.when(session.createQuery(cq)).thenReturn(query);
        Mockito.when(query.getResultList()).thenReturn(expectedWorkFlow);

        // Act
        List<WorkFlow> actualWorkFlow = applicationDao.getAllApplication();

        // Assert
        assertEquals(expectedWorkFlow, actualWorkFlow);
    }

    @Test
    public void testAddDocument() {
        Session session = mock(Session.class);
        Mockito.when(sessionFactory.getCurrentSession()).thenReturn(session);
        doNothing().when(session).saveOrUpdate(any(DigitalDocument.class));

        String fileType = "pdf";
        String fileName = "test.pdf";
        String filePath = "/documents/test.pdf";
        String fileDescription = "Test document";

        applicationDao.addDocument(fileType, fileName, filePath, fileDescription);

        ArgumentCaptor<DigitalDocument> captor = ArgumentCaptor.forClass(DigitalDocument.class);
        Mockito.verify(session, times(1)).saveOrUpdate(captor.capture());
        DigitalDocument document = captor.getValue();

        assertEquals(fileName, document.getDocumentTitle());
        assertEquals(fileType, document.getDocumentType());
        assertEquals(filePath, document.getDocumentPath());
        assertEquals(fileDescription, document.getDocumentDescription());
        assertTrue(document.getDocumentIsRequired());
    }

    @Test
    public void testAddDocumentHR() {
        Session session = mock(Session.class);
        Mockito.when(sessionFactory.getCurrentSession()).thenReturn(session);
        doNothing().when(session).saveOrUpdate(any(DigitalDocument.class));

        String fileType = "pdf";
        String fileName = "test.pdf";
        String filePath = "/documents/test.pdf";
        String hrVersion = "HR document version";
        Boolean required = Boolean.TRUE;

        applicationDao.addDocumentHR(fileType, fileName, filePath, hrVersion, required);

        ArgumentCaptor<DigitalDocument> captor = ArgumentCaptor.forClass(DigitalDocument.class);
        verify(session, times(1)).saveOrUpdate(captor.capture());
        DigitalDocument document = captor.getValue();

        assertEquals(fileName, document.getDocumentTitle());
        assertEquals(fileType, document.getDocumentType());
        assertEquals(filePath, document.getDocumentPath());
        assertEquals(hrVersion, document.getDocumentDescription());
        assertTrue(document.getDocumentIsRequired());
    }
}
