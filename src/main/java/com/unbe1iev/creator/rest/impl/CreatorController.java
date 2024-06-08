package com.unbe1iev.creator.rest.impl;

import com.unbe1iev.common.exception.ApplicationValidationException;
import com.unbe1iev.common.validator.CreateValidator;
import com.unbe1iev.common.validator.Validator;
import com.unbe1iev.creator.dto.ChangeEmailRequestDto;
import com.unbe1iev.creator.dto.ChangePasswordDto;
import com.unbe1iev.creator.dto.CreatorDetailsDto;
import com.unbe1iev.creator.dto.SignInCreatorRequestDto;
import com.unbe1iev.creator.dto.StatusResponseDto;
import com.unbe1iev.creator.service.RegistrationService;
import com.unbe1iev.creator.rest.CreatorControllerApi;
import com.unbe1iev.creator.service.CreatorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CreatorController implements CreatorControllerApi {

    private final RegistrationService registrationService;
    private final CreatorService creatorService;
    private final CreateValidator<SignInCreatorRequestDto> creatorCreateValidator;
    private final Validator<ChangePasswordDto> creatorChangePasswordValidator;

    private static final StatusResponseDto SUCCESS_RESPONSE = StatusResponseDto.builder()
            .success(true)
            .build();

    @Override
    public ResponseEntity<Void> signInNewCreator(@Valid @RequestBody SignInCreatorRequestDto signInCreatorRequestDto, BindingResult result) {
        if (result.hasErrors()) {
            throw new ApplicationValidationException(result.getAllErrors());
        }
        creatorCreateValidator.validate(signInCreatorRequestDto);
        registrationService.register(signInCreatorRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    @PreAuthorize("hasRole('creator')")
    public StatusResponseDto changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto) {
        creatorChangePasswordValidator.validate(changePasswordDto);
        creatorService.changePassword(changePasswordDto);
        return SUCCESS_RESPONSE;
    }

    @Override
    public ResponseEntity<Boolean> checkEmail(@Valid @RequestBody String email) {
        return ResponseEntity.ok(creatorService.isEmailTaken(email));
    }

    @Override
    @PreAuthorize("hasRole('creator')")
    public ResponseEntity<CreatorDetailsDto> changeEmail(@Valid @RequestBody ChangeEmailRequestDto changeEmailRequestDto, BindingResult result) {
        return new ResponseEntity<>(creatorService.changeEmail(changeEmailRequestDto), HttpStatus.OK);
    }
}
