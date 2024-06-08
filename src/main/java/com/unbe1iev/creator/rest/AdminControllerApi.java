package com.unbe1iev.creator.rest;

import com.unbe1iev.common.exception.ErrorDto;
import com.unbe1iev.creator.dto.CreatorDetailsDto;
import com.unbe1iev.creator.dto.PenaltyCreatorRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/admins")
@Tag(name = "admin", description = "The admin API")
public interface AdminControllerApi {

    @Operation(summary = "Ban or unban creator",
            security = {@SecurityRequirement(name = "bearer-token")},
            method = "handlePenalty",
            description = "This operation bans or unbans a creator based on the penalty switch.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class)))},
            tags = {"admin"})
    @PostMapping(value = "/penalty",
            produces = {"application/json;charset=utf-8"},
            consumes = {"application/json;charset=utf-8"})
    ResponseEntity<Void> handlePenalty(
            @Parameter(description = "Creator penalty request", required = true) @RequestBody PenaltyCreatorRequestDto penaltyCreatorRequestDto);

    @Operation(summary = "It retrieves all creators",
            security = {@SecurityRequirement(name = "bearer-token")},
            method = "getAllCreators",
            description = "This operation retrieves all creators.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreatorDetailsDto.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class)))},
            tags = {"admin"})
    @GetMapping(value = "/creators",
            produces = {"application/json;charset=utf-8"})
    ResponseEntity<List<CreatorDetailsDto>> getAllCreators();

    @Operation(summary = "Delete a creator by ID",
            security = {@SecurityRequirement(name = "bearer-token")},
            method = "deleteCreator",
            description = "This operation deletes a creator by their ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successful operation, no content"),
                    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDto.class)))},
            tags = {"admin"})
    @DeleteMapping(value = "/creators/{creatorId}",
            produces = {"application/json;charset=utf-8"})
    ResponseEntity<Void> deleteCreator(
            @Parameter(description = "ID of the creator to be deleted", required = true) @PathVariable("creatorId") Long creatorId);
}
