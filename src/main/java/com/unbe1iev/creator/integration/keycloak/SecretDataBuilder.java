package com.unbe1iev.creator.integration.keycloak;

import java.util.HashMap;
import java.util.Map;

public class SecretDataBuilder {

    private final Map<String, String> fields = new HashMap<>();

    public SecretDataBuilder add(String fieldName, String value) {
        if (value != null) {
            fields.put(fieldName, value);
        }

        return this;
    }

    public String build() {
        StringBuilder stringBuilder = new StringBuilder();
        fields.forEach((key, value) ->
                stringBuilder.append(",").append("\"").append(key).append("\":\"").append(value).append("\""));

        stringBuilder.deleteCharAt(0);

        return "{" + stringBuilder + "}";
    }
}
