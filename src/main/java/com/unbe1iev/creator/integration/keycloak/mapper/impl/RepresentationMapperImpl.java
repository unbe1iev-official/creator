package com.unbe1iev.creator.integration.keycloak.mapper.impl;

import com.unbe1iev.creator.integration.keycloak.mapper.RepresentationMapper;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class RepresentationMapperImpl implements RepresentationMapper {

    private static final String CREDENTIAL_TYPE = CredentialRepresentation.PASSWORD;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int HASH_ITERATIONS = 27500;

    @Override
    public UserRepresentation getUserRepresentation(String username, String email, String password, boolean confirmByEmail, boolean forceChangePassword) {
        UserRepresentation userRepresentation = new UserRepresentation();

        userRepresentation.setUsername(username);
        userRepresentation.setEmail(email);
        userRepresentation.setEnabled(Boolean.TRUE);
        userRepresentation.setEmailVerified(Boolean.TRUE);

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(forceChangePassword);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(password);

        userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));

        return userRepresentation;
    }

    @Override
    public List<CredentialRepresentation> getCredentialsForChangeOperation(String newPassword, String oldPassword) {
        return prepareCredentials(newPassword, oldPassword);
    }

    private List<CredentialRepresentation> prepareCredentials(String newPassword, String oldPassword) {
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType(CREDENTIAL_TYPE);
        credentialRepresentation.setValue(newPassword);
        if (oldPassword != null) {
            credentialRepresentation.setSecretData("{\"password\":\"" + newPassword + "\",\"oldPassword\":\"" + oldPassword + "\"}");
        } else {
            credentialRepresentation.setSecretData("{\"password\":\"" + newPassword + "\"}");
        }
        credentialRepresentation.setCredentialData("{\"algorithm\":\"" + ALGORITHM + "\",\"hashIterations\":" + HASH_ITERATIONS + "}");

        return Collections.singletonList(credentialRepresentation);
    }
}

