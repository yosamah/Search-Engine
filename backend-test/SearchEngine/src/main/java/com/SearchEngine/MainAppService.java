package com.SearchEngine;


import com.SearchEngine.database.MongoDBService;
import com.SearchEngine.database.MongoDbEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class MainAppService {

    @Autowired
    StemmerService stemmerService;

    @Autowired
    MongoDBService mongoDBService;

    @Autowired
    UtilityService utilityService;



    List<MongoDbEntity> search(String searchedWord, int pageNumber) {

        
        // --Remove special charachters
        searchedWord = searchedWord.replaceAll("-", " ");
        searchedWord = searchedWord.replaceAll("[-®#%~!@#$%^&*()_+/*?<>':;–.,`’\"\\[\\]]+", " ");
        // Replace Single occurring characters
        searchedWord = searchedWord.replaceAll(" [a-zA-Z0-9] ", " ");
        // --Replace 2 or more white spaces with a single white space
        searchedWord = searchedWord.replaceAll("\\s{2,}", " ");

        pageNumber--;       // for 0-indexing
        boolean isExactSearch = searchedWord.charAt(0) == '"';      // exact search (phrasal) must start with " 
        List<MongoDbEntity> exactSearch =  this.searchExact(searchedWord);      // returns a list of sites that contain the exact phrase
        // Check start index
        // 8albn start: Math.min(pageNumber*10, exactSearch.size() - 10)
        // 3lshan lw edany page akbr mn el total bta3na, azherlo 2a5er 10 results bs
        // y3ny if pageNumber* 10 > exactSearch.size(). kda hageblo 2a5er 10 results 
        return exactSearch.subList(pageNumber*10, Math.min(pageNumber*10+10, exactSearch.size()));
        // 3ayzeen nt2aked enena msh bn3ml el 7esba dyh kol mara, w bngeeb el search results mara wa7da bs actually

    }



    List<MongoDbEntity> searchExact(String searchedWord){

        // searchedWord dh elly el user katabo
        List<String> processedWords=this.utilityService.removeStopWords(searchedWord);  // remove stop words and return list of words
        String processedString = String.join(" ", processedWords);                      // return string mn el list of words
        List<MongoDbEntity> sites = this.mongoDBService.searchByExactWord(stemmerService.stem(processedWords.get(0)),processedWords.get(0),200);
        // searching for the first words, and getting 200 results (number might be modified later)
        // byb2o sorted descendingly by score
        List<MongoDbEntity> matchedResult = new ArrayList<>();

        List<String> stemmedWords = new ArrayList<>();
        for (String processedWord : processedWords) stemmedWords.add(stemmerService.stem(processedWord));

        // TODO: Check the relevance of other words (na5do m3ana fl e3tebar y3ny)
        for(MongoDbEntity site: sites){
            // loop 3l 200 site elly rg3o
            for(String paragraph: site.getDetails().getParagraphs()){
                // loop 3l paragraphs bta3t el site, shoof lw fyh el phrase elly el user 3ayezha
                List<String> paragraphWord = this.utilityService.removeStopWords(paragraph);        // remove stop words from paragraph
                String paragraphString = String.join(" ", paragraphWord);       // paragraph size is around 20 words or less, so no high complexity
                if(paragraphString.contains(processedString)){      // if the paragraph contains the phrase
                    site.getDetails().setParagraphs(List.of(paragraph));
                    matchedResult.add(site);
                    Double newRelevance = site.getDetails().getScore();
                    for(int j = 1; j < processedWords.size(); ++j)
                        newRelevance+=this.mongoDBService.getScore(stemmedWords.get(j), processedWords.get(j), site.getDetails().getUrl());
                    site.getDetails().setScore(newRelevance);
                    break;
                }
            }
        }
        MongoDbEntity.sortEntitiesByScore(matchedResult);
        return matchedResult;
    }




}
