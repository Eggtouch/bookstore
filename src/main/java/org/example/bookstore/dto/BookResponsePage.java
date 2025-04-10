package org.example.bookstore.dto;

import org.example.bookstore.model.Book;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.util.List;

public record BookResponsePage(List<Book> data, int page, int size, long totalElements, int totalPages, String nextPageLink, Instant timestamp) {
    public BookResponsePage(BookFilter filter, Page<Book> page) {
        this(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), filter.nextPageLink(page.getTotalPages()), Instant.now());
    }
}
