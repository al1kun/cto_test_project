package com.example.sto.core.request.controller;

import com.example.sto.common.domain.RequestStatus;
import com.example.sto.core.request.domain.Request;
import com.example.sto.core.request.dto.ChangeStatusDto;
import com.example.sto.core.request.dto.CreateRequestDto;
import com.example.sto.core.request.dto.RequestDto;
import com.example.sto.core.request.service.RequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping("/create")
    public ResponseEntity<RequestDto> createRequest(
            @Valid @RequestBody CreateRequestDto createDto
    ) {
        Request request = requestService.createRequest(
                createDto.getClientId(),
                createDto.getDescription()
        );
        RequestDto requestDto = RequestDto.fromEntity(request);
        return ResponseEntity
                .created(URI.create("/api/requests/" + requestDto.getId()))
                .body(requestDto);
    }

    @GetMapping("/by-client")
    public List<RequestDto> getByClientId(
            @RequestParam("clientId") Long clientId
    ) {
        return requestService.getByClientId(clientId).stream()
                .map(RequestDto::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/by-status")
    public List<RequestDto> getByStatus(
            @RequestParam("status") RequestStatus status
    ) {
        return requestService.getByStatus(status).stream()
                .map(RequestDto::fromEntity)
                .collect(Collectors.toList());
    }

    @PutMapping("/{id}/status")
    public RequestDto changeStatus(
            @PathVariable("id") Long requestId,
            @Valid @RequestBody ChangeStatusDto changeStatusDto
    ) {
        Request updated = requestService.changeStatus(
                requestId,
                changeStatusDto.getNewStatus(),
                changeStatusDto.getChangedBy(),
                changeStatusDto.getReason()
        );
        return RequestDto.fromEntity(updated);
    }
}