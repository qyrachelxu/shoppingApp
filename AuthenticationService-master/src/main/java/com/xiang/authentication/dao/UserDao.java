package com.xiang.authentication.dao;

import com.xiang.authentication.config.HibernateConfigUtil;
import com.xiang.authentication.domain.entity.User;
import com.xiang.authentication.exception.ZeroOrManyException;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import com.xiang.authentication.security.PasswordEncryptor;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;
import java.sql.Timestamp;

@Repository
public class UserDao {
    public String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
    }
    public String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
    }
    public Optional<User> loadUserByUsername(String username) {
        List<User> users = getAllUser();
        return users.stream().filter(user -> username.equals(user.getUsername())).findAny();
    }

    public List<User> getAllUser() {
        List<User> result = null;

        Session session = null;
        try {
            session = HibernateConfigUtil.openSession();
            session.beginTransaction();

            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);

            Root<User> root = criteriaQuery.from(User.class);
            criteriaQuery.select(root);

            result = session.createQuery(criteriaQuery).getResultList();

            for (User u: result) {
                Hibernate.initialize(u.getUserRoles());
            }

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

    public List<User> getUserByUsername(String username) {
        List<User> result = null;

        Session session = null;
        try {
            session = HibernateConfigUtil.openSession();
            session.beginTransaction();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);

            Root<User> root = criteriaQuery.from(User.class);
            criteriaQuery.select(root);
            criteriaQuery.where(criteriaBuilder.equal(root.get("username"), username));

            result = session.createQuery(criteriaQuery).getResultList();
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
        }

        return result;
    }

    public List<User> getUserByEmail(String email) {
        List<User> result = null;

        Session session = null;
        try {
            session = HibernateConfigUtil.openSession();
            session.beginTransaction();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);

            Root<User> root = criteriaQuery.from(User.class);
            criteriaQuery.select(root);
            criteriaQuery.where(criteriaBuilder.equal(root.get("email"), email));

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

    public User createNewUser(Integer id, String username, String password, String email) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPassword(PasswordEncryptor.encodePassword(password));
        user.setEmail(email);
        user.setCreateDate(new Timestamp(System.currentTimeMillis()));
        user.setLastModificationDate(new Timestamp(System.currentTimeMillis()));
        user.setActiveFlag(true);

        Session session = null;
        try {
            session = HibernateConfigUtil.openSession();
            session.beginTransaction();
            session.save(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
        } finally {
            session.close();
        }
        return user;
    }

    public User getCurrentUser() throws ZeroOrManyException {
        String username = getCurrentUsername();
        List<User> result = getUserByUsername(username);
        if (result.size() == 0 || result.size() > 1) {
            throw new ZeroOrManyException("The current user return either 0 or more than 1 result.");
        }
        return result.get(0);
    }

    public void updatePassword(User user, String password) {
        Session session = null;
        try {
            session = HibernateConfigUtil.openSession();
            session.beginTransaction();

            String qryString = "UPDATE User u SET u.password =: uPassword, u.lastModificationDate =: uDate " +
                    "WHERE u.id =: uId";
            Query query = session.createQuery(qryString);
            query.setParameter("uPassword", PasswordEncryptor.encodePassword(password));
            query.setParameter("uDate", new Timestamp(System.currentTimeMillis()));
            query.setParameter("uId", user.getId());
            query.executeUpdate();

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
}
