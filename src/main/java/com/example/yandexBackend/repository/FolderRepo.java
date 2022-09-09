package com.example.yandexBackend.repository;

import com.example.yandexBackend.model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FolderRepo extends JpaRepository<Folder, Integer> {
}
