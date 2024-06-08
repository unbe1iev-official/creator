package com.unbe1iev.creator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@Schema(description = "Data transfer object for Creator Details")
public class CreatorDetailsDto {
    @NotNull
    @Schema(description = "Creator's identifier")
    private Long id;

    @NotNull
    @Schema(description = "Creator's username")
    private String username;

    @Schema(description = "Creator's first name")
    private String firstName;

    @Schema(description = "Creator's first name")
    private String lastName;

    @Schema(description = "Creator's first name")
    private String phoneNumber;

    @NotNull
    @Schema(description = "Creator's email")
    private String email;

    @Schema(description = "Creator's additional email")
    private String contactEmail;

    @NotNull
    @Schema(description = "Creator's birth date")
    private LocalDate dateOfBirth;
}
