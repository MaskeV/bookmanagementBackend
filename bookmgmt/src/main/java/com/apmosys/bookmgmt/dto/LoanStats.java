package com.apmosys.bookmgmt.dto;

public record LoanStats(long total, long active, long overdue, long returned) {}