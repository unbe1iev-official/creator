package com.unbe1iev.creator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@Schema(description = "Data transfer object for Creator creation.")
public class SignInCreatorRequestDto {

    @Schema(description = "Creator's generated keycloakId")
    private String keycloakId;

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

    @Schema(description = "Creator's birth date")
    private String dateOfBirth;

    @ToString.Exclude
    private String password;
}
