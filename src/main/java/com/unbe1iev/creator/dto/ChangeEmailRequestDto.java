package com.unbe1iev.creator.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeEmailRequestDto {
    @NotNull
    private String email;
}
