package com.apmosys.bookmgmt.dto;

import java.util.List;

// Distinct values used to fill the filter dropdowns in the UI
public record FilterOptions(List<String> categories, List<String> languages, List<String> publishers) {
}
