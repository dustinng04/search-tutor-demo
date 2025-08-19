package com.example.dev.util;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.TermsAggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.*;

import java.util.List;
import java.util.function.UnaryOperator;
import java.util.ArrayList;
import com.example.dev.constants.Constants.Fuzzy;
import com.example.dev.dto.SearchRequestParams.AvailabilityRange;

public class ElasticSearchUtil {
    public static Query buildTermQuery(String field, String value, float boost) {
        var termQuery = TermQuery.of(builder -> builder
                .field(field)
                .boost(boost)
                .value(value)
                .caseInsensitive(true)
        );

        return Query.of(builder -> builder.term(termQuery));
    }

    public static Query buildRangeQuery(String field, UnaryOperator<NumberRangeQuery.Builder> function) {
        var numberRangeQuery = NumberRangeQuery.of(builder -> function.apply(builder.field(field)));

        var rangeQuery = RangeQuery.of(builder -> builder.number(numberRangeQuery));
        return Query.of(builder -> builder.range(rangeQuery));
    }

    public static Query buildDateRangeQuery(String field, UnaryOperator<DateRangeQuery.Builder> function) {
        var dateRangeQuery = DateRangeQuery.of(builder -> function.apply(builder.field(field)));

        var rangeQuery = RangeQuery.of(builder -> builder.date(dateRangeQuery));
        return Query.of(builder -> builder.range(rangeQuery));
    }

    public static Query buildMultimatchQuery(List<String> fields, String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return Query.of(builder -> builder.matchAll(MatchAllQuery.of(b -> b)));
        }

        var multiMatchQuery = MultiMatchQuery.of(builder -> builder
                .query(searchTerm)
                .fields(fields)
                .fuzziness(Fuzzy.LEVEL)
                .prefixLength(Fuzzy.PREFIX_LENGTH)
                .type(TextQueryType.MostFields)
                .operator(Operator.And)
        );

        return Query.of(builder -> builder.multiMatch(multiMatchQuery));
    }

    public static Query buildContainsQuery(List<String> fields, String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return Query.of(builder -> builder.matchAll(MatchAllQuery.of(b -> b)));
        }

        // Create wildcard queries for each field to support "contains" functionality
        List<Query> fieldQueries = new ArrayList<>();
        String wildcardTerm = "*" + searchTerm.toLowerCase() + "*";
        
        for (String fieldWithBoost : fields) {
            String field;
            float boost;

            // Parse field boost if present (e.g., "name^3.0")
            if (fieldWithBoost.contains("^")) {
                String[] parts = fieldWithBoost.split("\\^");
                field = parts[0];
                boost = Float.parseFloat(parts[1]);
            } else {
                boost = 1.0f;
                field = fieldWithBoost;
            }
            
            var wildcardQuery = WildcardQuery.of(builder -> builder
                    .field(field)
                    .value(wildcardTerm)
                    .boost(boost)
                    .caseInsensitive(true)
            );
            
            fieldQueries.add(Query.of(builder -> builder.wildcard(wildcardQuery)));
        }
        
        // Combine all field queries with OR (should match any field)
        if (fieldQueries.size() == 1) {
            return fieldQueries.get(0);
        } else {
            var boolQuery = BoolQuery.of(builder -> builder.should(fieldQueries));
            return Query.of(builder -> builder.bool(boolQuery));
        }
    }

    public static Query buildAvailabilityQuery(String availabilityPath, String dayField, 
                                              String startTimeField, String endTimeField, 
                                              List<AvailabilityRange> availabilities) {
        List<Query> availabilityQueries = new ArrayList<>();
        if (availabilities == null) {
            // Return a MatchAllQuery if the list is null or empty
            return Query.of(builder -> builder.matchAll(MatchAllQuery.of(b -> b)));
        }
        for (AvailabilityRange availability : availabilities) {
            List<Query> conditions = new ArrayList<>();

            // Add day condition if provided
            if (availability.day() != null && !availability.day().trim().isEmpty()) {
                conditions.add(buildTermQuery(dayField, availability.day(), 1.0f));
            }
            
            // Add time range conditions if provided
            // For time overlap: tutor.start_time <= user.endTime AND tutor.end_time >= user.startTime
            if (availability.startTime() != null && !availability.startTime().trim().isEmpty()) {
                // Tutor's end_time must be >= user's startTime
                var endTimeQuery = buildDateRangeQuery(endTimeField, 
                    builder -> builder.gte(availability.startTime()));
                conditions.add(endTimeQuery);
            }
            
            if (availability.endTime() != null && !availability.endTime().trim().isEmpty()) {
                // Tutor's start_time must be <= user's endTime
                var startTimeQuery = buildDateRangeQuery(startTimeField, 
                    builder -> builder.lte(availability.endTime()));
                conditions.add(startTimeQuery);
            }
            
            // If any conditions exist for this availability, combine them with AND
            if (!conditions.isEmpty()) {
                if (conditions.size() == 1) {
                    // Wrap single condition in nested query for availability object
                    var nestedQuery = NestedQuery.of(builder -> builder
                        .path(availabilityPath)
                        .query(conditions.get(0))
                    );
                    availabilityQueries.add(Query.of(builder -> builder.nested(nestedQuery)));
                } else {
                    // Multiple conditions for this availability - combine with AND
                    var boolQuery = BoolQuery.of(builder -> builder.must(conditions));
                    var nestedQuery = NestedQuery.of(builder -> builder
                        .path(availabilityPath)
                        .query(Query.of(q -> q.bool(boolQuery)))
                    );
                    availabilityQueries.add(Query.of(builder -> builder.nested(nestedQuery)));
                }
            }
        }
        
        // Combine all availability queries with OR (should match any one of them)
        if (availabilityQueries.isEmpty()) {
            return Query.of(builder -> builder.matchAll(MatchAllQuery.of(b -> b)));
        } else if (availabilityQueries.size() == 1) {
            return availabilityQueries.get(0);
        } else {
            var boolQuery = BoolQuery.of(builder -> builder.should(availabilityQueries));
            return Query.of(builder -> builder.bool(boolQuery));
        }
    }

    public static Aggregation buildTermsAggregation(String field){
        var termsAggregation = TermsAggregation.of(builder -> builder.field(field).size(10));
        return Aggregation.of(builder -> builder.terms(termsAggregation));
    }
}
