package com.example.Inmopro.v1;

import com.example.Inmopro.v1.Controller.Request.RequestResponse;
import com.example.Inmopro.v1.Dto.Request.RequestRequest;
import com.example.Inmopro.v1.Model.Calculator;
import com.example.Inmopro.v1.Model.Request.FollowUpRequest;
import com.example.Inmopro.v1.Model.Request.Request;
import com.example.Inmopro.v1.Model.Request.RequestStatus;
import com.example.Inmopro.v1.Model.Request.RequestType;
import com.example.Inmopro.v1.Model.Users.Roles;
import com.example.Inmopro.v1.Model.Users.Users;
import com.example.Inmopro.v1.Repository.*;
import com.example.Inmopro.v1.Service.Jwt.JwtService;
import com.example.Inmopro.v1.Service.Mail.MailService;
import com.example.Inmopro.v1.Service.Request.RequestService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


import java.io.IOException;
import java.util.List;
import java.util.Optional;

import jakarta.mail.MessagingException;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class RequestServiceTest {

    @Mock
    private FollowUpRequestRepository followUpRequestRepository;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private RequestTypeRepository requestTypeRepository;

    @Mock
    private RequestStatusRepository requestStatusRepository;

    @Mock
    private MailService mailService;

    @InjectMocks
    private RequestService requestService;

    @Mock
    private HttpServletRequest httpRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreate_Success() throws IOException, MessagingException {
        RequestRequest requestRequest = new RequestRequest();
        requestRequest.setRequestType(1);
        requestRequest.setDescription("Test description");

        Users mockUser = new Users();
        Roles mockRole = new Roles();
        mockRole.setId(1);
        mockUser.setRole(mockRole);
        mockUser.setEmail("test@example.com");

        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer test-token");
        when(jwtService.getUsernameFromToken("test-token")).thenReturn("test@example.com");
        when(usersRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));

        RequestType mockRequestType = new RequestType();
        mockRequestType.setId(1);
        when(requestTypeRepository.findById(1)).thenReturn(Optional.of(mockRequestType));

        RequestStatus mockRequestStatus = new RequestStatus();
        mockRequestStatus.setId(1);
        when(requestStatusRepository.findById(1)).thenReturn(Optional.of(mockRequestStatus));

        RequestResponse response = requestService.create(requestRequest, httpRequest);

        assertNotNull(response);
        assertEquals("Request created", response.getMessage());

        verify(requestRepository).save(any(Request.class));
        verify(followUpRequestRepository).save(any(FollowUpRequest.class));
        verify(mailService).sendHtmlEmail(eq("test@example.com"), eq("Request created"), anyString());
    }

    @Test
    void testCreate_InvalidAuthorization() throws IOException, MessagingException {
        RequestRequest requestRequest = new RequestRequest();
        requestRequest.setRequestType(1);
        requestRequest.setDescription("Test description");

        when(httpRequest.getHeader("Authorization")).thenReturn(null);


        RequestResponse response = requestService.create(requestRequest, httpRequest);

        assertNotNull(response);
        assertEquals("Invalid request", response.getMessage());

        verify(requestRepository, never()).save(any(Request.class));
        verify(followUpRequestRepository, never()).save(any(FollowUpRequest.class));
        verify(mailService, never()).sendHtmlEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testGetFollowUpRequest_Success() {
        List<FollowUpRequest> mockFollowUpRequests = List.of(new FollowUpRequest());

        when(followUpRequestRepository.findAll()).thenReturn(mockFollowUpRequests);

        List<FollowUpRequest> result = requestService.getFollowUpRequest();
        
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(followUpRequestRepository).findAll();
    }



}
