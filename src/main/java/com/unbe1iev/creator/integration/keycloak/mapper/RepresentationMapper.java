package com.unbe1iev.creator.integration.keycloak.mapper;

import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

public interface RepresentationMapper {

    UserRepresentation getUserRepresentation(String email, String password, boolean confirmByEmail, boolean forceChangePassword);

    List<CredentialRepresentation> getCredentialsForChangeOperation(String newPassword, String oldPassword);

    List<CredentialRepresentation> getCredentialsForChangeEmailOperation(String newEmail);
}
