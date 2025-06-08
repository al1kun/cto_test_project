package com.example.sto.core.request.service.impl;

import com.example.sto.common.domain.RequestStatus;
import com.example.sto.core.history.domain.RequestStatusHistory;
import com.example.sto.core.history.repository.RequestStatusHistoryRepository;
import com.example.sto.core.kafka.StatusChangeEvent;
import com.example.sto.core.request.domain.Request;
import com.example.sto.core.request.repository.RequestRepository;
import com.example.sto.core.request.service.RequestService;
import com.example.sto.notification.Notifier;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final RequestStatusHistoryRepository historyRepository;
    private final Notifier notifier;
    private final KafkaTemplate<String, StatusChangeEvent> kafkaTemplate;

    @Value("${sto.kafka.topic.request-status}")
    private String statusTopic;
    @Override
    @Transactional
    public Request createRequest(Long clientId, String description) {
        Request request = Request.builder()
                .clientId(clientId)
                .description(description)
                .status(RequestStatus.NEW)
                .build();
        request = requestRepository.save(request);

        historyRepository.save(RequestStatusHistory.builder()
                .requestId(request.getId())
                .fromStatus(null)
                .toStatus(RequestStatus.NEW)
                .changedBy("system")
                .reason("Request created")
                .build());

        kafkaTemplate.send(statusTopic,
                new StatusChangeEvent(request.getId(), null, RequestStatus.NEW, LocalDateTime.now())
        );

        return request;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Request> getByClientId(Long clientId) {
        return requestRepository.findByClientId(clientId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Request> getByStatus(RequestStatus status) {
        return requestRepository.findByStatus(status);
    }

    @Override
    @Transactional
    public Request changeStatus(Long requestId,
                                RequestStatus newStatus,
                                String changedBy,
                                String reason) {

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found: " + requestId));

        RequestStatus old = request.getStatus();
        request.setStatus(newStatus);
        request = requestRepository.save(request);

        historyRepository.save(RequestStatusHistory.builder()
                .requestId(request.getId())
                .fromStatus(old)
                .toStatus(newStatus)
                .changedBy(changedBy)
                .reason(reason)
                .build());

        if (newStatus == RequestStatus.DONE) {
            notifier.notifyClient(request.getClientId(),
                    "Your request No." + request.getId() + " is done!");
        }

        kafkaTemplate.send(statusTopic,
                new StatusChangeEvent(request.getId(), old, newStatus, LocalDateTime.now())
        );

        return request;
    }
}
