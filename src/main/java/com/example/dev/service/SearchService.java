package com.example.dev.service;

import com.example.dev.constants.Constants;
import com.example.dev.dto.Pagination;
import com.example.dev.dto.SearchRequestParams;
import com.example.dev.dto.SearchResponse;
import com.example.dev.dto.Teacher;
import com.example.dev.util.NativeQueryBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {
    private final ElasticsearchOperations elasticsearchOperations;

    public SearchResponse search(SearchRequestParams params) {
        log.info("Search request received with params: {}", params);
        var query = NativeQueryBuilder.toSearchQuery(params);
        log.info("bool query: {}", query.getQuery());

        var searchHits = elasticsearchOperations.search(query, Teacher.class, Constants.Index.TUTORS);
        return buildResponse(params, searchHits);
    }

    private SearchResponse buildResponse(SearchRequestParams params, SearchHits<Teacher> searchHits) {
        var result = searchHits.getSearchHits()
                .stream()
                .map(SearchHit::getContent)
                .toList();
        var searchPage = SearchHitSupport.searchPageFor(searchHits, PageRequest.of(params.page(), params.size()));
        var pagination = new Pagination(
                searchPage.getNumber(),
                searchPage.getNumberOfElements(),
                searchPage.getTotalElements(),
                searchPage.getTotalPages()
        );

        return new SearchResponse(
                result,
                pagination,
                searchHits.getExecutionDuration().toMillis()
        );
    }


}
