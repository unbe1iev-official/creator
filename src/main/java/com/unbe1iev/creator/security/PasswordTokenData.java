package com.unbe1iev.creator.security;

import lombok.Getter;

@Getter
public class PasswordTokenData {

    private String email;

    // more complex in the future
    public PasswordTokenData(String stringData) {
        this.email = stringData;
    }

    @Override
    public String toString() {
        return email;
    }
}
