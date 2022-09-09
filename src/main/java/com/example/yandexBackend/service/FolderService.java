package com.example.yandexBackend.service;

import com.example.yandexBackend.model.Folder;
import com.example.yandexBackend.repository.FolderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class FolderService {

    private final FolderRepo folderRepo;

    @Autowired
    public FolderService(FolderRepo folderRepo) {
        this.folderRepo = folderRepo;
    }

    public Folder get(int id) {
        return folderRepo.findById(id).orElse(null);
    }

    @Transactional
    public void save(Folder folder) {
        folderRepo.save(folder);
    }
}
