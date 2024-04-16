package com.esen.demo.shell;

import com.esen.demo.model.Book;
import com.esen.demo.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.stream.Collectors;

@ShellComponent
@ShellCommandGroup("Book shell idk")
@RequiredArgsConstructor
public class BookHandler {

    private final BookService bookService;

    @ShellMethod(value = "Create a book", key = "create book")
    public void createBook(String title, String author, String publisher, Double price) {
        bookService.save(Book.builder()
                .title(title)
                .author(author)
                .publisher(publisher)
                .price(price)
                .build());
    }

    @ShellMethod(value = "List the books", key = "list books")
    public String listBooks() {
        return bookService.findAll()
                .stream()
                .map(book -> "ID: %d, Title: %s, Author: %s, Publisher: %s, Price: %f".formatted(
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getPublisher(),
                        book.getPrice()
                )).collect(Collectors.joining(System.lineSeparator()));
    }
}