package org.example.bookstore.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bookstore.dto.BookFilter;
import org.example.bookstore.model.Book;
import org.example.bookstore.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/bookstore")
@RequiredArgsConstructor
@Slf4j
public class BookstoreController {

    private final BookService bookService;

    @GetMapping
    public String index(@ModelAttribute("filter") BookFilter filter, Model model) {

        Page<Book> books = bookService.findBooks(filter);

        model.addAttribute("books", books);
        model.addAttribute("filter", filter);
        return "bookstore/index";
    }

    @GetMapping("/new")
    public String newBookPage(Model model) {
        model.addAttribute("book", new Book());
        return "bookstore/new";
    }

    @PostMapping("/new")
    public String newBook(Book book) {
        Long id = bookService.createBook(book).getId();
        return "redirect:/bookstore/" + id;
    }

    @GetMapping("/{id}")
    public String showBook(@PathVariable Long id, Model model) {
        var book = bookService.getBookById(id);
        if (book.isPresent()) {
            model.addAttribute("book", book.get());
            return "bookstore/show";
        } else {
            return "redirect:/bookstore";
        }
    }

    @GetMapping("/edit/{id}")
    public String updateBookPage(@PathVariable Long id, Model model) {
        var book = bookService.getBookById(id);
        if (book.isPresent()) {
            model.addAttribute("book", book.get());
            return "bookstore/edit";
        } else {
            return "redirect:/bookstore";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateBook(@PathVariable Long id, Book updatedBook) {
        var existingBook = bookService.getBookById(id);
        if (existingBook.isPresent()) {
            bookService.updateBook(existingBook.get(), updatedBook);
            return "redirect:/bookstore/" + id;
        } else {
            return "redirect:/bookstore";
        }
    }

    @DeleteMapping("/{id}")
    public String deleteBook(@PathVariable Long id) {
        var book = bookService.getBookById(id);
        book.ifPresent(deletedBook -> bookService.deleteBook(deletedBook.getId()));
        return "redirect:/bookstore";
    }

    @ModelAttribute("filter")
    public BookFilter getBookFilter() {
        return new BookFilter();
    }
}
