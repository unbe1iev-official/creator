package com.unbe1iev.creator.service;

import com.unbe1iev.creator.dto.SignInCreatorRequestDto;

public interface RegistrationService {
    void register(SignInCreatorRequestDto signInCreatorRequestDto);
}