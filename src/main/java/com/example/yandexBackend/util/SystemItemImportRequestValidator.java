package com.example.yandexBackend.util;

import com.example.yandexBackend.dto.SystemItemImport;
import com.example.yandexBackend.dto.SystemItemImportRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.stream.Collectors;

@Component
public class SystemItemImportRequestValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(SystemItemImportRequest.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SystemItemImportRequest request = (SystemItemImportRequest) target;
        int itemsSize = request.getItems().stream()
                .collect(Collectors.groupingBy(SystemItemImport::getId))
                .keySet()
                .size();
        if (itemsSize != request.getItems().size())
            errors.rejectValue("items", "400");
    }
}
