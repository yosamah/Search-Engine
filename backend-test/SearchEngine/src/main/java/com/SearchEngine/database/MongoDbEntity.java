package com.SearchEngine.database;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class MongoDbEntity {
    private String _id;
    private String root;
    private Details details;

    // Getters and setters

    @Getter
    @Setter
    public static class Details {
        private String original;
        private String url;
        private List<String> paragraphs;
        private int relevance;
        private int popularity;
        private int termFrequency;
        private int score;
        private String title;

        // Getters and setters
    }
}
