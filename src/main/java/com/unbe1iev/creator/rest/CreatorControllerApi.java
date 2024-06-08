package com.unbe1iev.creator.rest;

import com.unbe1iev.common.exception.ErrorDto;
import com.unbe1iev.creator.dto.ChangeEmailRequestDto;
import com.unbe1iev.creator.dto.ChangePasswordDto;
import com.unbe1iev.creator.dto.CreatorDetailsDto;
import com.unbe1iev.creator.dto.SignInCreatorRequestDto;
import com.unbe1iev.creator.dto.StatusResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/creators")
@Tag(name = "creator", description = "The creator API")
public interface CreatorControllerApi {

    @Operation(summary = "Signs in a new creator",
            method = "signInNewCreator",
            description = "This operation signs in a new creator.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "405", description = "Method Not allowed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "409", description = "Conflict", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class)))},
            tags = {"creator"})
    @PostMapping(value = "/sign-in",
            produces = {"application/json;charset=utf-8"},
            consumes = {"application/json;charset=utf-8"})
    ResponseEntity<Void> signInNewCreator(
            @Parameter(description = "Sign data of a creator", required = true) @Valid @RequestBody SignInCreatorRequestDto signInCreatorRequestDto,
            @Parameter(hidden = true) BindingResult result);

    @Operation(summary = "Change creator's password",
            security = {@SecurityRequirement(name = "bearer-token")},
            method = "changePassword",
            description = "This operation allows logged in creator to change password.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StatusResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "405", description = "Method Not allowed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "409", description = "Conflict", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class)))},
            tags = {"creator"})
    @PostMapping(value = "/change-password",
            produces = {"application/json;charset=utf-8"},
            consumes = {"application/json;charset=utf-8"})
    StatusResponseDto changePassword(
            @Parameter(description = "New and old passwords required to change password", required = true) @Valid @RequestBody ChangePasswordDto changePasswordDto);

    @Operation(summary = "Change creator's email",
            security = {@SecurityRequirement(name = "bearer-token")},
            method = "changeEmail",
            description = "This operation allows logged in creator to change his email address.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreatorDetailsDto.class))),
                    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "405", description = "Method Not allowed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "409", description = "Conflict", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class)))},
            tags = {"creator"})
    @PostMapping(value = "/change-email",
            produces = {"application/json;charset=utf-8"},
            consumes = {"application/json;charset=utf-8"})
    ResponseEntity<CreatorDetailsDto> changeEmail(
            @Parameter(name = "changeEmailRequestDto", description = "Creator email change data", required = true) @Valid @RequestBody ChangeEmailRequestDto changeEmailRequestDto,
            @Parameter(hidden = true) BindingResult result);

    @Operation(summary = "Check email address availability ",
            method = "checkEmail",
            description = "This operation checks the availability of email address and tells whenever it is taken or not.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class)))},
            tags = {"creator"})
    @PostMapping(value = "/check-email",
            produces = {"application/json;charset=utf-8"},
            consumes = {"application/json;charset=utf-8"})
    ResponseEntity<Boolean> checkEmail(@Parameter(description = "Email address to check", required = true) @Valid @RequestBody String email);
}
