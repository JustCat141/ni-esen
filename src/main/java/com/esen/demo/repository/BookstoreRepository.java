package com.esen.demo.repository;

import com.esen.demo.model.Bookstore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookstoreRepository extends JpaRepository<Bookstore, Integer> {
}