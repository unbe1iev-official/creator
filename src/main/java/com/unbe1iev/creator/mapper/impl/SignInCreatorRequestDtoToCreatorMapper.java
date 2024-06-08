package com.unbe1iev.creator.mapper.impl;

import com.unbe1iev.common.configuration.DomainHolder;
import com.unbe1iev.common.mapper.Mapper;
import com.unbe1iev.common.util.SecurityUtil;
import com.unbe1iev.creator.dto.SignInCreatorRequestDto;
import com.unbe1iev.creator.entity.Creator;
import com.unbe1iev.creator.integration.keycloak.service.KeycloakService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class SignInCreatorRequestDtoToCreatorMapper implements Mapper<SignInCreatorRequestDto, Creator> {

    private final DomainHolder domainHolder;
    private final KeycloakService keycloakService;

    @Value("${callback.client.id}")
    private String callbackClientId;

    @Override
    public Creator map(SignInCreatorRequestDto inputObject) {
        boolean shouldSetKeycloakId = SecurityUtil.getClientId()
                .map(clientId -> !callbackClientId.equals(clientId))
                .orElse(true);

        return Creator.builder()
                .domain(domainHolder.getDomain())
                .keycloakId(shouldSetKeycloakId ? keycloakService.createUser(inputObject.getUsername(), inputObject.getEmail(), inputObject.getPassword()) : inputObject.getKeycloakId())
                .username(inputObject.getUsername())
                .firstName(inputObject.getFirstName())
                .lastName(inputObject.getLastName())
                .phoneNumber(inputObject.getPhoneNumber())
                .email(inputObject.getEmail())
                .contactEmail(inputObject.getContactEmail())
                .dateOfBirth(StringUtils.isEmpty(inputObject.getDateOfBirth()) ? null : LocalDate.parse(inputObject.getDateOfBirth()))
                .build();
    }
}
