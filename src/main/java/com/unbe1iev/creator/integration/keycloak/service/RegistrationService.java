package com.unbe1iev.creator.integration.keycloak.service;

import com.unbe1iev.creator.dto.SignInCreatorRequestDto;
import com.unbe1iev.creator.dto.SignInCreatorResponseDto;

public interface RegistrationService {

    SignInCreatorResponseDto register(SignInCreatorRequestDto signInCreatorRequestDto);
}
