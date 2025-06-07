package com.example.sto.core.request.repository;

import com.example.sto.common.domain.RequestStatus;
import com.example.sto.core.request.domain.Request;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByClientId(Long clientId);
    List<Request> findByStatus(RequestStatus status);
}