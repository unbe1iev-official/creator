package com.unbe1iev.creator.integration.keycloak.service.impl;

import com.unbe1iev.common.configuration.DomainHolder;
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
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    public static final String ROLE_MAIN = "unbe1iev";
    public static final String ROLE_USER = "user";
    public static final String ROLE_ADMIN = "admin";
    public static final String REALM_MASTER = "master";

    private static final String PRODUCT_ROLE_PREFIX = "prd_";

    private static final String CHANGE_PASSWORD_ATTRIBUTE = "CHANGE_PASSWORD";

    private final DomainHolder domainHolder;
    private final KeycloakConfiguration keycloakConfiguration;
    private final RepresentationMapper representationMapper;

    private RealmResource getRealm(String domain) {
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

    private void updateKeycloakUserPassword(String keycloakUserId, String domain, Supplier<List<CredentialRepresentation>> credentialRepresentationSupplier) {
        RealmResource realm = getRealm(domain);
        UserRepresentation user = realm.users().get(keycloakUserId).toRepresentation();
        user.setCredentials(credentialRepresentationSupplier.get());
        realm.users().get(keycloakUserId).update(user);
    }

    @Override
    public String getKeycloakUserId(String email, String domain) {
        RealmResource realm = getRealm(domain);

        UserRepresentation user = realm.users()
                .search(null, null, null, email, 0, 1)
                .stream()
                .findFirst()
                .orElse(null);

        if (user == null) {
            return null;
        }

        return user.getId();
    }

    private UserRepresentation searchUser(String email, String domain) {
        RealmResource realm = getRealm(domain);

        UserRepresentation user = realm.users()
                .search(email, 0, 1)
                .stream()
                .findFirst()
                .orElse(null);
        if (user != null) {
            log.info("searched user found: {}", user.getUsername());
        }
        return user;
    }

    @Override
    public String createUser(String email, String password, String domain) {
        return createUser(email, password, List.of(ROLE_MAIN), domain, false);
    }

    @Override
    public String createAdminUser(String email, String password, String domain) {
        return createUser(email, password, Collections.singletonList(ROLE_ADMIN), domain, true);
    }

    private String createUser(String email, String password, List<String> roles, String domain, boolean forceChangePassword) {
        UserRepresentation user = searchUser(email, domain);
        if (user == null) {
            user = createUserInternal(email, password, roles, domain, forceChangePassword);
            log.info("created user: {} with roles: {}, with attributes: {}", user.getId(), roles, user.getAttributes());
        } else {
            final String userId = user.getId();
            RealmResource realm = getRealm(domain);
            roles.forEach(role -> addRoleToUser(userId, realm, role));
        }

        return user.getId();
    }

    @NotNull
    private UserRepresentation createUserInternal(String email,
                                                  String password,
                                                  List<String> roles,
                                                  String domain,
                                                  boolean forceChangePassword) {
        RealmResource realm = getRealm(domain);
        UserRepresentation userRepresentation = representationMapper.getUserRepresentation(email, password, false, forceChangePassword);

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
    public void addRole(String keycloakUserId, String roleName, String domain) {
        RealmResource realm = getRealm(domain);
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
    public void setupUserProductRoles(String keycloakUserId, Set<String> userRoleNamesToSetup, String domain) {
        RealmResource realm = getRealm(domain);

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
    public void removeRole(String keycloakUserId, String roleName, String domain) {
        RealmResource realm = getRealm(domain);
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
        String domain = domainHolder.getDomain();
        String lowerCaseEmail = email.toLowerCase();
        RealmResource realm = getRealm(domain);

        List<RoleRepresentation> existingUserRoles = realm.users().get(keycloakUserId)
                .roles()
                .realmLevel().listAll();

        updateKeycloakUserPassword(keycloakUserId,
                domain, () -> representationMapper.getCredentialsForChangeEmailOperation(lowerCaseEmail));

        String newKeycloakUserId = getUserKeycloakIdByEmail(lowerCaseEmail, domain);

        copyRoles(realm, existingUserRoles, newKeycloakUserId);
        tryToDeleteOldUser(keycloakUserId, domain);

        return newKeycloakUserId;
    }

    private String getUserKeycloakIdByEmail(String email, String domain) {
        UserRepresentation user = searchUser(email, domain);
        if (user == null) {
            log.error("User not found after email changed for: {}", email);
            throw new UserNotFoundException(email);
        }
        return user.getId();
    }

    private void copyRoles(RealmResource realm, List<RoleRepresentation> existingUserRoles, String newKeycloakUserId) {
        UserResource newUserResource = realm.users().get(newKeycloakUserId);
        existingUserRoles.forEach(role -> addRoleToUser(newUserResource, role));
    }

    private void tryToDeleteOldUser(String keycloakUserId, String domain) {
        try {
            deleteUser(keycloakUserId, domain);
        } catch (Exception e) {
            log.error("try to delete old user: {}", e.getMessage() != null ? e.getMessage() : e.toString());
        }
    }

    @Override
    public void changePassword(String keycloakUserId, String oldPassword, String newPassword) {
        updateKeycloakUserPassword(keycloakUserId, domainHolder.getDomain(),
                () -> representationMapper.getCredentialsForChangeOperation(newPassword, oldPassword));
    }

    @Override
    public void clearResetPasswordRequiredFlag(String keycloakUserId) {
        RealmResource realm = getRealm(domainHolder.getDomain());
        UserRepresentation user = realm.users().get(keycloakUserId).toRepresentation();

        if (user.firstAttribute(CHANGE_PASSWORD_ATTRIBUTE) != null) {
            user.getAttributes().remove(CHANGE_PASSWORD_ATTRIBUTE);
            realm.users().get(keycloakUserId).update(user);
        }
    }

    @Override
    public void deleteUser(String keycloakUserId, String domain) {
        log.info("deleted user: {}", keycloakUserId);
        RealmResource realm = getRealm(domain);
        setupUserProductRoles(keycloakUserId, Collections.emptySet(), domain);
        removeMainRoleFromUser(keycloakUserId, realm);
        realm.users().delete(keycloakUserId);
    }

    private void removeMainRoleFromUser(String keycloakUserId, RealmResource realm) {
        RoleRepresentation mainRole = realm.roles()
                .get(ROLE_MAIN)
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
