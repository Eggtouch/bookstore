package org.example.bookstore.dto;

import org.example.bookstore.model.Book;

import java.time.Instant;

public record BookResponse(String message, Book data, Instant timestamp) {
    public BookResponse(String message, Book data) {
        this(message, data, Instant.now());
    }
}
