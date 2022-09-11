package com.example.yandexBackend.model;

import com.example.yandexBackend.model.constant.SystemItemType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SystemItemHistoryUnit {

    private String id;
    private String url;
    private String parentId;
    private SystemItemType type;
    private Integer size;
    private String date;
}
