package com.unbe1iev.creator.configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ToString
@Component
public class KeycloakConfiguration {
    private String user;
    private String password;
    private String url;
    private String realm;
    private String domain;
}
