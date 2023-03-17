package com.xiang.authentication.dao;

import com.xiang.authentication.config.HibernateConfigUtil;
import com.xiang.authentication.domain.entity.User;
import com.xiang.authentication.domain.entity.Role;
import com.xiang.authentication.domain.entity.UserRole;
import com.xiang.authentication.security.PasswordEncryptor;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class UserRoleDao {

    public void createNewUserRoleForNewEmployee(User user, Role role) {
        UserRole userRole = new UserRole();
        userRole.setRoleId(role);
        userRole.setUserId(user);
        userRole.setActiveFlag(true);
        userRole.setCreateDate(new Timestamp(System.currentTimeMillis()));
        userRole.setLastModificationDate(new Timestamp(System.currentTimeMillis()));

        Session session = null;
        try {
            session = HibernateConfigUtil.openSession();
            session.beginTransaction();
            session.save(userRole);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
        } finally {
            session.close();
        }
    }
    public List<UserRole> getAllUserRoleByUser(User user) {
        List<UserRole> result = null;

        Session session = null;
        try {
            session = HibernateConfigUtil.openSession();
            session.beginTransaction();

            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<UserRole> criteriaQuery = criteriaBuilder.createQuery(UserRole.class);

            Root<UserRole> root = criteriaQuery.from(UserRole.class);
            criteriaQuery.select(root);
            criteriaQuery.where(criteriaBuilder.equal(root.get("userId"), user));

            result = session.createQuery(criteriaQuery).getResultList();

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
        } finally {
            session.close();
        }

        return result;
    }
}
