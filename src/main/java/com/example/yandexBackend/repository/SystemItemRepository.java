package com.example.yandexBackend.repository;

import com.example.yandexBackend.model.SystemItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemItemRepository extends JpaRepository<SystemItem, String> {
}
