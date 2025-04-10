package org.example.bookstore.service;

import lombok.RequiredArgsConstructor;
import org.example.bookstore.dto.BookFilter;
import org.example.bookstore.model.Book;
import org.example.bookstore.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    public Page<Book> findBooks(BookFilter filter) {
        var pageable = PageRequest.of(filter.getPage(), filter.getSize(), Sort.by("id").ascending());
        var year = filter.getYear();
        var publisher = filter.getBrand();
        var title = filter.getTitle();
        var search = filter.getSearch();

        return bookRepository.findAll(((root, query, criteriaBuilder) -> {
            var predicates = criteriaBuilder.conjunction();

            if (year != null) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.equal(root.get("publishingYear"), year));
            }

            if (publisher != null && !publisher.isEmpty()) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.like(criteriaBuilder.lower(root.get("brand")), "%" + publisher.toLowerCase() + "%"));
            }

            if (title != null && !title.isEmpty()) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }

            if (search != null && !search.isEmpty()) {
                String lowerSearch = search.toLowerCase();

                if (isNumeric(lowerSearch)) {
                     predicates = criteriaBuilder.and(predicates, criteriaBuilder.or(criteriaBuilder.equal(root.get("id"), Long.valueOf(lowerSearch)),
                                                                    criteriaBuilder.equal(root.get("publishingYear"), year)));
                }

                predicates = criteriaBuilder.and(predicates, criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("vendorCode")), "%" + lowerSearch + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + lowerSearch + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("brand")), "%" + lowerSearch + "%")));
            }

            return predicates;
        }), pageable);
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    public Book updateBook(Book bookToUpdate, Book book) {
        bookToUpdate.setVendorCode(book.getVendorCode());
        bookToUpdate.setTitle(book.getTitle());
        bookToUpdate.setPublishingYear(book.getPublishingYear());
        bookToUpdate.setBrand(book.getBrand());
        bookToUpdate.setStock(book.getStock());
        bookToUpdate.setPrice(book.getPrice());
        return bookRepository.save(bookToUpdate);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    private static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
