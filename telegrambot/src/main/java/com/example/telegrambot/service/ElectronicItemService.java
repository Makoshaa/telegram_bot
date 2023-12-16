package com.example.telegrambot.service;

import com.example.telegrambot.category.ElectronicItem;
import com.example.telegrambot.category.ElectronicItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ElectronicItemService {

    private ElectronicItemRepository electronicItemRepository;

    @Autowired
    public ElectronicItemService(ElectronicItemRepository electronicItemRepository) {
        this.electronicItemRepository = electronicItemRepository;
    }

    public List<ElectronicItem> getAllElectronicItems() {
        return electronicItemRepository.findAll();
    }

    public void addElectronicItem(String itemName) {
        ElectronicItem electronicItem = new ElectronicItem(itemName);
        electronicItemRepository.save(electronicItem);
    }

    public boolean removeElectronicItem(String itemName) {
        Optional<ElectronicItem> optionalItem = electronicItemRepository.findByName(itemName);
        if (optionalItem.isPresent()) {
            ElectronicItem itemToRemove = optionalItem.get();
            electronicItemRepository.delete(itemToRemove);
            return true;
        } else {
            return false;
        }
    }
}
