package com.wxy.design.iterator;

import lombok.Data;

@Data
public class Book {
    private String name;
    public Book(String name) {
        this.name = name;
    }
}
