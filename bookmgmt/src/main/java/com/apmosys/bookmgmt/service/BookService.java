package com.apmosys.bookmgmt.service;

import java.lang.annotation.Documented;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.apmosys.bookmgmt.document.BookDocument;
import com.apmosys.bookmgmt.entity.Book;
import com.apmosys.bookmgmt.repository.BookRepository;
import com.apmosys.bookmgmt.searchrepository.BookSearchRepository;

@Service
public class BookService {
	
	private BookRepository bookRepository;
	private BookSearchRepository bookSearchRepository;
	
	
	public BookService(BookRepository bookRepository, BookSearchRepository bookSearchRepository) {
		super();
		this.bookRepository = bookRepository;
		this.bookSearchRepository = bookSearchRepository;
	}

	public Book findBookById(int id) {
		
		Optional<Book> bookOptional = bookRepository.findById(id);
		
		if(bookOptional==null) {
			System.out.println("Book not found");
			return null;
		}
		
		return bookOptional.get();
		
	}

	public void addBook(Book book) {
		book.setCreatedAt(LocalDateTime.now());
		
	  Book savedBook =	bookRepository.save(book);
	  
	  bookSearchRepository.save(mapToBookDocument(savedBook));

	}
	
	public void deleteBook(int id)  {
		
     Optional<Book> bookOptional = bookRepository.findById(id);
		
		if(bookOptional==null) {
			System.out.println("Book not found");
		    return;
		}
		
	
		bookRepository.deleteById(id);
		bookSearchRepository.deleteById(String.valueOf(id));
	}

	public Book updateBook(int id, Book updatedBook) {
		
		// TODO Auto-generated method stub
		
        Book book = this.findBookById(id);
		
		if(book==null) {
			System.out.println("Book not found");
		    return null;
		}

		book.setTitle(updatedBook.getTitle());
		book.setAuthor(updatedBook.getAuthor());
		book.setDescription(updatedBook.getDescription());
		book.setCategory(updatedBook.getCategory());
		book.setPublisher(updatedBook.getPublisher());
		book.setPublicationYear(updatedBook.getPublicationYear());
		book.setLanguage(updatedBook.getLanguage());
		book.setQuantity(updatedBook.getQuantity());
		book.setAvailable(updatedBook.getAvailable());
		book.setUpdatedAt(LocalDateTime.now());
		
	 Book updated =	bookRepository.save(book);
	 bookSearchRepository.save(mapToBookDocument(updated));
	 

		
		return updated;
	}
	
	
	public List<Book> getAllBooks(){
		
		List<Book> books = bookRepository.findAll();
		
		if(books==null) {
			System.out.println("No books Available");
		
		}
		
		return books;
	}
	
	private BookDocument mapToBookDocument(Book book) {
		BookDocument bookDocument = new BookDocument();
		bookDocument.setId(String.valueOf(book.getId()));
		bookDocument.setTitle(book.getTitle());
	    bookDocument.setAuthor(book.getAuthor());
		bookDocument.setDescription(book.getDescription());
		bookDocument.setCategory(book.getCategory());
		bookDocument.setPublisher(book.getPublisher());
		bookDocument.setPublicationYear(book.getPublicationYear());
		bookDocument.setLanguage(book.getLanguage());
		bookDocument.setAvailable(book.getAvailable());
		return bookDocument;
	}

	public void addAllBooks(List<Book> books) {
		// TODO Auto-generated method stub
		
		for(Book book:books) {
			addBook(book);
		}
		
	}
	
	
}
