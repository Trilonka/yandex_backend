package com.example.yandexBackend.repository;

import com.example.yandexBackend.model.SystemItemHistoryUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemItemHistoryUnitRepository extends JpaRepository<SystemItemHistoryUnit, Integer> {

    List<SystemItemHistoryUnit> findByIdAndDateIsBetween(String id, String low, String high);
}
