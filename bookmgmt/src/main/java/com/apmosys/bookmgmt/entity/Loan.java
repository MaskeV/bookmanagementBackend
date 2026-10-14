package com.apmosys.bookmgmt.entity;



import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// One row = one borrowing of one book by one user. returnedAt stays null until it is returned.
@Entity
@Table(name = "loans")
public class Loan {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id")
	private AppUser user;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "book_id")
	private Book book;

	private LocalDateTime borrowedAt;
	private LocalDate dueDate;
	private LocalDateTime returnedAt;

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public AppUser getUser() { return user; }
	public void setUser(AppUser user) { this.user = user; }
	public Book getBook() { return book; }
	public void setBook(Book book) { this.book = book; }
	public LocalDateTime getBorrowedAt() { return borrowedAt; }
	public void setBorrowedAt(LocalDateTime borrowedAt) { this.borrowedAt = borrowedAt; }
	public LocalDate getDueDate() { return dueDate; }
	public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
	public LocalDateTime getReturnedAt() { return returnedAt; }
	public void setReturnedAt(LocalDateTime returnedAt) { this.returnedAt = returnedAt; }
}

