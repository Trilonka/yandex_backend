package com.example.yandexBackend.service;

import com.example.yandexBackend.model.SystemItem;
import com.example.yandexBackend.repository.SystemItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class SystemItemService {

    private final SystemItemRepository systemItemRepository;

    @Autowired
    public SystemItemService(SystemItemRepository systemItemRepository) {
        this.systemItemRepository = systemItemRepository;
    }

    public Optional<SystemItem> findById(String id) {
        return systemItemRepository.findById(id);
    }

    @Transactional
    public void save(SystemItem systemItem, String dateUpdate) {
        int differenceSize = 0;
        Optional<SystemItem> existingItem = findById(systemItem.getId());
        if (existingItem.isPresent())
            differenceSize = systemItem.getSize() - existingItem.get().getSize();
        SystemItem parent = findById(systemItem.getParentId()).orElse(null);
        while (parent!=null) {
            parent.setSize(parent.getSize() + differenceSize);
            parent.setDate(dateUpdate);
            parent = findById(systemItem.getParentId()).orElse(null);
        }
        systemItem.setDate(dateUpdate);
        systemItemRepository.save(systemItem);
    }

    @Transactional
    public void saveList(List<SystemItem> items, String date) {
        items.forEach(e -> save(e, date));
    }

    @Transactional
    public boolean removeIfExists(String id) {
        if (systemItemRepository.findById(id).isPresent()) {
            systemItemRepository.deleteById(id);
            return true;
        }
        if (systemItemRepository.findByDate(id).isPresent()) {
            systemItemRepository.deleteByDate(id);
            return true;
        }
        return false;
    }
}
