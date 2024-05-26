package com.unbe1iev.creator.integration.keycloak.exception;

import com.unbe1iev.common.exception.DefaultRuntimeException;

public class UserNotFoundException extends DefaultRuntimeException {

    private final String[] arguments;

    public UserNotFoundException(String email) {
        this.arguments = new String[] { email };
    }

    @Override
    public Object[] getArguments() {
        return arguments;
    }
}
