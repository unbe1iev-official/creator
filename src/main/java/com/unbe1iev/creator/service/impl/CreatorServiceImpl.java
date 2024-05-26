package com.unbe1iev.creator.service.impl;

import com.unbe1iev.common.configuration.DomainHolder;
import com.unbe1iev.common.mapper.Mapper;
import com.unbe1iev.creator.dto.SignInCreatorRequestDto;
import com.unbe1iev.creator.entity.Creator;
import com.unbe1iev.creator.service.CreatorService;
import com.unbe1iev.creator.repository.CreatorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CreatorServiceImpl implements CreatorService {

    private final CreatorRepository creatorRepository;
    private final Mapper<SignInCreatorRequestDto, Creator> signInCreatorRequestDtoToCreatorMapper;
    private final DomainHolder domainHolder;

    @Override
    public Boolean verifyEmail(String email, String domain) {
        log.info("verify user email: {} ", email);

        Creator creatorByEmail = creatorRepository.findByEmailAndDomain(email, domainHolder.getDomain())
                .orElse(null);

        return creatorByEmail != null;
    }

    @Override
    public Creator createCreator(SignInCreatorRequestDto signInCreatorRequestDto) {
        return creatorRepository.save(signInCreatorRequestDtoToCreatorMapper.map(signInCreatorRequestDto));
    }
}
