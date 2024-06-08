package com.unbe1iev.creator.validator.impl;

import com.unbe1iev.common.exception.MessageExtractor;
import com.unbe1iev.common.validator.CreateValidator;
import com.unbe1iev.creator.dto.ChangePasswordDto;
import com.unbe1iev.creator.service.CreatorService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CreatorChangePasswordValidator implements CreateValidator<ChangePasswordDto> {

    private final MessageExtractor messageExtractor;
    private final CreatorService creatorService;

    private class ValidatorContext extends CreatorValidatorSteps {
        private ValidatorContext() {
            super(messageExtractor, creatorService);
        }
    }

    @Override
    public void validate(ChangePasswordDto toValidate) {
        new CreatorChangePasswordValidator.ValidatorContext()
                .passwordValid(toValidate);
    }
}
