package com.xiang.authentication.dao;

import com.xiang.authentication.config.HibernateConfigUtil;
import com.xiang.authentication.domain.entity.Role;
import com.xiang.authentication.exception.ZeroOrManyException;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public class RoleDao {

    public Role getRoleById(Integer id) {
        List<Role> result = null;
        Session session = null;
        try {
            session = HibernateConfigUtil.openSession();
            session.beginTransaction();

            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Role> criteriaQuery = criteriaBuilder.createQuery(Role.class);

            Root<Role> root = criteriaQuery.from(Role.class);
            criteriaQuery.select(root);
            criteriaQuery.where(criteriaBuilder.equal(root.get("id"), id));

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

        return result.get(0);
    }
}
