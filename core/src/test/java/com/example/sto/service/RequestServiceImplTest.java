package com.example.sto.service;

import com.example.sto.core.history.repository.RequestStatusHistoryRepository;
import com.example.sto.core.kafka.StatusChangeEvent;
import com.example.sto.core.request.exception.InvalidRequestStatusException;
import com.example.sto.core.request.exception.ResourceNotFoundException;
import com.example.sto.core.request.model.Request;
import com.example.sto.core.request.model.enums.RequestStatus;
import com.example.sto.core.request.repository.RequestRepository;
import com.example.sto.core.request.service.impl.RequestServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RequestServiceImplTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private RequestStatusHistoryRepository historyRepository;

    @Mock
    private KafkaTemplate<String, StatusChangeEvent> kafkaTemplate;

    @InjectMocks
    private RequestServiceImpl requestService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(requestService, "statusTopic", "topic");
    }

    @Test
    void createRequest_success() {
        Request request = Request.builder().id(1L).clientId(42L).description("desc").status(RequestStatus.NEW).build();
        when(requestRepository.save(any())).thenReturn(request);

        Request result = requestService.createRequest(42L, "desc");

        assertEquals(1L, result.getId());
        verify(historyRepository).save(argThat(h -> h.getToStatus()==RequestStatus.NEW));
        verify(kafkaTemplate).send(eq("topic"), any(StatusChangeEvent.class));
    }

    @Test
    void getByClientId_found() {
        Request dummyRequest = Request.builder()
                .id(1L)
                .clientId(42L)
                .status(RequestStatus.NEW)
                .description("dummyRequest")
                .createdAt(LocalDateTime.now())
                .build();
        List<Request> list = List.of(dummyRequest);
        when(requestRepository.findByClientId(1L)).thenReturn(list);
        List<Request> result = requestService.getByClientId(1L);
        assertSame(list, result);
    }

    @Test
    void getByClientId_notFound() {
        when(requestRepository.findByClientId(1L)).thenReturn(Collections.emptyList());
        assertThrows(ResourceNotFoundException.class, () -> requestService.getByClientId(1L));
    }

    @Test
    void changeStatus_success() {
        Request request = Request.builder().id(1L).clientId(42L).status(RequestStatus.NEW).build();
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(requestRepository.save(any())).thenReturn(request);

        Request updated = requestService.changeStatus(1L, RequestStatus.IN_PROGRESS, "user", "reason");

        assertEquals(RequestStatus.IN_PROGRESS, updated.getStatus());
        verify(historyRepository).save(argThat(h -> h.getToStatus()==RequestStatus.IN_PROGRESS));
        verify(kafkaTemplate).send(eq("topic"), any(StatusChangeEvent.class));
    }

    @Test
    void changeStatus_notFound() {
        when(requestRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> requestService.changeStatus(1L, RequestStatus.DONE, "u","reason"));
    }

    @Test
    void changeStatus_sameStatus() {
        Request r = Request.builder().id(1L).clientId(42L).status(RequestStatus.NEW).build();
        when(requestRepository.findById(1L)).thenReturn(Optional.of(r));
        assertThrows(InvalidRequestStatusException.class,
                () -> requestService.changeStatus(1L, RequestStatus.NEW, "u","reason"));
    }
}