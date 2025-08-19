package com.example.dev.util;

import org.springframework.data.util.Predicates;

import java.util.List;
import java.util.Objects;
import static com.example.dev.constants.Constants.Tutor.*;
public class QueryRules {
    public static final String BOOST_FIELD_FORMAT = "%s^%f";

    public static final QueryRule SUBJECT_QUERY = QueryRule.of(
            srp -> Objects.nonNull(srp.subject()) && !srp.subject().trim().isEmpty(),
            srp -> ElasticSearchUtil.buildTermQuery(SUBJECT, srp.subject(), 1.0f)
    );

    public static final QueryRule LEVEL_QUERY = QueryRule.of(
//            srp -> Objects.nonNull(srp.level()),
            srp -> Objects.nonNull(srp.level()) && !srp.level().trim().isEmpty(),
            srp -> ElasticSearchUtil.buildTermQuery(LEVEL, srp.level(), 1.0f)
    );

    public static final QueryRule RATING_QUERY = QueryRule.of(
            srp -> Objects.nonNull(srp.rating()),
            srp -> ElasticSearchUtil.buildRangeQuery(RATING, builder -> builder.gte(srp.rating()))
    );

    public static final QueryRule AVAILABILITY_QUERY = QueryRule.of(
//            srp -> Objects.nonNull(srp.availabilities()) && !srp.availabilities().isEmpty(),
            Predicates.isTrue(),
            srp -> ElasticSearchUtil.buildAvailabilityQuery(AVAILABILITY, AVAILABILITY_DAY,
                    AVAILABILITY_START, AVAILABILITY_END, srp.availabilities())
    );

    public static final List<String> SEARCH_BOOST_FIELDS = List.of(
            boostField(NAME, 3.0f),        // Highest boost - tutor name is most important
            boostField(DESCRIPTION, 2.5f), // High boost - description contains detailed info
            boostField(SUBJECT, 1.5f),     // Medium boost - subject is important but categorical
            boostField(LEVEL, 1.0f)        // Lower boost - level is more of a filter than search target
    );

    public static final QueryRule SEARCH_QUERY = QueryRule.of(
            srp -> Objects.nonNull(srp.query()) && !srp.query().trim().isEmpty(),
            srp -> ElasticSearchUtil.buildMultimatchQuery(List.of(NAME, DESCRIPTION), srp.query())
    );

    public static String boostField(String field, float boost) {
        return BOOST_FIELD_FORMAT.formatted(field, boost);
    }
}
