package com.example.sto.core.request.dto;

import com.example.sto.common.domain.RequestStatus;
import com.example.sto.core.request.model.Request;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RequestDto {
    private Long id;
    private Long clientId;
    private String description;
    private RequestStatus status;
    private LocalDateTime createdAt;

    public static RequestDto fromEntity(Request request) {
        return new RequestDto(
                request.getId(),
                request.getClientId(),
                request.getDescription(),
                request.getStatus(),
                request.getCreatedAt()
        );
    }
}
