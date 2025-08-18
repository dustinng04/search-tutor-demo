package com.example.dev.util;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.example.dev.dto.SearchRequestParams;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public record QueryRule(Predicate<SearchRequestParams> predicate, Function<SearchRequestParams, Query> function) {

    public static QueryRule of(Predicate<SearchRequestParams> predicate, Function<SearchRequestParams, Query> function) {
        return new QueryRule(predicate, function);
    }

    public Optional<Query> build(SearchRequestParams params) {
        return Optional.of(params)
                .filter(this.predicate())
                .map(this.function());
    }
}
