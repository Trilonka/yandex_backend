package com.example.yandexBackend.service;

import com.example.yandexBackend.model.SystemItemHistoryUnit;
import java.util.List;

public interface SystemItemHistoryUnitService {

    void saveList(List<SystemItemHistoryUnit> historyUnits);

    List<SystemItemHistoryUnit> getItemHistory(String unitId, String dateStart, String dateEnd);
}
