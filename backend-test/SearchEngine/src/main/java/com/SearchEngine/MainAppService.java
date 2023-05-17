package com.SearchEngine;


import com.SearchEngine.database.MongoDBService;
import com.SearchEngine.database.MongoDbEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.math3.linear.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


@Service
public class MainAppService {

    @Autowired
    StemmerService stemmerService;

    @Autowired
    MongoDBService mongoDBService;

    @Autowired
    UtilityService utilityService;



    List<MongoDbEntity> search(String searchedWord, int pageNumber) {
        pageNumber--;
        boolean isExactSearch = searchedWord.charAt(0) == '"';
        List<MongoDbEntity> exactSearch =  this.searchExact(searchedWord);
        // Check start index
        return exactSearch.subList(pageNumber*10, Math.min(pageNumber*10+10, exactSearch.size()));

    }

    List<MongoDbEntity> searchExact(String searchedWord){
        List<String> processedWords=this.utilityService.removeStopWords(searchedWord);
        String processedString = String.join(" ", processedWords);
        List<MongoDbEntity> sites = this.mongoDBService.searchByExactWord(stemmerService.stem(processedWords.get(0)),processedWords.get(0),200);
        List<MongoDbEntity> matchedResult = new ArrayList<>();

        // Check the relevance of other words
        for(MongoDbEntity site: sites){
            for(String paragraph: site.getDetails().getParagraphs()){
                List<String> paragraphWord = this.utilityService.removeStopWords(paragraph);
                String paragraphString = String.join(" ", paragraphWord);
                if(paragraphString.contains(processedString)){
                    site.getDetails().setParagraphs(List.of(paragraph));
                    matchedResult.add(site);
                    break;
                }
            }
        }
        return matchedResult;
    }


}
