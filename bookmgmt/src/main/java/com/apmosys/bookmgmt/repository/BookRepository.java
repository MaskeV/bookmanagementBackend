package com.apmosys.bookmgmt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.apmosys.bookmgmt.entity.Book;

public interface BookRepository extends JpaRepository<Book, Integer> {

}
