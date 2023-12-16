package com.example.telegrambot.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ElectronicItemRepository extends JpaRepository <ElectronicItem, Long> {
    Optional<ElectronicItem> findByName(String name);
}
