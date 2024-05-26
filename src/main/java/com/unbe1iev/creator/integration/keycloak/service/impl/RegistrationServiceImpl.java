package com.unbe1iev.creator.integration.keycloak.service.impl;

import com.unbe1iev.creator.dto.SignInCreatorRequestDto;
import com.unbe1iev.creator.dto.SignInCreatorResponseDto;
import com.unbe1iev.creator.entity.Creator;
import com.unbe1iev.creator.service.CreatorService;
import com.unbe1iev.creator.integration.keycloak.service.RegistrationService;
import com.unbe1iev.creator.security.PasswordTokenData;
import com.unbe1iev.creator.security.PasswordTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final CreatorService creatorService;
    private final PasswordTokenProvider passwordTokenProvider;

    @Override
    public SignInCreatorResponseDto register(SignInCreatorRequestDto signInCreatorRequestDto) {
        log.info("register an email: {}", signInCreatorRequestDto.getEmail());

        signInCreatorRequestDto.setEmail(signInCreatorRequestDto.getEmail().toLowerCase());
        Creator creator = creatorService.createCreator(signInCreatorRequestDto);
        String token = passwordTokenProvider.create(new PasswordTokenData(signInCreatorRequestDto.getEmail().toLowerCase()));
        creator.setPasswordToken(token);

        return SignInCreatorResponseDto.builder()
                .passwordToken(token)
                .build();
    }
}
