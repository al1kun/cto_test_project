package com.example.sto.core.request.service;

import com.example.sto.common.domain.RequestStatus;
import com.example.sto.core.request.domain.Request;

import java.util.List;

public interface RequestService {
    Request createRequest(Long clientId, String description);
    List<Request> getByClientId(Long clientId);
    List<Request> getByStatus(RequestStatus status);
    Request changeStatus(Long requestId, RequestStatus newStatus, String changedBy, String reason);
}