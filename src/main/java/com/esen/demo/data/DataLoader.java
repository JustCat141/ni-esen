package com.esen.demo.data;

import com.esen.demo.model.Book;
import com.esen.demo.model.Bookstore;
import com.esen.demo.repository.BookRepository;
import com.esen.demo.repository.BookstoreRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.*;

import java.io.IOException;
import java.util.List;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataLoader {
    private final BookRepository bookRepository;
    private final BookstoreRepository bookstoreRepository;

    @Value("classpath:data/books.json")
    private Resource bookResource;
    @Value("classpath:data/bookstores.json")
    private Resource bookstoreResource;

    @PostConstruct
    public void loadData() {
        var objectMapper = new ObjectMapper();

        var bookType = new TypeReference<List<Book>>() { };
        var bookstoreType = new TypeReference<List<Bookstore>>() { };

        try {
            var booksJson = StreamUtils.copyToString(bookResource.getInputStream(), StandardCharsets.UTF_8);
            var books = objectMapper.readValue(booksJson, bookType);
            bookRepository.saveAll(books);

            var bookstoresJson = StreamUtils.copyToString(bookstoreResource.getInputStream(), StandardCharsets.UTF_8);
            var bookstores = objectMapper.readValue(bookstoresJson, bookstoreType);

            bookstores.forEach(bookstore -> {
                bookstore.setInventory(books.stream()
                        .collect(Collectors.toMap(
                                book -> book,
                                book -> ThreadLocalRandom.current().nextInt(1,50)
                        )));
            });
            bookstoreRepository.saveAll(bookstores);

        } catch (IOException e)
        {
            log.error("Cannot load data.", e);
        }
    }
}
