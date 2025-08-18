package com.example.dev.dto;

import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

public record SearchRequestParams(String query, String subject, String level, Double rating,
                                  List<AvailabilityRange> availabilities, Integer page, @DefaultValue("10") Integer size) {
    public record AvailabilityRange(String day, String startTime, String endTime) {
    }

}
