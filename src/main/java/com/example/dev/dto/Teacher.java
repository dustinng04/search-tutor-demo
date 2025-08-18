package com.example.dev.dto;

import java.util.List;

public record Teacher(String id, String name, String description, String subject, String level, double rating, List<Availability> availabilities)
{

}
