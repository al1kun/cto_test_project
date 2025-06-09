package com.example.sto.core.kafka;

import com.example.sto.common.domain.RequestStatus;
import com.example.sto.core.history.model.RequestStatusHistory;
import com.example.sto.core.history.repository.RequestStatusHistoryRepository;
import com.example.sto.core.request.repository.RequestRepository;
import com.example.sto.notification.Notifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatusChangeListener {

    private final RequestRepository requestRepository;
    private final RequestStatusHistoryRepository historyRepository;
    private final Notifier notifier;

    @KafkaListener(topics = "${sto.kafka.topic.request-status}", groupId = "${spring.kafka.consumer.group-id}")
    public void onStatusChange(StatusChangeEvent event) {
        requestRepository.findById(event.getRequestId()).ifPresent(req -> {
            RequestStatus old = event.getOldStatus();
            RequestStatus newer = event.getNewStatus();

            historyRepository.save(RequestStatusHistory.builder()
                    .requestId(req.getId())
                    .fromStatus(old)
                    .toStatus(newer)
                    .changedBy("kafka-consumer")
                    .reason("Event replay or post-processing")
                    .build());

            if (newer == RequestStatus.DONE) {
                notifier.notifyClient(req.getClientId(),
                        "Your request No." + req.getId() + " has been completed.");
            }
        });
    }
}
