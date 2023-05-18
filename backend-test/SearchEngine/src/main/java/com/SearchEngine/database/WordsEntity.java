package com.SearchEngine.database;

import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.List;

@Setter
@Getter
public class WordsEntity {
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
        private double relevance;              // The relevance of the page
        private double popularity;             // The popularity of the page
        private double termFrequency;          // The term frequency of the word in the page (normalized)
        private double score;                  // The score of the page (a linear combination of popularity and relevance)
        private String title;               // The title of the page
        private String content;
        // Getters and setters
    }

    public static void sortEntitiesByScore(List<WordsEntity> entities) {
        // Sort the list using a custom comparator that compares the scores of the entities
        entities.sort(new Comparator<WordsEntity>() {
            public int compare(WordsEntity e1, WordsEntity e2) {
                Double score1 = e1.getDetails().getScore();
                Double score2 = e2.getDetails().getScore();
                return score2.compareTo(score1); // sort in descending order
            }
        });
    }
}
