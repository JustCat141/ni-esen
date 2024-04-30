package com.esen.demo.shell;

import com.esen.demo.model.Book;
import com.esen.demo.service.BookService;
import com.esen.demo.service.BookstoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@ShellComponent
@ShellCommandGroup("Book shell idk")
@RequiredArgsConstructor
public class BookHandler {

    private final BookService bookService;
    private final BookstoreService bookstoreService;

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

    @ShellMethod(value = "Deletes book", key = "delete book")
    public void deleteBook(Long id) {
        bookService.deleteBook(id);
    }

    @ShellMethod(value = "Updates bookstore", key = "update bookstore")
    public void updateBook(Long id,
                           @ShellOption(defaultValue = ShellOption.NULL) String title,
                           @ShellOption(defaultValue = ShellOption.NULL) String author,
                           @ShellOption(defaultValue = ShellOption.NULL) String publisher,
                           @ShellOption(defaultValue = ShellOption.NULL) Double price) {
        bookService.updateBook(id, title, author, publisher, price);
    }

    @ShellMethod(value = "Lists books by location price", key = "list bookByPrices")
    public String listBookPrice(Long id) {
        var booksByModifiedPrice = bookstoreService.findBookPrice(id);

        var book = bookService.findById(id);

        String header = "Book: %s, Base Price: %f\n".formatted(book.getTitle(), book.getPrice());

        return header + booksByModifiedPrice
                .entrySet()
                .stream()
                .map(bookByPrice -> "Location: %s, Price: %s".formatted(
                        bookByPrice.getKey().getLocation(),
                        bookByPrice.getValue()
                )).collect(Collectors.joining(System.lineSeparator()));
    }
}
