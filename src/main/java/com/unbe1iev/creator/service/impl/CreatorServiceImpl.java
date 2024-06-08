package com.unbe1iev.creator.service.impl;

import com.unbe1iev.common.mapper.Mapper;
import com.unbe1iev.common.util.SecurityUtil;
import com.unbe1iev.creator.dto.ChangeEmailRequestDto;
import com.unbe1iev.creator.dto.ChangePasswordDto;
import com.unbe1iev.creator.dto.CreatorDetailsDto;
import com.unbe1iev.creator.dto.SignInCreatorRequestDto;
import com.unbe1iev.creator.entity.Creator;
import com.unbe1iev.creator.exception.ChangePasswordException;
import com.unbe1iev.creator.integration.keycloak.service.KeycloakService;
import com.unbe1iev.creator.repository.CreatorRepository;
import com.unbe1iev.creator.service.CreatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CreatorServiceImpl implements CreatorService {

    private final CreatorRepository creatorRepository;
    private final KeycloakService keycloakService;

    private final Mapper<SignInCreatorRequestDto, Creator> signInCreatorRequestDtoToCreatorMapper;
    private final Mapper<Creator, CreatorDetailsDto> creatorToCreatorDetailsDtoMapper;

    @Override
    public Creator createCreator(SignInCreatorRequestDto signInCreatorRequestDto) {
        return creatorRepository.save(signInCreatorRequestDtoToCreatorMapper.map(signInCreatorRequestDto));
    }

    @Override
    public Boolean isEmailTaken(String email) {
        log.info("Checking availability of email: {}", email);
        return creatorRepository.findByEmail(email).isPresent() || keycloakService.getKeycloakUserId(email) != null;
    }

    @Override
    public Boolean isUsernameTaken(String username) {
        log.info("Checking availability of username: {}", username);
        return creatorRepository.findByUsername(username).isPresent();
    }

    @Override
    public void changePassword(ChangePasswordDto changePasswordDto) {
        Creator creator = getCreatorByKeycloakId();

        try {
            keycloakService.changePassword(creator.getKeycloakId(), changePasswordDto.getOldPassword(), changePasswordDto.getNewPassword());
        } catch (Exception e) {
            throw new ChangePasswordException(e);
        }
        keycloakService.clearResetPasswordRequiredFlag(creator.getKeycloakId());
    }

    @Override
    public CreatorDetailsDto changeEmail(ChangeEmailRequestDto changeEmailRequestDto) {
        Creator creator = getCreatorByKeycloakId();

        String newKeycloakId = keycloakService.changeEmail(creator.getKeycloakId(), changeEmailRequestDto.getEmail());
        log.info("New keycloakId generated for the user. keycloakId = {}", newKeycloakId);

        String newEmail = changeEmailRequestDto.getEmail().toLowerCase();

        creator.setKeycloakId(newKeycloakId);
        creator.setEmail(newEmail);

        return creatorToCreatorDetailsDtoMapper.map(
                creatorRepository.save(creator));
    }

    @Override
    public Creator getCreatorByKeycloakId() {
        String userKeycloakId = SecurityUtil.getLoggedUserKeycloakId();
        return creatorRepository.findByKeycloakId(userKeycloakId)
                .orElseThrow(() -> new NoSuchElementException(userKeycloakId));
    }
}
