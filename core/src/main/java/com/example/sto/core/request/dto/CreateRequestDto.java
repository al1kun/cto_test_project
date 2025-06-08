package com.example.sto.core.request.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRequestDto {
    @NotNull
    private Long clientId;

    @NotNull
    @Size(min = 1, max = 500)
    private String description;
}
