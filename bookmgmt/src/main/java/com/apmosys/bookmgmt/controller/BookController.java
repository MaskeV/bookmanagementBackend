package com.apmosys.bookmgmt.controller;


import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.apmosys.bookmgmt.document.BookDocument;
import com.apmosys.bookmgmt.dto.AddBookDto;
import com.apmosys.bookmgmt.entity.Book;
import com.apmosys.bookmgmt.repository.BookRepository;
import com.apmosys.bookmgmt.searchrepository.BookSearchRepository;
import com.apmosys.bookmgmt.service.BookService;

@CrossOrigin
@RestController
@RequestMapping("/api/books")
public class BookController {
	
	private BookService bookService;
	private BookSearchRepository bookSearchRepository;
	
	
	public BookController(BookService bookService, BookSearchRepository bookSearchRepository) {
		super();
		this.bookService = bookService;
		this.bookSearchRepository = bookSearchRepository;
	}

	@GetMapping("/{id}")
	public Book getBook(@PathVariable int id) {
		return bookService.findBookById(id);
	}
	
	@PostMapping
	public void addBook(@RequestBody Book book) {
		
		bookService.addBook(book);
		
	}
	
	@PostMapping("/addAll")
	public void addAllBooks(@RequestBody List<Book> books) {
		bookService.addAllBooks(books);
	}
	@DeleteMapping("/{id}")
	public void deleteBook(@PathVariable int id) {
		bookService.deleteBook(id);
	}
	
	@PutMapping("/{id}")
	public Book updateBook(@PathVariable int id, @RequestBody Book book) {
		return bookService.updateBook(id,book);
	}

	
	@GetMapping
	public List<Book> getBooks(){
		return bookService.getAllBooks();
	}
	
	@GetMapping("/search")
	public List<BookDocument> searchBook(@RequestParam String query) {
		return  bookSearchRepository.findByTitleContainingOrAuthorContainingOrDescriptionContaining(query, query,query);
	}
}
