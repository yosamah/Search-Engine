package com.SearchEngine;

import org.springframework.stereotype.Service;
import org.tartarus.snowball.ext.EnglishStemmer;

@Service
public class StemmerService {

    private EnglishStemmer stemmer;

    public StemmerService() {
        stemmer = new EnglishStemmer();
    }

    public String stem(String[] words) {
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            stemmer.setCurrent(word);
            stemmer.stem();
            result.append(stemmer.getCurrent()).append(" ");
        }

        result = new StringBuilder(result.substring(0, result.length() - 1));
        return result.toString();
    }

    public String stem(String word) {
        // getting the root of the word
        stemmer.setCurrent(word);
        stemmer.stem();
        return stemmer.getCurrent();
    }

    public String[] stemSentence(String sentence) {
        String[] words = sentence.split(" ");

        for (int i = 0; i < words.length; i++) {
            stemmer.setCurrent(words[i]);
            stemmer.stem();
            words[i] = stemmer.getCurrent();
        }

        return words;
    }
}