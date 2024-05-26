package com.unbe1iev.creator.integration.keycloak.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class KeycloakErrorDto {

    private String errorMessage;
    private String error;

    public String getMessage() {
        return errorMessage != null ? errorMessage : error;
    }
}
