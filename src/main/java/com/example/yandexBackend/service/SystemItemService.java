package com.example.yandexBackend.service;

import com.example.yandexBackend.model.SystemItem;
import com.example.yandexBackend.model.constant.SystemItemType;
import com.example.yandexBackend.repository.SystemItemRepository;
import com.example.yandexBackend.util.SystemItemNotValidException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;

@Service
@Transactional
public class SystemItemService {

    private final SystemItemRepository systemItemRepository;

    @Autowired
    public SystemItemService(SystemItemRepository systemItemRepository) {
        this.systemItemRepository = systemItemRepository;
    }

    @Transactional(readOnly = true)
    public Optional<SystemItem> findById(String id) {
        return systemItemRepository.findById(id);
    }

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

        systemItemRepository.saveAll(orderedItemsToSave);
    }

    private ArrayList<SystemItem> defineItemsOrder(List<SystemItem> itemsToSave,
                                                   Map<String, Integer> itemsInfoForQuickAccess)
    {
        Deque<SystemItem> items = new ArrayDeque<>(itemsToSave);
        LinkedList<SystemItem> orderedItemsToSave = new LinkedList<>();
        String firstItemParentId;
        int itemIndex = 0;
        int iterationsCount = 0;
        final int MAX_ITERATIONS_COUNT = itemsToSave.size()*itemsToSave.size();

        while (!items.isEmpty()) {
            firstItemParentId = items.peekFirst().getParentId();

            if (itemsInfoForQuickAccess.containsKey(items.peekFirst().getId()) || ++iterationsCount > MAX_ITERATIONS_COUNT)
                throw new SystemItemNotValidException();

            if (firstItemParentId==null || itemsInfoForQuickAccess.containsKey(firstItemParentId) || systemItemRepository.findById(firstItemParentId).isPresent()) {
                itemsInfoForQuickAccess.put(items.peekFirst().getId(), itemIndex++);
                orderedItemsToSave.addLast(items.pollFirst());
            } else {
                items.addLast(items.pollFirst());
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

    @Transactional(readOnly = true)
    public List<SystemItem> getByDate(ZonedDateTime zonedDateTime) {
        return systemItemRepository.findByDateBetween(zonedDateTime.minusDays(1).toString(), zonedDateTime.toString());
    }
}
