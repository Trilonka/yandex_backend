package com.example.yandexBackend.dto.request;

import com.example.yandexBackend.model.constant.SystemItemType;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SystemItemImport {

    @NotNull
    private String id;
    private String url;
    private String parentId;
    private SystemItemType type;
    private Integer size;
}
