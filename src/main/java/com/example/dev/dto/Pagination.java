package com.example.dev.dto;

public record Pagination(int page, int size, long totalElements, int totalPages) {
}
