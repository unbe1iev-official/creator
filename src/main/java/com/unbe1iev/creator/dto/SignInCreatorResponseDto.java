package com.unbe1iev.creator.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class SignInCreatorResponseDto {

    @NotNull
    private String passwordToken;
}
