package com.unbe1iev.creator.integration.keycloak.mapper.impl;

import com.unbe1iev.creator.integration.keycloak.SecretDataBuilder;
import com.unbe1iev.creator.integration.keycloak.mapper.RepresentationMapper;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class RepresentationMapperImpl implements RepresentationMapper {

    private static final String OPERATION_CREATE = "{ \"operation\": \"CREATE\" }";
    private static final String OPERATION_CHANGE = "{ \"operation\": \"CHANGE\" }";
    private static final String OPERATION_CHANGE_EMAIL = "{ \"operation\": \"CHANGE_EMAIL\" }";

    private static final String CREDENTIAL_TYPE = "sso-password";

    @Override
    public UserRepresentation getUserRepresentation(String email, String password, boolean confirmByEmail, boolean forceChangePassword) {
        UserRepresentation userRepresentation = new UserRepresentation();

        userRepresentation.setUsername(email);
        userRepresentation.setEmail(email);
        userRepresentation.setEnabled(Boolean.TRUE);
        userRepresentation.setEmailVerified(Boolean.TRUE);
        userRepresentation.setCredentials(getCredentialsForCreateOperation(password, confirmByEmail, forceChangePassword));

        return userRepresentation;
    }

    @Override
    public List<CredentialRepresentation> getCredentialsForChangeOperation(String newPassword, String oldPassword) {
        return prepareCredentials(
                "{ \"password\": \"" + newPassword + "\", \"oldPassword\": \"" + oldPassword + "\" }",
                OPERATION_CHANGE);
    }

    @Override
    public List<CredentialRepresentation> getCredentialsForChangeEmailOperation(String newEmail) {
        return prepareCredentials(
                "{ \"newEmail\": \"" + newEmail + "\" }",
                OPERATION_CHANGE_EMAIL);
    }

    private List<CredentialRepresentation> getCredentialsForCreateOperation(String password, boolean confirmByEmail, boolean forceChangePassword) {
        String secretData = new SecretDataBuilder()
                .add("password", password)
                .add("confirm", "" + confirmByEmail)
                .add("forceChangePassword", "" + forceChangePassword)
                .build();

        return prepareCredentials(secretData, OPERATION_CREATE);
    }

    private List<CredentialRepresentation> prepareCredentials(String secretData, String operation) {
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();

        credentialRepresentation.setCredentialData(operation);
        credentialRepresentation.setSecretData(secretData);
        credentialRepresentation.setType(CREDENTIAL_TYPE);

        return Collections.singletonList(credentialRepresentation);
    }
}
