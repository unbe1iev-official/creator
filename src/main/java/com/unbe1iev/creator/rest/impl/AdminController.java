package com.unbe1iev.creator.rest.impl;

import com.unbe1iev.creator.dto.CreatorDetailsDto;
import com.unbe1iev.creator.dto.PenaltyCreatorRequestDto;
import com.unbe1iev.creator.rest.AdminControllerApi;
import com.unbe1iev.creator.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('admin')")
public class AdminController implements AdminControllerApi {

    private final AdminService adminService;

    @Override
    public ResponseEntity<Void> handlePenalty(PenaltyCreatorRequestDto penaltyCreatorRequestDto) {
        log.info("Handling penalty for creator: {}", penaltyCreatorRequestDto.getCreatorId());
        log.info("User roles: {}", SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        if (Boolean.TRUE.equals(penaltyCreatorRequestDto.getBanned())) {
            adminService.banCreator(penaltyCreatorRequestDto.getCreatorId());
        } else {
            adminService.unbanCreator(penaltyCreatorRequestDto.getCreatorId());
        }
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<CreatorDetailsDto>> getAllCreators() {
        return ResponseEntity.ok(adminService.getAllCreators());
    }

    @Override
    public ResponseEntity<Void> deleteCreator(Long creatorId) {
        adminService.deleteCreator(creatorId);
        return ResponseEntity.noContent().build();
    }
}
