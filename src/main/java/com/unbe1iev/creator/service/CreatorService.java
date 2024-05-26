package com.unbe1iev.creator.service;

import com.unbe1iev.creator.dto.SignInCreatorRequestDto;
import com.unbe1iev.creator.entity.Creator;
import jakarta.validation.constraints.NotNull;

public interface CreatorService {

    Boolean verifyEmail(@NotNull String email, String domain);

    Creator createCreator(SignInCreatorRequestDto signInCreatorRequestDto);
}
