package com.apmosys.bookmgmt.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apmosys.bookmgmt.dto.LoanResponse;
import com.apmosys.bookmgmt.entity.AppUser;
import com.apmosys.bookmgmt.entity.Book;
import com.apmosys.bookmgmt.entity.Loan;
import com.apmosys.bookmgmt.exception.LoanException;
import com.apmosys.bookmgmt.repository.BookRepository;
import com.apmosys.bookmgmt.repository.LoanRepository;
import com.apmosys.bookmgmt.repository.UserRepository;

@Service
public class LoanService {

	public static final int MAX_ACTIVE_LOANS = 5; // books one user may hold at the same time
	public static final int LOAN_DAYS = 14;       // days until a book is due

	private final LoanRepository loanRepository;
	private final BookRepository bookRepository;
	private final UserRepository userRepository;

	public LoanService(LoanRepository loanRepository, BookRepository bookRepository, UserRepository userRepository) {
		this.loanRepository = loanRepository;
		this.bookRepository = bookRepository;
		this.userRepository = userRepository;
	}

	// One transaction: lock the book, check the rules, take one copy, save the loan.
	// If anything throws, nothing is saved.
	@Transactional
	public LoanResponse borrow(String email, Integer bookId) {
		AppUser user = userRepository.findByEmail(email)
				.orElseThrow(() -> new LoanException(HttpStatus.UNAUTHORIZED, "User not found"));
		Book book = bookRepository.findByIdForUpdate(bookId)
				.orElseThrow(() -> new LoanException(HttpStatus.NOT_FOUND, "Book not found"));

		int copies = book.getQuantity() == null ? 0 : book.getQuantity();
		if (!Boolean.TRUE.equals(book.getAvailable()) || copies <= 0)
			throw new LoanException(HttpStatus.CONFLICT, "No copies of this book are available right now");
		if (loanRepository.hasActiveLoan(email, bookId))
			throw new LoanException(HttpStatus.CONFLICT, "You have already borrowed this book");
		if (loanRepository.countOverdue(email, LocalDate.now()) > 0)
		    throw new LoanException(HttpStatus.CONFLICT,
		            "You have overdue books. Please return them before borrowing more.");
		if (loanRepository.countActive(email) >= MAX_ACTIVE_LOANS)
			throw new LoanException(HttpStatus.CONFLICT,
					"You can borrow at most " + MAX_ACTIVE_LOANS + " books at a time. Return one first.");

		book.setQuantity(copies - 1);
		if (copies - 1 == 0)
			book.setAvailable(false);
		book.setUpdatedAt(LocalDateTime.now());
		bookRepository.save(book);

		Loan loan = new Loan();
		loan.setUser(user);
		loan.setBook(book);
		loan.setBorrowedAt(LocalDateTime.now());
		loan.setDueDate(LocalDate.now().plusDays(LOAN_DAYS));
		return toResponse(loanRepository.save(loan));
	}

	@Transactional
	public LoanResponse giveBack(String email, Long loanId) {
		Loan loan = loanRepository.findById(loanId)
				.orElseThrow(() -> new LoanException(HttpStatus.NOT_FOUND, "Loan not found"));
		if (!loan.getUser().getEmail().equalsIgnoreCase(email))
			throw new LoanException(HttpStatus.FORBIDDEN, "This loan belongs to another user");
		if (loan.getReturnedAt() != null)
			throw new LoanException(HttpStatus.CONFLICT, "This book was already returned");

		Book book = bookRepository.findByIdForUpdate(loan.getBook().getId())
				.orElseThrow(() -> new LoanException(HttpStatus.NOT_FOUND, "Book not found"));
		int copies = book.getQuantity() == null ? 0 : book.getQuantity();
		book.setQuantity(copies + 1);
		if (copies == 0)
			book.setAvailable(true); // it was borrowed out completely, now one copy is back
		book.setUpdatedAt(LocalDateTime.now());
		bookRepository.save(book);

		loan.setReturnedAt(LocalDateTime.now());
		return toResponse(loanRepository.save(loan));
	}

	@Transactional(readOnly = true)
	public List<LoanResponse> myLoans(String email) {
		return loanRepository.findByUserEmail(email).stream().map(this::toResponse).toList();
	}

	private LoanResponse toResponse(Loan l) {
		String status = l.getReturnedAt() != null ? "RETURNED"
				: l.getDueDate().isBefore(LocalDate.now()) ? "OVERDUE" : "BORROWED";
		Book b = l.getBook();
		return new LoanResponse(l.getId(), b.getId(), b.getTitle(), b.getAuthor(), b.getCategory(),
				l.getBorrowedAt(), l.getDueDate(), l.getReturnedAt(), status);
	}
}
