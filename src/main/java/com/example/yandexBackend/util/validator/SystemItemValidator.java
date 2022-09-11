package com.example.yandexBackend.util.validator;

import com.example.yandexBackend.model.SystemItem;
import com.example.yandexBackend.model.constant.SystemItemType;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class SystemItemValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(SystemItem.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SystemItem item = (SystemItem) target;

        if (item.getType()==SystemItemType.FOLDER) {
            if (item.getUrl()!=null)
                errors.reject("400");
            if (item.getSize()!=null)
                errors.reject("400");
        }
        if (item.getType()==SystemItemType.FILE) {
            if (item.getUrl().length()>255)
                errors.reject("400");
            if (item.getSize()<=0)
                errors.reject("400");
        }
    }
}
