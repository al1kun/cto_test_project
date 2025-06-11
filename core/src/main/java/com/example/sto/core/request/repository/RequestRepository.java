package com.example.sto.core.request.repository;

import com.example.sto.core.request.model.enums.RequestStatus;
import com.example.sto.core.request.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByClientId(Long clientId);
    List<Request> findByStatus(RequestStatus status);
}