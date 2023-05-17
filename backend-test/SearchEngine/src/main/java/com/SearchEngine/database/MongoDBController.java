package com.SearchEngine.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/database")
public class MongoDBController {

    @Autowired
    MongoDBService mongoDBService;

    @PutMapping("/update-score")
    void updateScore(@RequestBody Map<String, Double> body) {
        this.mongoDBService.updateScore(body.get("Popularity percentage"), body.get("Relevance percentage"));
    }

    @PutMapping("/update-popularity")
    void updatePopularity(@RequestBody Map<String, String> body) {
        this.mongoDBService.updatePopularity(body.get("url"), Double.parseDouble(body.get("popularity")));
    }
    @PutMapping("/update-idf")
    void updateIDF(@RequestBody Map<String, String> body) {
        this.mongoDBService.setIdfForOriginalWord(body.get("original"), Double.parseDouble(body.get("idf")));
    }

    @PutMapping("/update-relevance")
    boolean updateRelevance() {
        return this.mongoDBService.computeAllWordsRelevance();
    }

    @GetMapping("/search-by-exact-word")
    List<WordsEntity> searchByExactWord() {
        return this.mongoDBService.searchByExactWord("play", "playing", 2);
    }

    @GetMapping("/search-by-root-word")
    List<WordsEntity> searchByRootWord() {
        return this.mongoDBService.searchByRootWord("game", 10);
    }

    @PutMapping("/update-website-popularity")
    void updateWebsitePopularity(@RequestBody Map<String, String> body) {
        this.mongoDBService.updateWebsitePopularity(body.get("url"), Double.parseDouble(body.get("popularity")));
    }

    @PutMapping("/set-websites-popularity")
    boolean setWebsitesPopularity() {
        return this.mongoDBService.computeAllSitesPopularity();
    }

    @GetMapping("/get-all-websites")
    List<WebsiteEntity> getAllWebsites() {
        return this.mongoDBService.getAllWebsites();
    }

    @GetMapping("/get-all-words")
    List<WordsEntity> getAllWords() {
        return this.mongoDBService.getAllWordsUnWindDetails();
    }
    @GetMapping("/get-count-all-words")
    List<WordsCountEntity> getAllCountOfAllWords() {
        return this.mongoDBService.getAllCountOFWords();
    }
}
