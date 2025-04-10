package org.example.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookFilter {

    private String search;
    private int page = 0;
    private int size = 10;
    private Integer year;
    private String brand;
    private String title;

    public String nextPageLink(int maxPage) {
        return maxPage > 1  && maxPage-1 > page ? "/bookstore/api/books?" + Stream.of(
                        search != null ? "search=" + encode(search) : null,
                        maxPage-1 > page ? "page=" + Integer.valueOf(page + 1) : null,
                        size != 10 ? "size=" + size : null,
                        year != null ? "year=" + year : null,
                        brand != null ? "brand=" + encode(brand) : null,
                        title != null ? "title=" + encode(title) : null
                )
                .filter(Objects::nonNull)
                .collect(Collectors.joining("&")) : null;
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
