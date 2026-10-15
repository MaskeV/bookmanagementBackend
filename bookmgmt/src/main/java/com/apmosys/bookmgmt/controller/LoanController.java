package com.apmosys.bookmgmt.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.apmosys.bookmgmt.dto.LoanResponse;
import com.apmosys.bookmgmt.service.LoanService;

// Any logged-in user (USER or ADMIN) can borrow and return. SecurityConfig already requires a login for
// everything outside /api/books/**, so no change is needed there.
// authentication.getName() is the logged-in user's email (the JWT subject).
@RestController
@RequestMapping("/api/loans")
public class LoanController {

	private final LoanService loanService;

	public LoanController(LoanService loanService) {
		this.loanService = loanService;
	}

	@PostMapping("/{bookId}")
	@ResponseStatus(HttpStatus.CREATED)
	public LoanResponse borrow(@PathVariable Integer bookId, Authentication authentication) {
		return loanService.borrow(authentication.getName(), bookId);
	}

	@PostMapping("/{loanId}/return")
	public LoanResponse giveBack(@PathVariable Long loanId, Authentication authentication) {
		return loanService.giveBack(authentication.getName(), loanId);
	}

	@GetMapping("/my")
	public List<LoanResponse> myLoans(Authentication authentication) {
		return loanService.myLoans(authentication.getName());
	}

	// ADMIN only (enforced in SecurityConfig): every loan of every user
	@GetMapping
	public List<LoanResponse> allLoans() {
		return loanService.allLoans();
	}
}