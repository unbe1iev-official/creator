package com.unbe1iev.creator.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;

@Component
@Slf4j
public class PasswordTokenProvider extends TokenProvider<PasswordTokenData> {

    public static final String FAKE_TOKEN = "FAKE_TOKEN";

    public PasswordTokenProvider(@Value("${token.provider.password:TgY55sz#25kLm3}") String keystorePassword) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableKeyException {
        super(keystorePassword);
    }

    @Override
    protected PasswordTokenData createTokenData(String tokenData) {
        return new PasswordTokenData(tokenData);
    }

    @Override
    protected LocalDateTime createExpiresAt() {
        return LocalDateTime.now().plusHours(1);
    }
}

