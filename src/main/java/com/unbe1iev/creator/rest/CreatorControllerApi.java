package com.unbe1iev.creator.rest;

import com.unbe1iev.common.exception.ErrorDto;
import com.unbe1iev.creator.dto.SignInCreatorRequestDto;
import com.unbe1iev.creator.dto.SignInCreatorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/creators")
@Tag(name = "creators", description = "The creator API")
public interface CreatorControllerApi {

    @Operation(summary = "Sign in a new creator",
            method = "signInNewCreator",
            description = "This operation creates new Creator and sends on provided email a message to confirm email and proceed next sign in steps.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SignInCreatorResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "405", description = "Method Not allowed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "409", description = "Conflict", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class)))},
            tags = {"creator"})
    @PostMapping(value = "/sign-in",
            produces = {"application/json;charset=utf-8"},
            consumes = {"application/json;charset=utf-8"})
    ResponseEntity<SignInCreatorResponseDto> signInNewCreator(
            @Parameter(description = "SignId data", required = true) @Valid @RequestBody SignInCreatorRequestDto signInCreatorRequestDto,
            @Parameter(hidden = true) BindingResult result
    );
}
