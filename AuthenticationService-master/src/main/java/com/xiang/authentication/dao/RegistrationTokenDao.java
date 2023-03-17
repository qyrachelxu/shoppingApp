package com.xiang.authentication.dao;

import com.xiang.authentication.config.HibernateConfigUtil;
import com.xiang.authentication.domain.entity.RegistrationToken;
import com.xiang.authentication.domain.entity.User;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public class RegistrationTokenDao {
    public RegistrationToken createNewRegistrationToken(String token, String email, Timestamp expirationDate, User createdBy) {
        RegistrationToken registrationToken = new RegistrationToken();
        registrationToken.setToken(token);
        registrationToken.setEmail(email);
        registrationToken.setExpirationDate(expirationDate);
        registrationToken.setCreatedBy(createdBy);

        Session session = null;
        try {
            session = HibernateConfigUtil.openSession();
            session.beginTransaction();
            session.save(registrationToken);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
        } finally {
            session.close();
        }
        return registrationToken;
    }
}
