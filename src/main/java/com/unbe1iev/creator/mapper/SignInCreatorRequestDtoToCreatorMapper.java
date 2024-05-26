package com.unbe1iev.creator.mapper;

import com.unbe1iev.common.configuration.DomainHolder;
import com.unbe1iev.common.mapper.Mapper;
import com.unbe1iev.creator.dto.SignInCreatorRequestDto;
import com.unbe1iev.creator.entity.Creator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SignInCreatorRequestDtoToCreatorMapper implements Mapper<SignInCreatorRequestDto, Creator> {

    private final DomainHolder domainHolder;

    @Override
    public Creator map(SignInCreatorRequestDto inputObject) {
        return Creator.builder()
                .domain(domainHolder.getDomain())
                .username(inputObject.getUsername())
                .firstName(inputObject.getFirstName())
                .lastName(inputObject.getLastName())
                .phoneNumber(inputObject.getPhoneNumber())
                .email(inputObject.getEmail())
                .contactEmail(inputObject.getContactEmail())
                .dateOfBirth(inputObject.getDateOfBirth())
                .build();
    }
}
