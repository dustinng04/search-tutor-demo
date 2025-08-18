package com.example.dev.constants;

import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;

@NoArgsConstructor
public class Constants {
    public static class Index {
        public static final IndexCoordinates SUGGESTION = IndexCoordinates.of("suggestions");
        public static final IndexCoordinates TUTORS = IndexCoordinates.of("teachers");
    }

    public static class Suggestion {
        public static final String SEARCH_TERM = "search_term";
        public static final String SUGGEST_NAME = "search_term-suggest";
    }

    public static class Fuzzy {
        public static final String LEVEL = "1";
        public static final Integer PREFIX_LENGTH = 2;
    }

    public static class Tutor {
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String SUBJECT = "subject";
        public static final String LEVEL = "level";
        public static final String DESCRIPTION = "description";
        public static final String RATING = "rating";
        public static final String AVAILABILITY = "availability";
        public static final String AVAILABILITY_DAY = "availability.day";
        public static final String AVAILABILITY_START = "availability.start_time";
        public static final String AVAILABILITY_END = "availability.end_time";
    }
}
