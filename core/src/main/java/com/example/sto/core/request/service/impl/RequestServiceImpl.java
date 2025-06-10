package com.example.sto.core.request.service.impl;

import com.example.sto.core.request.exception.InvalidRequestStatusException;
import com.example.sto.core.request.exception.ResourceNotFoundException;
import com.example.sto.core.request.model.enums.RequestStatus;
import com.example.sto.core.history.model.RequestStatusHistory;
import com.example.sto.core.history.repository.RequestStatusHistoryRepository;
import com.example.sto.core.kafka.StatusChangeEvent;
import com.example.sto.core.request.model.Request;
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
        List<Request> requests = requestRepository.findByClientId(clientId);
        if (requests.isEmpty()) {
            throw new ResourceNotFoundException("No request found for clientId: " + clientId);
        }
        return requests;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Request> getByStatus(RequestStatus status) {
        List<Request> requests = requestRepository.findByStatus(status);
        if (requests.isEmpty()) {
            throw new ResourceNotFoundException("No request found for status: " + status);
        }
        return requests;
    }

    @Override
    @Transactional
    public Request changeStatus(Long requestId,
                                RequestStatus newStatus,
                                String changedBy,
                                String reason) {

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + requestId));

        RequestStatus old = request.getStatus();
        if (old == newStatus) {
            throw new InvalidRequestStatusException("Cannot change status: already in " + newStatus);
        }

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
