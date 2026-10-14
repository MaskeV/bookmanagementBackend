package com.apmosys.bookmgmt.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

// What the API sends for a loan. A DTO (not the entity) so the user's password hash is never exposed.
// status = BORROWED, OVERDUE (past the due date, not returned) or RETURNED
public record LoanResponse(Long id, Integer bookId, String title, String author, String category,
		LocalDateTime borrowedAt, LocalDate dueDate, LocalDateTime returnedAt, String status) {
}
