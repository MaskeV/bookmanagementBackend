package com.apmosys.bookmgmt.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.apmosys.bookmgmt.dto.AdminLoanResponse;
import com.apmosys.bookmgmt.dto.LoanStats;
import com.apmosys.bookmgmt.dto.PageResponse;
import com.apmosys.bookmgmt.service.LoanService;

@RestController
@RequestMapping("/api/admin/loans")
public class AdminLoanController {
    private final LoanService loanService;
    public AdminLoanController(LoanService loanService) { this.loanService = loanService; }

    @GetMapping
    public PageResponse<AdminLoanResponse> all(@RequestParam(required = false) String status,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return loanService.allLoans(status, query, page, size);
    }

    @GetMapping("/stats")
    public LoanStats stats() { return loanService.stats(); }
}