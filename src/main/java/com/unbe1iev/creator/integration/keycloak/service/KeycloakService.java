package com.unbe1iev.creator.integration.keycloak.service;

import java.util.Set;

public interface KeycloakService {

    String getKeycloakUserId(String email);

    String createUser(String username, String email, String password);
    String createAdminUser(String username, String email, String password);

    void addRole(String keycloakUserId, String roleName);
    void setupUserProductRoles(String keycloakUserId, Set<String> userRoleNames);
    void removeRole(String keycloakUserId, String roleName);

    String changeEmail(String keycloakUserId, String email);

    void changePassword(String keycloakUserId, String oldPassword, String newPassword);
    void clearResetPasswordRequiredFlag(String keycloakUserId);

    void deleteUser(String keycloakUserId);
}
