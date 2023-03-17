package com.example.ApplicationService.dao;

import com.example.ApplicationService.domain.DigitalDocument;
import com.example.ApplicationService.domain.WorkFlow;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class ApplicationDao {
    @Autowired
    private SessionFactory sessionFactory;


    public List<WorkFlow> getAllApplication() {
        Session session;
        List<WorkFlow> applicationList = new ArrayList<>();
        try {
            session = sessionFactory.getCurrentSession();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<WorkFlow> cq = cb.createQuery(WorkFlow.class);
            Root<WorkFlow> root = cq.from(WorkFlow.class);
            cq.select(root);
            applicationList = session.createQuery(cq).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return applicationList;
    }

    public List<DigitalDocument> getAllDocument() {
        Session session;
        List<DigitalDocument> documentList = new ArrayList<>();
        try {
            session = sessionFactory.getCurrentSession();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<DigitalDocument> cq = cb.createQuery(DigitalDocument.class);
            Root<DigitalDocument> root = cq.from(DigitalDocument.class);
            cq.select(root);
            documentList = session.createQuery(cq).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return documentList;
    }
    public void addApplication(Integer userID) {
        Session session;
        try {
            session = sessionFactory.getCurrentSession();
            WorkFlow workFlow = new WorkFlow();
            workFlow.setUserId(userID);
            workFlow.setWorkflowStatus("Incomplete");
            Date date = new Date();
            workFlow.setWorkflowDateCreated(new Timestamp(date.getTime()));
            workFlow.setWorkflowDateModified(new Timestamp(date.getTime()));
            session.saveOrUpdate(workFlow);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addDocument(String fileType, String fileName, String filePath, String fileDescription) {
        Session session;
        try {
            session = sessionFactory.getCurrentSession();
            DigitalDocument document = new DigitalDocument();
            document.setDocumentTitle(fileName);
            document.setDocumentType(fileType);
            document.setDocumentPath(filePath);
            document.setDocumentDescription(fileDescription);
            document.setDocumentIsRequired(Boolean.TRUE);
            session.saveOrUpdate(document);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void submitApplication(Integer userID) {
        Session session;
        try {
            session = sessionFactory.getCurrentSession();
            String hql = "UPDATE WorkFlow SET workflowStatus = :status, workflowComment = :comment, workflowDateModified = current_timestamp WHERE userId = :id";
            Query query = session.createQuery(hql);
            query.setParameter("status", "Pending");
            query.setParameter("id", userID);
            query.setParameter("comment", "Not Yet Reviewed");
            query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void updateApplication(WorkFlow curApplication, String status, String comment) {
        Session session;
        try {
            session = sessionFactory.getCurrentSession();
            String hql = "UPDATE WorkFlow SET workflowStatus = :status, workflowComment = :comment, workflowDateModified = current_timestamp WHERE workflowId = :id";
            Query query = session.createQuery(hql);
            query.setParameter("status", status);
            query.setParameter("id", curApplication.getWorkflowId());
            query.setParameter("comment", comment);
            query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteDigitalDocument(DigitalDocument document) {
        Session session;
        try {
            session = sessionFactory.getCurrentSession();
            String hql = "DELETE FROM DigitalDocument WHERE documentId = :id";
            Query query = session.createQuery(hql);
            query.setParameter("id", document.getDocumentId());
            int result = query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addDocumentHR(String fileType, String fileName, String filePath, String hrVersion, Boolean required) {
        Session session;
        try {
            session = sessionFactory.getCurrentSession();
            DigitalDocument document = new DigitalDocument();
            document.setDocumentTitle(fileName);
            document.setDocumentType(fileType);
            document.setDocumentPath(filePath);
            document.setDocumentDescription(hrVersion);
            document.setDocumentIsRequired(required);
            session.saveOrUpdate(document);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateApplicationTime(Integer userID) {
        Session session;
        try {
            session = sessionFactory.getCurrentSession();
            String hql = "UPDATE WorkFlow SET workflowDateModified = current_timestamp WHERE userId = :id";
            Query query = session.createQuery(hql);
            query.setParameter("id", userID);
            query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
