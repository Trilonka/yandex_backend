package com.example.yandexBackend.service.impl;

import com.example.yandexBackend.model.SystemItem;
import com.example.yandexBackend.model.SystemItemHistoryUnit;
import com.example.yandexBackend.model.constant.SystemItemType;
import com.example.yandexBackend.repository.SystemItemRepository;
import com.example.yandexBackend.service.SystemItemHistoryUnitService;
import com.example.yandexBackend.service.SystemItemService;
import com.example.yandexBackend.util.exception.SystemItemNotValidException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SystemItemServiceDefault implements SystemItemService {

    private final SystemItemRepository systemItemRepository;
    private final SystemItemHistoryUnitService systemItemHistoryUnitService;
    private final ModelMapper modelMapper;

    @Autowired
    public SystemItemServiceDefault(SystemItemRepository systemItemRepository, SystemItemHistoryUnitService systemItemHistoryUnitService, ModelMapper modelMapper) {
        this.systemItemRepository = systemItemRepository;
        this.systemItemHistoryUnitService = systemItemHistoryUnitService;
        this.modelMapper = modelMapper;
    }

    public Optional<SystemItem> findById(String id) {
        return systemItemRepository.findById(id);
    }

    @Transactional
    public void saveList(List<SystemItem> itemsToSave, String updateDate) {
        Map<String, Integer> itemsInfoForQuickAccess = new HashMap<>();
        ArrayList<SystemItem> orderedItemsToSave = defineItemsOrder(itemsToSave, itemsInfoForQuickAccess);

        for (SystemItem itemToSave : orderedItemsToSave) {
            itemToSave.setDate(updateDate);

            Optional<SystemItem> existingItem = findById(itemToSave.getId());

            if (existingItem.isPresent() && (existingItem.get().getType() != itemToSave.getType()))
                throw new SystemItemNotValidException();

            int sizeDifference = updateSizeAndReturnDifference(itemToSave, existingItem);

            updateItemParents(itemToSave, sizeDifference, orderedItemsToSave, itemsInfoForQuickAccess);
        }

        systemItemRepository.saveAllAndFlush(orderedItemsToSave);

        systemItemHistoryUnitService.saveList(orderedItemsToSave.stream()
                .map(this::convertToSystemItemHistoryUnit)
                .collect(Collectors.toList()));
    }

    @Transactional
    public boolean deleteIfExists(String id) {
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

    public List<SystemItem> getByDate(ZonedDateTime zonedDateTime) {
        return systemItemRepository.findByDateBetween(zonedDateTime.minusDays(1).toString(), zonedDateTime.toString());
    }

    private ArrayList<SystemItem> defineItemsOrder(List<SystemItem> itemsToSave,
                                                   Map<String, Integer> checkedItems)
    {
        HashMap<String, SystemItem> uncheckedItems = new HashMap<>();
        for (SystemItem item : itemsToSave)
            uncheckedItems.put(item.getId(), item);

        if (uncheckedItems.size()<itemsToSave.size())
            throw new SystemItemNotValidException();

        LinkedList<SystemItem> orderedItemsToSave = new LinkedList<>();
        int itemIndex = 0;

        for (SystemItem item : itemsToSave) {
            if (checkedItems.containsKey(item.getId()))
                continue;

            if (item.getParentId()==null || checkedItems.containsKey(item.getParentId()) || findById(item.getParentId()).isPresent()) {
                orderedItemsToSave.addLast(item);
                checkedItems.put(item.getId(), itemIndex++);
                uncheckedItems.remove(item.getId());
            } else if (uncheckedItems.containsKey(item.getParentId())) {
                Deque<String> temporaryItemParentTree = new ArrayDeque<>();
                String tempItemParentId = item.getParentId();

                while (tempItemParentId!=null && !checkedItems.containsKey(tempItemParentId) && findById(item.getParentId()).isEmpty()) {
                    temporaryItemParentTree.addLast(tempItemParentId);
                    tempItemParentId = uncheckedItems.get(tempItemParentId).getParentId();
                }
                while (!temporaryItemParentTree.isEmpty()) {
                    tempItemParentId = temporaryItemParentTree.peekLast();
                    orderedItemsToSave.addLast(uncheckedItems.get(tempItemParentId));
                    checkedItems.put(tempItemParentId, itemIndex++);
                    uncheckedItems.remove(temporaryItemParentTree.pollLast());
                }
                orderedItemsToSave.addLast(item);
                checkedItems.put(item.getId(), itemIndex++);
                uncheckedItems.remove(item.getId());
            } else {
                throw new SystemItemNotValidException();
            }
        }

        return new ArrayList<>(orderedItemsToSave);
    }

    private int updateSizeAndReturnDifference(SystemItem itemToSave, Optional<SystemItem> existingItem) {
        if (itemToSave.getType()==SystemItemType.FOLDER) {
            if (existingItem.isPresent())
                itemToSave.setSize(existingItem.get().getSize());
            else
                itemToSave.setSize(0);
        }

        int sizeDifference = itemToSave.getSize();

        if (itemToSave.getParentId()!=null && existingItem.isPresent())
            sizeDifference = itemToSave.getSize() - existingItem.get().getSize();

        return sizeDifference;
    }

    private void updateItemParents(SystemItem itemToSave,
                                   int sizeDifference,
                                   ArrayList<SystemItem> orderedItemsToSave,
                                   Map<String, Integer> itemsInfoForQuickAccess)
    {
        SystemItem itemParent;
        String itemParentId = itemToSave.getParentId();

        while (itemParentId!=null) {
            if (findById(itemParentId).isPresent())
                itemParent = findById(itemParentId).get();
            else
                itemParent = orderedItemsToSave.get(itemsInfoForQuickAccess.get(itemParentId));

            if (itemParent.getType()==SystemItemType.FILE)
                throw new SystemItemNotValidException();

            itemParent.setSize(itemParent.getSize() + sizeDifference);
            itemParent.setDate(itemToSave.getDate());

            itemParentId = itemParent.getParentId();
        }
    }

    private SystemItemHistoryUnit convertToSystemItemHistoryUnit(SystemItem systemItem) {
        return modelMapper.map(systemItem, SystemItemHistoryUnit.class);
    }
}
