package com.apmosys.bookmgmt.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
	
	
	@Query(value = """
		    SELECT l FROM Loan l JOIN FETCH l.book b JOIN FETCH l.user u
		    WHERE (:status IS NULL
		        OR (:status = 'RETURNED' AND l.returnedAt IS NOT NULL)
		        OR (:status = 'BORROWED' AND l.returnedAt IS NULL AND l.dueDate >= :today)
		        OR (:status = 'OVERDUE'  AND l.returnedAt IS NULL AND l.dueDate <  :today))
		      AND (:q IS NULL OR LOWER(u.name) LIKE :q OR LOWER(u.email) LIKE :q OR LOWER(b.title) LIKE :q)
		    """,
		    countQuery = """
		    SELECT COUNT(l) FROM Loan l JOIN l.book b JOIN l.user u
		    WHERE (:status IS NULL
		        OR (:status = 'RETURNED' AND l.returnedAt IS NOT NULL)
		        OR (:status = 'BORROWED' AND l.returnedAt IS NULL AND l.dueDate >= :today)
		        OR (:status = 'OVERDUE'  AND l.returnedAt IS NULL AND l.dueDate <  :today))
		      AND (:q IS NULL OR LOWER(u.name) LIKE :q OR LOWER(u.email) LIKE :q OR LOWER(b.title) LIKE :q)
		    """)
		Page<Loan> findForAdmin(@Param("status") String status, @Param("q") String q,
		                        @Param("today") LocalDate today, Pageable pageable);

		long countByReturnedAtIsNull();
		long countByReturnedAtIsNotNull();
		long countByReturnedAtIsNullAndDueDateBefore(LocalDate today);

		// paged version of "my loans"
		@Query(value = "SELECT l FROM Loan l JOIN FETCH l.book WHERE l.user.email = :email",
		       countQuery = "SELECT COUNT(l) FROM Loan l WHERE l.user.email = :email")
		Page<Loan> findByUserEmail(@Param("email") String email, Pageable pageable);

}
