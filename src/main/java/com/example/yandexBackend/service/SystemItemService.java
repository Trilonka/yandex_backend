package com.example.yandexBackend.service;

import com.example.yandexBackend.model.SystemItem;

import java.time.ZonedDateTime;
import java.util.*;

public interface SystemItemService {

    Optional<SystemItem> findById(String id);

    void saveList(List<SystemItem> itemsToSave, String updateDate);

    boolean deleteIfExists(String id);

    List<SystemItem> getByDate(ZonedDateTime zonedDateTime);
}
