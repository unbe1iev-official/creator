package com.unbe1iev.creator.validator.impl;

import com.unbe1iev.common.exception.ApplicationValidationException;
import com.unbe1iev.common.exception.MessageExtractor;
import com.unbe1iev.common.validator.impl.CommonValidatorStepsImpl;
import com.unbe1iev.creator.dto.ChangePasswordDto;
import com.unbe1iev.creator.dto.SignInCreatorRequestDto;
import com.unbe1iev.creator.entity.Creator;
import com.unbe1iev.creator.service.CreatorService;

class CreatorValidatorSteps extends CommonValidatorStepsImpl {

    private final CreatorService creatorService;

    CreatorValidatorSteps(MessageExtractor messageExtractor, CreatorService creatorService) {
        super(messageExtractor);
        this.creatorService = creatorService;
    }

    CreatorValidatorSteps usernameValid(SignInCreatorRequestDto toValidate) {
        if (creatorService.isUsernameTaken(toValidate.getUsername())) {
            throw new ApplicationValidationException("The provided username is already taken");
        }
        return this;
    }

    CreatorValidatorSteps emailValid(SignInCreatorRequestDto toValidate) {
        if (creatorService.isEmailTaken(toValidate.getEmail())) {
            throw new ApplicationValidationException("The provided email is already taken");
        }
        return this;
    }

    CreatorValidatorSteps passwordsValid(ChangePasswordDto toValidate) {
        Creator creator = creatorService.getCreatorByKeycloakId();
        if (!creatorService.verifyPassword(creator.getEmail(), toValidate.getOldPassword())) {
            throw new ApplicationValidationException("Old password is incorrect");
        }

        if (!toValidate.getNewPassword().equals(toValidate.getNewPassword2())) {
            throw new ApplicationValidationException("New passwords must be equal");
        }

        if (toValidate.getOldPassword().equals(toValidate.getNewPassword())) {
            throw new ApplicationValidationException("New password cannot be the same as old password");
        }
        return this;
    }
}