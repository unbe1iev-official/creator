package com.unbe1iev.creator.rest.impl;

import com.unbe1iev.common.exception.ApplicationValidationException;
import com.unbe1iev.common.validator.CreateValidator;
import com.unbe1iev.creator.dto.SignInCreatorRequestDto;
import com.unbe1iev.creator.dto.SignInCreatorResponseDto;
import com.unbe1iev.creator.integration.keycloak.service.RegistrationService;
import com.unbe1iev.creator.rest.CreatorControllerApi;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CreatorController implements CreatorControllerApi {

    private final RegistrationService registrationService;
    private final CreateValidator<SignInCreatorRequestDto> creatorCreateValidator;

    @Override
    public ResponseEntity<SignInCreatorResponseDto> signInNewCreator(@Valid  SignInCreatorRequestDto signInCreatorRequestDto, BindingResult result) {
        if (result.hasErrors()) {
            throw new ApplicationValidationException(result.getAllErrors());
        }

        creatorCreateValidator.validate(signInCreatorRequestDto);
        SignInCreatorResponseDto signInCreatorResponseDto = registrationService.register(signInCreatorRequestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(signInCreatorResponseDto);
    }
}
