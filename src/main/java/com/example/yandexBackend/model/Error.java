package com.example.yandexBackend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Error {

    private Integer code;
    private String message;
}
