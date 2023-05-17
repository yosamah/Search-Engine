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
        private String original;            // The original word without stemming
        private String url;                 // The url of the page
        private List<String> paragraphs;    // The paragraphs in which the word appeared
        private int relevance;              // The relevance of the page
        private int popularity;             // The popularity of the page
        private int termFrequency;          // The term frequency of the word in the page (normalized)
        private int score;                  // The score of the page (a linear combination of popularity and relevance)
        private String title;               // The title of the page

        // Getters and setters
    }
}
