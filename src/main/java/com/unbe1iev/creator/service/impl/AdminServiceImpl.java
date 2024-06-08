package com.unbe1iev.creator.service.impl;

import com.unbe1iev.common.mapper.Mapper;
import com.unbe1iev.creator.dto.CreatorDetailsDto;
import com.unbe1iev.creator.entity.Creator;
import com.unbe1iev.creator.integration.keycloak.service.KeycloakService;
import com.unbe1iev.creator.repository.CreatorRepository;
import com.unbe1iev.creator.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.unbe1iev.creator.integration.keycloak.service.impl.KeycloakServiceImpl.ROLE_CREATOR;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final CreatorRepository creatorRepository;
    private final KeycloakService keycloakService;
    private final Mapper<Creator, CreatorDetailsDto> creatorToCreatorDetailsDtoMapper;

    @Override
    public void banCreator(Long creatorId) {
        Creator creator = creatorRepository.findById(creatorId)
                .orElseThrow(() -> new NoSuchElementException(Objects.toString(creatorId)));
        keycloakService.removeRole(creator.getKeycloakId(), ROLE_CREATOR);
        log.info("Banned creator with ID: {}", creatorId);
    }

    @Override
    public void unbanCreator(Long creatorId) {
        Creator creator = creatorRepository.findById(creatorId)
                .orElseThrow(() -> new NoSuchElementException(Objects.toString(creatorId)));
        keycloakService.addRole(creator.getKeycloakId(), ROLE_CREATOR);
        log.info("Unbanned creator with ID: {}", creatorId);
    }

    @Override
    public List<CreatorDetailsDto> getAllCreators() {
        return creatorRepository.findAll()
                .stream()
                .map(creatorToCreatorDetailsDtoMapper::map)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCreator(Long creatorId) {
        Creator creator = creatorRepository.findById(creatorId)
                .orElseThrow(() -> new NoSuchElementException(Objects.toString(creatorId)));
        keycloakService.deleteUser(creator.getKeycloakId());
        creatorRepository.delete(creator);
        log.info("Deleted creator with ID: {}", creatorId);
    }
}
