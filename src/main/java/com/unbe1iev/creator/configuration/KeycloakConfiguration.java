package com.unbe1iev.creator.configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ToString
@Component
@ConfigurationProperties(prefix = "keycloak.config")
public class KeycloakConfiguration {
    private String user;
    private String password;
    private String url;
    private String realm;
    private String domain;
}
