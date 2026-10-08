package com.apmosys.bookmgmt.searchrepository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.apmosys.bookmgmt.document.BookDocument;

public interface BookSearchRepository extends ElasticsearchRepository<BookDocument, String> {

	
	 List<BookDocument> findByTitleContainingOrAuthorContainingOrDescriptionContaining(String title, String author, String description);
	 
}
