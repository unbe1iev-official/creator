package com.unbe1iev.creator.validator.impl;

import com.unbe1iev.common.configuration.DomainHolder;
import com.unbe1iev.common.exception.MessageExtractor;
import com.unbe1iev.common.validator.impl.CommonValidatorStepsImpl;
import com.unbe1iev.creator.dto.SignInCreatorRequestDto;
import com.unbe1iev.creator.service.CreatorService;

class CreatorValidatorSteps extends CommonValidatorStepsImpl {

    private final CreatorService creatorService;
    private final DomainHolder domainHolder;

    CreatorValidatorSteps(MessageExtractor messageExtractor,
                           CreatorService creatorService,
                           DomainHolder domainHolder) {
        super(messageExtractor);
        this.domainHolder = domainHolder;
        this.creatorService = creatorService;
    }

    CreatorValidatorSteps emailVerified(SignInCreatorRequestDto toValidate) {
        creatorService.verifyEmail(toValidate.getEmail(), domainHolder.getDomain());
        return this;
    }
}
