package com.example.telegrambot.category;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity (name = "ElectronicsData")
@Data
public class ElectronicItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;


    public ElectronicItem() {

    }

    public ElectronicItem(String name) {
        this.name = name;
    }

}
