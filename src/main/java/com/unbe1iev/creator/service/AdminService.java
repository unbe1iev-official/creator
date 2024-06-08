package com.unbe1iev.creator.service;

import com.unbe1iev.creator.dto.CreatorDetailsDto;

import java.util.List;

public interface AdminService {

    void banCreator(Long creatorId);

    void unbanCreator(Long creatorId);

    List<CreatorDetailsDto> getAllCreators();

    void deleteCreator(Long creatorId);
}
