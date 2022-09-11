package com.example.yandexBackend.dto.response;

import com.example.yandexBackend.model.SystemItemHistoryUnit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SystemItemHistoryResponse {

    private List<SystemItemHistoryUnit> items;
}
