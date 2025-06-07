package com.example.sto.core.history.repository;

import com.example.sto.core.history.domain.RequestStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestStatusHistoryRepository extends JpaRepository<RequestStatusHistory, Long> {
    List<RequestStatusHistory> findByRequestIdOrderByChangedAtAsc(Long requestId);
}
