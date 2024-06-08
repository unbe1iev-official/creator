package com.unbe1iev.creator.entity;

import com.unbe1iev.common.entity.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "creator")
@EntityListeners(AuditingEntityListener.class)
public class Creator extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String domain;

    @NotNull
    private String keycloakId;

    @NotNull
    private String username;

    private String firstName;
    private String lastName;
    private String phoneNumber;
    @NotNull
    private String email;
    private String contactEmail;

    private LocalDate dateOfBirth;

    @Column(length = 5000)
    private String passwordToken;

    @NotNull
    @Builder.Default
    private Boolean isDeleted = Boolean.FALSE;
}
