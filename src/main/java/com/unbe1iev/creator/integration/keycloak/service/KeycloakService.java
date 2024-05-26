package com.unbe1iev.creator.integration.keycloak.service;

import java.util.Set;

public interface KeycloakService {

    String getKeycloakUserId(String email, String domain);

    String createUser(String email, String password, String domain);
    String createAdminUser(String email, String password, String domain);

    void addRole(String keycloakUserId, String roleName, String domain);
    void setupUserProductRoles(String keycloakUserId, Set<String> userRoleNames, String domain);
    void removeRole(String keycloakUserId, String roleName, String domain);

    String changeEmail(String keycloakUserId, String email);

    void changePassword(String keycloakUserId, String oldPassword, String newPassword);
    void clearResetPasswordRequiredFlag(String keycloakUserId);

    void deleteUser(String keycloakUserId, String domain);
}
