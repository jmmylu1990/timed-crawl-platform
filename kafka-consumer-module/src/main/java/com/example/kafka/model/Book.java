package com.example.kafka.model;

import lombok.Data;

public @Data class Book {

    private Long id;
    private String name;

    public Book() {
    }

    public Book(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
