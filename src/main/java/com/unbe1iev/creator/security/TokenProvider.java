package com.unbe1iev.creator.security;

import com.unbe1iev.common.exception.DefaultRuntimeException;
import com.unbe1iev.creator.exception.TokenException;
import com.unbe1iev.creator.exception.TokenExpiredException;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.security.access.AccessDeniedException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.util.Base64;

@Slf4j
public abstract class TokenProvider<T> {

    private static final String KEYSTORE_ALIAS = "tokenkeys";
    private static final String KEYSTORE_NAME = "tokenkeystore.p12";
    private static final String KEYSTORE_TYPE = "PKCS12";
    private static final String TRANSFORMATION = "RSA/None/OAEPWITHSHA-256ANDMGF1PADDING";

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    protected TokenProvider(String keystorePassword) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableKeyException {

        Security.addProvider(new BouncyCastleProvider());

        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
        keyStore.load(getClass().getClassLoader().getResourceAsStream(KEYSTORE_NAME), keystorePassword.toCharArray());
        Certificate certificate = keyStore.getCertificate(KEYSTORE_ALIAS);

        this.privateKey = (PrivateKey) keyStore.getKey(KEYSTORE_ALIAS, keystorePassword.toCharArray());
        this.publicKey = certificate.getPublicKey();
    }

    public String create(T tokenData) {
        String messageToSign = tokenData + "|" + createExpiresAt();

        try {
            return createInternal(messageToSign);
        } catch (Exception e) {
            throw new TokenException(e);
        }
    }

    private String createInternal(String messageToSign) throws NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchProviderException {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION, BouncyCastleProvider.PROVIDER_NAME);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] encryptedValue = cipher.doFinal(messageToSign.getBytes(StandardCharsets.UTF_8));
        return new String(Base64.getUrlEncoder().encode(encryptedValue), StandardCharsets.UTF_8);
    }

    @NotNull
    public T getTokenData(@NotNull String token) {
        String[] tokenParts = getTokenParts(token);
        log.info("getTokenData exp time {}, {}", tokenParts[1], LocalDateTime.parse(tokenParts[1]));
        if (LocalDateTime.parse(tokenParts[1]).isBefore(LocalDateTime.now())) {
            throw getTokenExpirationException();
        }
        return createTokenData(tokenParts[0]);
    }

    @NotNull
    public T unpackTokenData(@NotNull String token) {
        String[] tokenParts = getTokenParts(token);
        log.info("unpackTokenData, exp time {}, {}", tokenParts[1], LocalDateTime.parse(tokenParts[1]));
        return createTokenData(tokenParts[0]);
    }

    protected DefaultRuntimeException getTokenExpirationException() {
        return new TokenExpiredException();
    }

    protected abstract T createTokenData(String tokenData);

    protected abstract LocalDateTime createExpiresAt();

    private String[] getTokenParts(String token) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION, BouncyCastleProvider.PROVIDER_NAME);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            String decryptedMessage = new String(cipher.doFinal(Base64.getUrlDecoder().decode(token.getBytes(StandardCharsets.UTF_8))), StandardCharsets.UTF_8);
            return decryptedMessage.split("\\|");
        } catch (AccessDeniedException e) {
            throw e;
        } catch (Exception e) {
            throw new TokenException(e);
        }
    }
}
