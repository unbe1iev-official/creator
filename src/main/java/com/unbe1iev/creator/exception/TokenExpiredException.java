package com.unbe1iev.creator.exception;

import com.unbe1iev.common.exception.DefaultRuntimeException;

public class TokenExpiredException extends DefaultRuntimeException {

    private final String[] codes = new String[]{"exception.token.expired"};

    @Override
    public String[] getCodes() {
        return codes;
    }
}
