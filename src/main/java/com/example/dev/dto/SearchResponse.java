package com.example.dev.dto;

import java.util.List;

public record SearchResponse(List<Teacher> teachers, Pagination pagination, long timeTaken) {
}
