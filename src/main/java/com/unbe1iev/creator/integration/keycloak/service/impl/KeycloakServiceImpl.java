package com.unbe1iev.creator.integration.keycloak.service.impl;

import com.unbe1iev.common.exception.DefaultRuntimeException;
import com.unbe1iev.creator.configuration.KeycloakConfiguration;
import com.unbe1iev.creator.integration.keycloak.dto.KeycloakErrorDto;
import com.unbe1iev.creator.integration.keycloak.exception.UserNotFoundException;
import com.unbe1iev.creator.integration.keycloak.mapper.RepresentationMapper;
import com.unbe1iev.creator.integration.keycloak.service.KeycloakService;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.ClientBuilderWrapper;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.JacksonProvider;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.keycloak.OAuth2Constants.PASSWORD;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakServiceImpl implements KeycloakService {

    public static final String ROLE_CREATOR = "creator";
    public static final String ROLE_ADMIN = "admin";
    public static final String REALM_MASTER = "master";
    private static final String PRODUCT_ROLE_PREFIX = "prd_";
    private static final String CHANGE_PASSWORD_ATTRIBUTE = "CHANGE_PASSWORD";
    private static final String ADMIN_CLI_CLIENT = "admin-cli";

    private final KeycloakConfiguration keycloakConfiguration;
    private final RepresentationMapper representationMapper;

    private RealmResource getRealm() {
        return getKeycloakInstance(keycloakConfiguration).realm(keycloakConfiguration.getRealm());
    }

    private Keycloak getKeycloakInstance(KeycloakConfiguration keycloakConfiguration) {
        Client client = ClientBuilderWrapper
                .create(null, false)
                .readTimeout(120000, TimeUnit.MILLISECONDS)
                .connectTimeout(60000, TimeUnit.MILLISECONDS)
                .register(JacksonProvider.class, 100)
                .build();

        return KeycloakBuilder.builder()
                .clientId("admin-cli")
                .grantType(PASSWORD)
                .password(keycloakConfiguration.getPassword())
                .realm(REALM_MASTER)
                .serverUrl(keycloakConfiguration.getUrl())
                .username(keycloakConfiguration.getUser())
                .resteasyClient(client)
                .build();
    }

    private void updateKeycloakUserPassword(String keycloakUserId, Supplier<List<CredentialRepresentation>> credentialRepresentationSupplier) {
        RealmResource realm = getRealm();
        UserRepresentation user = realm.users().get(keycloakUserId).toRepresentation();
        user.setCredentials(credentialRepresentationSupplier.get());
        realm.users().get(keycloakUserId).update(user);
    }

    @Override
    public String getKeycloakUserId(String email) {
        return searchUser(email).map(UserRepresentation::getId).orElse(null);
    }

    private Optional<UserRepresentation> searchUser(String email) {
        RealmResource realm = getRealm();
        return realm.users()
                .search(email, 0, 1)
                .stream()
                .findFirst();
    }

    @Override
    public String createUser(String username, String email, String password) {
        return createUser(username, email, password, List.of(ROLE_CREATOR), false);
    }

    @Override
    public String createAdminUser(String username, String email, String password) {
        return createUser(username, email, password, Collections.singletonList(ROLE_ADMIN), true);
    }

    @Override
    public boolean isUsernameTaken(String username) {
        RealmResource realm = getRealm();
        List<UserRepresentation> users = realm.users().search(username, true);
        return !users.isEmpty();
    }

    private String createUser(String username, String email, String password, List<String> roles, boolean forceChangePassword) {
        Optional<UserRepresentation> userOpt = searchUser(email);
        UserRepresentation user = userOpt.orElseGet(() -> createUserInternal(username, email, password, roles, forceChangePassword));

        if (userOpt.isPresent()) {
            String userId = user.getId();
            RealmResource realm = getRealm();
            roles.forEach(role -> addRoleToUser(userId, realm, role));
        }

        return user.getId();
    }

    @NotNull
    private UserRepresentation createUserInternal(String username, String email, String password, List<String> roles, boolean forceChangePassword) {
        RealmResource realm = getRealm();
        UserRepresentation userRepresentation = representationMapper.getUserRepresentation(username, email, password, false, forceChangePassword);

        try (Response response = realm.users().create(userRepresentation)) {
            throwIfErrorStatus(response);
            String keycloakUserId = CreatedResponseUtil.getCreatedId(response);
            roles.forEach(role -> addRoleToUser(keycloakUserId, realm, role));

            return realm.users()
                    .get(keycloakUserId)
                    .toRepresentation();
        }
    }

    @Override
    public void addRole(String keycloakUserId, String roleName) {
        RealmResource realm = getRealm();
        addRoleToUser(keycloakUserId, realm, roleName);
    }

    private void addRoleToUser(String keycloakUserId, RealmResource realm, String roleName) {
        RoleRepresentation roleToAdd = realm.roles()
                .get(roleName)
                .toRepresentation();

        UserResource userResource = realm.users().get(keycloakUserId);
        addRoleToUser(userResource, roleToAdd);
    }

    private void addRoleToUser(UserResource userResource, RoleRepresentation roleToAdd) {
        if (!roleOnUser(roleToAdd, userResource)) {
            userResource
                    .roles()
                    .realmLevel()
                    .add(Collections.singletonList(roleToAdd));
        }
    }

    private boolean roleOnUser(RoleRepresentation roleToAdd, UserResource userResource) {
        List<RoleRepresentation> existingUserRoles = userResource
                .roles()
                .realmLevel().listAll();
        return existingUserRoles.stream().anyMatch(roleRepresentation -> roleRepresentation.getName().equals(roleToAdd.getName()));
    }

    @Override
    public void setupUserProductRoles(String keycloakUserId, Set<String> userRoleNamesToSetup) {
        RealmResource realm = getRealm();

        Map<String, RoleRepresentation> userRolesToSetup = filterOnlyExistingInKeycloak(realm, userRoleNamesToSetup);
        UserResource userResource = realm.users().get(keycloakUserId);

        List<RoleRepresentation> existingUserRoles = userResource
                .roles()
                .realmLevel().listAll();

        List<RoleRepresentation> rolesToRemove = new ArrayList<>();
        existingUserRoles.forEach(roleRepresentation -> {
            if (roleRepresentation.getName().startsWith(PRODUCT_ROLE_PREFIX)) {
                String roleName = roleRepresentation.getName().toLowerCase();
                if (userRolesToSetup.containsKey(roleName)) {
                    log.info("user already has role {}", roleName);
                    userRolesToSetup.remove(roleName);
                } else {
                    log.info("role to remove {}", roleName);
                    rolesToRemove.add(roleRepresentation);
                }
            }
        });

        removeRolesFromUser(userResource, rolesToRemove);
        addRolesToUser(userResource, userRolesToSetup);
    }

    private Map<String, RoleRepresentation> filterOnlyExistingInKeycloak(RealmResource realm, Set<String> userRoleNames) {
        return realm.roles()
                .list()
                .stream()
                .filter(roleRepresentation -> userRoleNames.contains(roleRepresentation.getName().toLowerCase()))
                .collect(Collectors.toMap(roleRepresentation -> roleRepresentation.getName().toLowerCase(), Function.identity()));
    }

    private void removeRolesFromUser(UserResource userResource, List<RoleRepresentation> rolesToRemove) {
        if (!rolesToRemove.isEmpty()) {
            log.info("roles to remove: {}", rolesToRemove);
            userResource.roles().realmLevel().remove(rolesToRemove);
        }
    }

    private void addRolesToUser(UserResource userResource, Map<String, RoleRepresentation> userRolesToSetup) {
        if (!userRolesToSetup.isEmpty()) {
            log.info("roles to add: {}", userRolesToSetup.keySet());
            userResource.roles().realmLevel().add(new ArrayList<>(userRolesToSetup.values()));
        }
    }

    @Override
    public void removeRole(String keycloakUserId, String roleName) {
        RealmResource realm = getRealm();
        RoleRepresentation role = realm.roles()
                .get(roleName)
                .toRepresentation();

        UserResource userResource = realm.users().get(keycloakUserId);

        if (roleOnUser(role, userResource)) {
            userResource
                    .roles()
                    .realmLevel()
                    .remove(Collections.singletonList(role));
        }
    }

    @Override
    public String changeEmail(String keycloakUserId, String email) {
        String lowerCaseEmail = email.toLowerCase();
        RealmResource realm = getRealm();

        UserResource userResource = realm.users().get(keycloakUserId);
        UserRepresentation userRepresentation = userResource.toRepresentation();
        userRepresentation.setEmail(lowerCaseEmail);

        userResource.update(userRepresentation);

        return keycloakUserId;
    }

    @Override
    public boolean verifyPassword(String email, String password) {
        try (Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(keycloakConfiguration.getUrl())
                .realm(keycloakConfiguration.getRealm())
                .clientId(ADMIN_CLI_CLIENT)
                .username(email)
                .password(password)
                .build()) {

            AccessTokenResponse tokenResponse = keycloak.tokenManager().grantToken();
            return tokenResponse != null && tokenResponse.getToken() != null;
        } catch (Exception e) {
            log.error("Password verification failed for user: {}", email, e);
            return false;
        }
    }

    private String getUserKeycloakIdByEmail(String email) {
        UserRepresentation user = searchUser(email).orElseThrow(() -> {
            log.error("User not found after email changed for: {}", email);
            return new UserNotFoundException(email);
        });
        return user.getId();
    }

    private void copyRoles(RealmResource realm, List<RoleRepresentation> existingUserRoles, String newKeycloakUserId) {
        UserResource newUserResource = realm.users().get(newKeycloakUserId);
        existingUserRoles.forEach(role -> addRoleToUser(newUserResource, role));
    }

    private void tryToDeleteOldUser(String keycloakUserId) {
        try {
            deleteUser(keycloakUserId);
        } catch (Exception e) {
            log.error("try to delete old user: {}", e.getMessage() != null ? e.getMessage() : e.toString());
        }
    }

    @Override
    public void changePassword(String keycloakUserId, String oldPassword, String newPassword) {
        updateKeycloakUserPassword(keycloakUserId,
                () -> representationMapper.getCredentialsForChangeOperation(newPassword, oldPassword));
    }

    @Override
    public void clearResetPasswordRequiredFlag(String keycloakUserId) {
        RealmResource realm = getRealm();
        UserRepresentation user = realm.users().get(keycloakUserId).toRepresentation();

        if (user.firstAttribute(CHANGE_PASSWORD_ATTRIBUTE) != null) {
            user.getAttributes().remove(CHANGE_PASSWORD_ATTRIBUTE);
            realm.users().get(keycloakUserId).update(user);
        }
    }

    @Override
    public void deleteUser(String keycloakUserId) {
        log.info("deleted user: {}", keycloakUserId);
        RealmResource realm = getRealm();
        setupUserProductRoles(keycloakUserId, Collections.emptySet());
        removeMainRoleFromUser(keycloakUserId, realm);
        realm.users().delete(keycloakUserId);
    }

    private void removeMainRoleFromUser(String keycloakUserId, RealmResource realm) {
        RoleRepresentation mainRole = realm.roles()
                .get(ROLE_CREATOR)
                .toRepresentation();

        realm.users().get(keycloakUserId)
                .roles()
                .realmLevel()
                .remove(Collections.singletonList(mainRole));
    }

    private void throwIfErrorStatus(Response response) {
        log.info("response status: {}", response.getStatus());

        if (response.getStatus() >= 400) {
            String message;

            try {
                KeycloakErrorDto error = response.readEntity(KeycloakErrorDto.class);
                message = error.getMessage();
            } catch (ProcessingException e) {
                message = response.readEntity(String.class);
            }
            log.info("created user: {}", message);

            final String errorMessage = message;

            throw new DefaultRuntimeException(new Exception(errorMessage)) {
                @Override
                public String getDefaultMessage() {
                    return errorMessage;
                }
            };
        }
    }
}
