package com.example.sto.core.request.dto;

import com.example.sto.common.domain.RequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeStatusDto {
    @NotNull
    private RequestStatus newStatus;

    @NotNull
    private String changedBy;
    private String reason;
}
