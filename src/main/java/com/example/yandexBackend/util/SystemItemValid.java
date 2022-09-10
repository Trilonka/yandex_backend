package com.example.yandexBackend.util;

import com.example.yandexBackend.model.SystemItem;
import com.example.yandexBackend.model.constant.SystemItemType;
import com.example.yandexBackend.service.SystemItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
public class SystemItemValid implements Validator {

    private final SystemItemService itemService;

    @Autowired
    public SystemItemValid(SystemItemService itemService) {
        this.itemService = itemService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(SystemItem.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SystemItem item = (SystemItem) target;
        Optional<SystemItem> itemDb = itemService.findById(item.getId());

        if (item.getParentId()!=null) {
            SystemItem parent = itemService.findById(item.getParentId()).orElse(null);
            if (parent != null && parent.getType() == SystemItemType.FILE) {
                errors.rejectValue("parentId", "400");
            }
        }
        if (itemDb.isPresent() && itemDb.get().getType()!=item.getType())
            errors.rejectValue("type", "400");
        if (item.getType()==SystemItemType.FOLDER) {
            if (item.getUrl()!=null)
                errors.rejectValue("url", "400");
            if (item.getSize()!=null)
                errors.rejectValue("size", "400");
        }
        if (item.getType()==SystemItemType.FILE) {
            if (item.getUrl().length()>255)
                errors.rejectValue("url", "400");
            if (item.getSize()<=0)
                errors.rejectValue("size", "400");
        }
    }
}
