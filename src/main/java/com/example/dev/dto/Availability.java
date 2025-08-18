package com.example.dev.dto;

import org.springframework.data.elasticsearch.annotations.Field;

import java.time.LocalTime;

public record Availability(String day, @Field(name = "start_time") LocalTime start, @Field(name = "end_time") LocalTime end) {
}
