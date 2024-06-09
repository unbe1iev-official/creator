package com.unbe1iev.creator.service;

import com.unbe1iev.creator.dto.ChangeEmailRequestDto;
import com.unbe1iev.creator.dto.ChangePasswordDto;
import com.unbe1iev.creator.dto.CreatorDetailsDto;
import com.unbe1iev.creator.dto.SignInCreatorRequestDto;
import com.unbe1iev.creator.entity.Creator;
import jakarta.validation.constraints.NotNull;

public interface CreatorService {

    Creator createCreator(SignInCreatorRequestDto signInCreatorRequestDto);

    Boolean isEmailTaken(@NotNull String email);

    Boolean isUsernameTaken(@NotNull String username);

    void changePassword(ChangePasswordDto changePasswordDto);

    CreatorDetailsDto changeEmail(ChangeEmailRequestDto changeEmailRequestDto);

    Creator getCreatorByKeycloakId();

    boolean verifyPassword(String email, String oldPassword);
}
