package com.example.yandexBackend.repository;

import com.example.yandexBackend.model.SystemItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemItemRepository extends JpaRepository<SystemItem, String> {

    void deleteByDate(String date);
    Optional<SystemItem> findByDate(String date);
}
