package com.example.yandexBackend.service;

import com.example.yandexBackend.model.SystemItem;
import com.example.yandexBackend.repository.SystemItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
        Optional<SystemItem> existingItem = findById(systemItem.getId());
        if (systemItem.getSize()==null) {
            if (existingItem.isEmpty())
                systemItem.setSize(0);
            else
                systemItem.setSize(existingItem.get().getSize());
        }
        else {
            int differenceSize = systemItem.getSize();
            if (existingItem.isPresent()) {
                int existingSize = existingItem.get().getSize()==null ? 0 : existingItem.get().getSize();
                differenceSize = systemItem.getSize() - existingSize;
            }

            SystemItem parent;
            String parentId = systemItem.getParentId();
            while (parentId!=null) {
                parent = findById(parentId).get();
                parent.setSize(parent.getSize() + differenceSize);
                parent.setDate(dateUpdate);
                parentId = parent.getParentId();
            }
        }
        systemItem.setDate(dateUpdate);
        systemItemRepository.save(systemItem);
    }

    @Transactional
    public void saveList(List<SystemItem> items, String date) {
        Deque<SystemItem> itemsDeque = new ArrayDeque<>(items);

        while (!itemsDeque.isEmpty()) {
            if (itemsDeque.peekFirst().getParentId()==null || systemItemRepository.findById(itemsDeque.peekFirst().getParentId()).isPresent()) {
                save(itemsDeque.pollFirst(), date);
            } else {
                itemsDeque.addLast(itemsDeque.pollFirst());
            }
        }
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
