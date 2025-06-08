package com.example.sto.core.kafka;

import com.example.sto.common.domain.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusChangeEvent {
    private Long requestId;
    private RequestStatus oldStatus;
    private RequestStatus newStatus;
    private LocalDateTime changedAt;
}
