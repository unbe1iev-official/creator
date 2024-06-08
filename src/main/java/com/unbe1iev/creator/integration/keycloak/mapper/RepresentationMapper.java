package com.unbe1iev.creator.integration.keycloak.mapper;

import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

public interface RepresentationMapper {

    List<CredentialRepresentation> getCredentialsForChangeOperation(String newPassword, String oldPassword);

    UserRepresentation getUserRepresentation(String username, String email, String password, boolean confirmByEmail, boolean forceChangePassword);
}
