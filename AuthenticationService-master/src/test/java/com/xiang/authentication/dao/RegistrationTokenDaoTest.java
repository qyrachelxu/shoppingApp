package com.xiang.authentication.dao;

import com.xiang.authentication.config.HibernateConfigUtil;
import com.xiang.authentication.domain.entity.RegistrationToken;
import com.xiang.authentication.domain.entity.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;

import static org.mockito.Mockito.when;

public class RegistrationTokenDaoTest {

    @Mock
    private HibernateConfigUtil hibernateConfigUtil;

    @Mock
    private Session session;

    @InjectMocks
    private RegistrationTokenDao registrationTokenDao;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateNewRegistrationToken() {
        // Arrange
        String token = "abcd1234";
        String email = "test@example.com";
        Timestamp expirationDate = new Timestamp(System.currentTimeMillis() + 3600 * 1000);
        User createdBy = new User();

        when(hibernateConfigUtil.openSession()).thenReturn(session);
        when(session.getTransaction()).thenReturn((Transaction) session);

        // Act
        RegistrationToken registrationToken = registrationTokenDao.createNewRegistrationToken(token, email, expirationDate, createdBy);

        // Assert
        Assert.assertNotNull(registrationToken);
        Assert.assertEquals(token, registrationToken.getToken());
        Assert.assertEquals(email, registrationToken.getEmail());
        Assert.assertEquals(expirationDate, registrationToken.getExpirationDate());
        Assert.assertEquals(createdBy, registrationToken.getCreatedBy());
    }
}
