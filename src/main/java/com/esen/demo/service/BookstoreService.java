package com.esen.demo.service;

import com.esen.demo.model.Book;
import com.esen.demo.model.Bookstore;
import com.esen.demo.repository.BookRepository;
import com.esen.demo.repository.BookstoreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BookstoreService {

    private final BookstoreRepository bookstoreRepository;
    private final BookRepository bookRepository;

    public void save(Bookstore bookstore) {
        bookstoreRepository.save(bookstore);
    }

    @Transactional
    public void removeBookFromInventory(Book book) {
        bookstoreRepository.findAll()
                .forEach((bookstore -> {
                    bookstore.getInventory().remove(book);
                    bookstoreRepository.save(bookstore);
                }));
    }

    public List<Bookstore> findAll() {
        return bookstoreRepository.findAll();
    }

    public void deleteBookstore(Long id) {
        var bookstore = bookstoreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot find book!"));
        bookstoreRepository.delete(bookstore);
    }

    public void updateBookstore(Long id, String location, Double priceModifier, Double moneyInCashRegister) {
        if (Stream.of(location,priceModifier,moneyInCashRegister).allMatch(Objects::isNull)) {
            throw new UnsupportedOperationException("There's nothing to update");
        }

        var bookstore = bookstoreRepository.findById(id).orElseThrow(() -> new RuntimeException("Cannot find book!"));

        if (location != null) {
            bookstore.setLocation(location);
        }

        if (priceModifier != null) {
            bookstore.setPriceModifier(priceModifier);
        }

        if (moneyInCashRegister != null) {
            bookstore.setMoneyInCashRegister(moneyInCashRegister);
        }

        bookstoreRepository.save(bookstore);
    }

    public Map<Bookstore, Double> findBookPrice(Long id) {
        var book = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Cannot find book"));
        var bookStores = bookstoreRepository.findAll();
        var bookPrice = book.getPrice();

        Map<Bookstore,Double> result = new HashMap<>();
        for (var b : bookStores) {
            if (b.getInventory().containsKey(book)) {
                Double newPrice = book.getPrice() * b.getPriceModifier();
                result.put(b,newPrice);
            }
        }

        return result;
    }

    public Map<Book, Integer> getStock(Long id) {
        var bookstore = bookstoreRepository.findById(id).orElseThrow(() -> new RuntimeException("Cannot find bookstore!"));
        return bookstore.getInventory();
    }

    public Bookstore findById(Long id) {
        return bookstoreRepository.findById(id).orElseThrow(() -> new RuntimeException("Cannot find bookstore!"));
    }

    public void changeStock(Long bookStoreId, Long bookId, Integer amount) {
        var bookstore = findById(bookStoreId);
        var book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Cannot find bookstore!"));

        // Ha benne van frissítjuk
        if (bookstore.getInventory().containsKey(book)) {
            var entry = bookstore.getInventory().get(book);
            var newPrice = entry + amount;

            if (newPrice < 0) {
                throw new UnsupportedOperationException("Invalid operation");
            }

            bookstore.getInventory().replace(book,newPrice);
        }
        // Ha nincs benne, hozzáadjuk
        else {
            if (amount < 1) {
                throw new IllegalArgumentException("Amount cannot be less than one!");
            }
            bookstore.getInventory().put(book,amount);
        }

        bookstoreRepository.save(bookstore);
    }
}