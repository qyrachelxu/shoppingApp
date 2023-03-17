package com.example.ApplicationService.controller;

import com.example.ApplicationService.controller.CompositeController;
import com.example.ApplicationService.service.ApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.commons.io.IOUtils;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.transaction.Transactional;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CompositeControllerTest {

    @Mock
    private ApplicationService applicationService;

    private CompositeController compositeController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        compositeController = new CompositeController(applicationService);
        mockMvc = MockMvcBuilders.standaloneSetup(compositeController).build();
    }

    @Test
    public void testGetApplicationStatus() throws Exception {
        int userId = 1;
        String expectedStatus = "Approved";
        when(applicationService.getApplicationStatus(userId)).thenReturn(expectedStatus);
        mockMvc.perform(get("/application/" + userId + "/getstatus"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedStatus));
    }

    @Test
    public void testGetApplicationComment() throws Exception {
        int userId = 1;
        String expectedComment = "This is a comment.";
        when(applicationService.getApplicationComment(userId)).thenReturn(expectedComment);
        mockMvc.perform(get("/application/" + userId + "/getcomment"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedComment));
    }

    @Test
    public void testUpdateApplicationTime() throws Exception {
        int userId = 1;
        doNothing().when(applicationService).updateApplicationTime(userId);
        mockMvc.perform(put("/application/" + userId + "/updatetime"))
                .andExpect(status().isOk());
        verify(applicationService, times(1)).updateApplicationTime(userId);
    }

    @Test
    public void testGetPendingUserId() throws Exception {
        String status = "Pending";
        List<Integer> userIdList = Arrays.asList(1, 2, 3);
        when(applicationService.getUserIdList(status)).thenReturn(userIdList);
        mockMvc.perform(get("/application/" + status + "/list"))
                .andExpect(status().isOk())
                .andExpect(content().json("[1,2,3]"));
    }

    @Test
    public void testGetPendingUserId_InvalidStatus() throws Exception {
        String status = "InvalidStatus";
        List<Integer> userIdList = new ArrayList<>();
        when(applicationService.getUserIdList(status)).thenReturn(userIdList);
        mockMvc.perform(get("/application/" + status + "/list"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}
