package com.example.dev.util;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.example.dev.dto.SearchRequestParams;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import static com.example.dev.util.QueryRules.*;
import java.util.List;
import java.util.Optional;

public class NativeQueryBuilder {
    public static final List<QueryRule> FILTER_QUERY_RULES = List.of(
        SUBJECT_QUERY,
        LEVEL_QUERY,
        AVAILABILITY_QUERY
    );

    public static final List<QueryRule> MUST_QUERY_RULES = List.of();
    public static final List<QueryRule> SHOULD_QUERY_RULES = List.of(
            RATING_QUERY,      // Higher rating = better score
            SEARCH_QUERY       // Text search match = better score
    );

    public static NativeQuery toSearchQuery(SearchRequestParams params) {
        var filterQueries = buildQueries(FILTER_QUERY_RULES, params);
        var mustQueries = buildQueries(MUST_QUERY_RULES, params);
        var shouldQueries = buildQueries(SHOULD_QUERY_RULES, params);
        var boolQuery = BoolQuery.of(builder -> builder.filter(filterQueries)
                .must(mustQueries)
                .should(shouldQueries)
        );

        return NativeQuery.builder()
                .withQuery(Query.of(builder -> builder.bool(boolQuery)))
                .withPageable(PageRequest.of(params.page(), params.size()))
                .withTrackTotalHits(true)
                .build();
    }

    public static List<Query> buildQueries(List<QueryRule> queryRules, SearchRequestParams params) {
        return queryRules.stream()
                .map(qr -> qr.build(params))
                .flatMap(Optional::stream)
                .toList();
    }
}
