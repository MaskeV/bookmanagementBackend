package com.apmosys.bookmgmt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.apmosys.bookmgmt.entity.Book;

public interface BookRepository extends JpaRepository<Book, Integer> {
	
	 @Query("""
		        SELECT b FROM Book b
		        WHERE (:category  IS NULL OR b.category  = :category)
		          AND (:language  IS NULL OR b.language  = :language)
		          AND (:publisher IS NULL OR b.publisher = :publisher)
		          AND (:available IS NULL OR b.available = :available)
		        """)
		    List<Book> findByFilters(@Param("category") String category,
		                             @Param("language") String language,
		                             @Param("publisher") String publisher,
		                             @Param("available") Boolean available);

}
