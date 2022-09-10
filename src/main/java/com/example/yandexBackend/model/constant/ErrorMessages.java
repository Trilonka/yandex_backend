package com.example.yandexBackend.model.constant;

public enum ErrorMessages {

    NOT_VALID("Validation Failed"),
    NOT_FOUND("Item not found");

    private final String value;

    public String getValue() {
        return value;
    }
    ErrorMessages(String value) {
        this.value = value;
    }
}
