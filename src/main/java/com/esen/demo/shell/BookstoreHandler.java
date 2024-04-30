package com.esen.demo.shell;

import com.esen.demo.model.Bookstore;
import com.esen.demo.service.BookstoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.stream.Collectors;

@ShellComponent
@ShellCommandGroup("Bookstore related commands")
@RequiredArgsConstructor
public class BookstoreHandler {

    private final BookstoreService bookstoreService;

    @ShellMethod(value = "Creates bookstore", key = "create bookstore")
    public void save(String location, Double priceModifier, Double moneyInCashRegister) {
        var bookstore = Bookstore.builder()
                .location(location)
                .priceModifier(priceModifier)
                .moneyInCashRegister(moneyInCashRegister)
                .build();
        bookstoreService.save(bookstore);
    }

    @ShellMethod(value = "Lists all bookstores", key = "list bookstores")
    public String listBookstores() {
        return bookstoreService.findAll()
                .stream()
                .map(bookstore -> "ID: %d, Location: %s, Price Modifier: %f, Money in cash register: %f".formatted(
                   bookstore.getId(),
                        bookstore.getLocation(),
                        bookstore.getPriceModifier(),
                        bookstore.getMoneyInCashRegister()
                )).collect(Collectors.joining(System.lineSeparator()));
    }

    @ShellMethod(value = "Deletes bookstore", key = "delete bookstore")
    public void deleteBookstore(Long id) {
        bookstoreService.deleteBookstore(id);
    }

    @ShellMethod(value = "Updates bookstore", key = "update bookstore")
    public void updateBookstore(Long id,
                                @ShellOption(defaultValue = ShellOption.NULL) String location,
                                @ShellOption(defaultValue = ShellOption.NULL) Double priceModifier,
                                @ShellOption(defaultValue = ShellOption.NULL) Double moneyInCashRegister) {
        bookstoreService.updateBookstore(id,location,priceModifier,moneyInCashRegister);
    }

    @ShellMethod(value = "Lists the bookstores inventory", key = "list stock")
    public String listInventory(Long id) {
        var bookStore = bookstoreService.findById(id);

        return bookStore.getInventory()
                .entrySet()
                .stream()
                .map(bookIntegerEntry -> "Book ID: %s, Title: %s, Author: %s, Copies: %s".formatted(
                        bookIntegerEntry.getKey().getId(),
                        bookIntegerEntry.getKey().getTitle(),
                        bookIntegerEntry.getKey().getAuthor(),
                        bookIntegerEntry.getValue()
                )).collect(Collectors.joining(System.lineSeparator()));
    }

    // tudjunk hozzáadni, Booksore, Book, Amount
    @ShellMethod(value = "Add book to the stores inventory", key = "add stock")
    public void addStock(Long bookStoreId, Long bookId, Integer amount) {
        bookstoreService.changeStock(bookStoreId, bookId, amount);
    }
}
