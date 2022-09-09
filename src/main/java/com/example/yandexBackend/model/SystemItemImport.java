package com.example.yandexBackend.model;

import com.example.yandexBackend.model.constant.SystemItemType;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@RequiredArgsConstructor
public class SystemItemImport {

    @NotNull
    private String id;

    private String url;
    private String parentId;
    private SystemItemType type;
    private Integer size;
}
