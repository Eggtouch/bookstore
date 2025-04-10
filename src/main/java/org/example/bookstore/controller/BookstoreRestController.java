package org.example.bookstore.controller;

import lombok.RequiredArgsConstructor;
import org.example.bookstore.dto.BookFilter;
import org.example.bookstore.dto.BookResponse;
import org.example.bookstore.dto.BookResponsePage;
import org.example.bookstore.model.Book;
import org.example.bookstore.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/bookstore/api")
@RequiredArgsConstructor
public class BookstoreRestController {

    private final BookService bookService;

    @GetMapping("/books")
    public ResponseEntity<BookResponsePage> getBooks(@ModelAttribute BookFilter bookFilter) {
        return ResponseEntity.ok(new BookResponsePage(bookFilter, bookService.findBooks(bookFilter)));
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {
        var book = bookService.getBookById(id);
        return book.map(value -> ResponseEntity.ok(new BookResponse("Book with id " + id + " found", value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/books/new")
    public ResponseEntity<BookResponse> createBook(@RequestBody Book book) {
        book.setId(null);
        Book createdBook = bookService.createBook(book);
        return ResponseEntity
                .created(URI.create("/books/" + createdBook.getId()))
                .body(new BookResponse("Book created successfully", createdBook));
    }

    @PutMapping("/books/{id}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable Long id, @RequestBody Book book) {
        var existingBook = bookService.getBookById(id);
        if (existingBook.isPresent()) {
            book.setId(id);
            var updatedBook = bookService.updateBook(existingBook.get(), book);
            return ResponseEntity.ok(new BookResponse("Book updated successfully", updatedBook));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<Map<String, String>> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Collections.singletonMap("message", "Book with ID " + id + " was deleted successfully"));
    }
}
