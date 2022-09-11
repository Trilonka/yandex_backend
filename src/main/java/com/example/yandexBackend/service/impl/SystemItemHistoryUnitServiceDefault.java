package com.example.yandexBackend.service.impl;

import com.example.yandexBackend.model.SystemItemHistoryUnit;
import com.example.yandexBackend.repository.SystemItemHistoryUnitRepository;
import com.example.yandexBackend.service.SystemItemHistoryUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class SystemItemHistoryUnitServiceDefault implements SystemItemHistoryUnitService {

    private final SystemItemHistoryUnitRepository systemItemHistoryUnitRepository;

    @Autowired
    public SystemItemHistoryUnitServiceDefault(SystemItemHistoryUnitRepository systemItemHistoryUnitRepository) {
        this.systemItemHistoryUnitRepository = systemItemHistoryUnitRepository;
    }

    @Override
    @Transactional
    public void saveList(List<SystemItemHistoryUnit> historyUnits) {
        systemItemHistoryUnitRepository.saveAllAndFlush(historyUnits);
    }

    @Override
    public List<SystemItemHistoryUnit> getItemHistory(String unitId, String dateStart, String dateEnd) {
        ZonedDateTime zonedDateStart = ZonedDateTime.parse(dateStart);
        ZonedDateTime zonedDateEnd = ZonedDateTime.parse(dateEnd).minusNanos(1);
        return systemItemHistoryUnitRepository.findByIdAndDateIsBetween(
                unitId,
                zonedDateStart.toString(),
                zonedDateEnd.toString()
        );
    }
}
