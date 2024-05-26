package com.unbe1iev.creator.mapper;

import com.unbe1iev.common.mapper.Mapper;
import com.unbe1iev.creator.dto.CreatorDetailsDto;
import com.unbe1iev.creator.entity.Creator;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class CreatorToCreatorDetailsMapper implements Mapper<Creator, CreatorDetailsDto> {

    @Override
    public @NotNull CreatorDetailsDto map(Creator inputObject) {
        return CreatorDetailsDto.builder()
                .id(inputObject.getId())
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
