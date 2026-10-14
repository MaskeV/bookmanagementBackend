package com.apmosys.bookmgmt.repository;

import java.time.LocalDate;
import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.apmosys.bookmgmt.entity.Loan;

public interface LoanRepository extends JpaRepository<Loan, Long> {
	
	@Query("SELECT l FROM Loan l JOIN FETCH l.book WHERE l.user.email = :email ORDER BY l.borrowedAt DESC")
	List<Loan> findByUserEmail(@Param("email") String email);

	@Query("SELECT COUNT(l) FROM Loan l WHERE l.user.email = :email AND l.returnedAt IS NULL")
	long countActive(@Param("email") String email);

	@Query("SELECT COUNT(l) > 0 FROM Loan l WHERE l.user.email = :email AND l.book.id = :bookId AND l.returnedAt IS NULL")
	boolean hasActiveLoan(@Param("email") String email, @Param("bookId") Integer bookId);
	
	@Query("SELECT COUNT(l) FROM Loan l WHERE l.user.email = :email AND l.returnedAt IS NULL AND l.dueDate < :today")
	long countOverdue(@Param("email") String email, @Param("today") LocalDate today);

}
