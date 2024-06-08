package com.unbe1iev.creator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Schema(description = "Data transfer object for Creator blockade")
public class PenaltyCreatorRequestDto {
    @NotNull
    private Long creatorId;
    @NotNull
    private Boolean banned;
}
