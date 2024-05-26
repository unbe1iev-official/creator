package com.unbe1iev.creator.repository;

import com.unbe1iev.creator.entity.Creator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreatorRepository extends JpaRepository<Creator, Long> {

    Optional<Creator> findByEmailAndDomain(String email, String domain);
}
