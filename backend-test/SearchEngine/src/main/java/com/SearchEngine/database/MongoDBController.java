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
    void updateScore(@RequestBody Map<String, Double> body){
        this.mongoDBService.updateScore(body.get("Popularity percentage"), body.get("Relevance percentage"));
    }

    @PutMapping("/update-popularity")
    void updatePopularity(@RequestBody Map<String, String> body){
        this.mongoDBService.updatePopularity(body.get("url"), Integer.parseInt(body.get("popularity")));
    }

    @PutMapping("/update-relevance")
    void updateRelevance(@RequestBody Map<String, String> body) {
        this.mongoDBService.updateRelevance(body.get("root"), body.get("original"), body.get("url"), Integer.parseInt(body.get("relevance")));
    }

    @GetMapping("/search-by-exact-word")
    List<MongoDbEntity> searchByExactWord(){
        return this.mongoDBService.searchByExactWord("play", "playing", 2);
    }

    @GetMapping("/search-by-root-word")
    List<MongoDbEntity> searchByRootWord(){
        return this.mongoDBService.searchByRootWord("game",  10);
    }


}
