package com.apmosys.bookmgmt.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AdminLoanResponse(Long id, Long userId, String userName, String userEmail,
        Integer bookId, String title, String author,
        LocalDateTime borrowedAt, LocalDate dueDate, LocalDateTime returnedAt, String status) {}

