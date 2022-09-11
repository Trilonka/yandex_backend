package com.example.yandexBackend.dto.response;

import com.example.yandexBackend.model.SystemItem;
import com.example.yandexBackend.model.constant.SystemItemType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SystemItemResponse {

    private String id;
    private String url;
    private String date;
    private String parentId;
    private SystemItemType type;
    private Integer size;
    private List<SystemItem> children;
}
