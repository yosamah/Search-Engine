package com.SearchEngine.database;

import com.SearchEngine.UtilityService;
import com.mongodb.client.MongoCollection;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MongoDBService {

    private final MongoTemplate mongoTemplate;
    MongoCollection<Document> wordsCollection;
    List<Document> wordsDocuments;

    @Autowired
    UtilityService utilityService;

    @Autowired
    public MongoDBService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
//        this.mongoTemplateWebsite = mongoTemplate;
        this.wordsCollection = mongoTemplate.getCollection("words");
        this.wordsDocuments = wordsCollection.find().into(new ArrayList<>());
    }

    public void updateScore(Double popularityPercentage, Double relevancePercentage) {
        // Access the database and collection
        for (Document document : wordsDocuments) {
            List<Document> details = (List<Document>) document.get("details");
            List<Document> updatedDetails = new ArrayList<>();

            for (Document detail : details) {
                int relevance = detail.getInteger("relevance");
                int popularity = detail.getInteger("popularity");
                double score = (relevance * popularityPercentage + popularity * relevancePercentage);

                Document updatedDetail = new Document(detail)
                        .append("score", (int) score);

                updatedDetails.add(updatedDetail);
            }

            document.put("details", updatedDetails);
            wordsCollection.replaceOne(new Document("_id", document.get("_id")), document);
        }
    }

    public void updateRelevance() {
        // Access the database and collection
        for (Document document : wordsDocuments) {
            List<Document> details = (List<Document>) document.get("details");
            List<Document> updatedDetails = new ArrayList<>();

            for (Document detail : details) {
                double idf = detail.getDouble("IDF");
                double termFrequency = detail.getDouble("termFrequency");
                double relevance = idf*termFrequency;

                Document updatedDetail = new Document(detail)
                        .append("relevance",  relevance);

                updatedDetails.add(updatedDetail);
            }

            document.put("details", updatedDetails);
            wordsCollection.replaceOne(new Document("_id", document.get("_id")), document);
        }
    }
    public void updatePopularity(String url, Double popularity) {
        // Build the query to match documents with the provided URL
        Query query = new Query();
        query.addCriteria(Criteria.where("details.url").is(url));

        // Build the update to set the popularity field in the matched elements of the details array
        Update update = new Update().set("details.$[elem].popularity", popularity);
        update.filterArray(Criteria.where("elem.url").is(url));

        // Execute the update query
        mongoTemplate.updateMulti(query, update, "words");
    }

    public void updateWebsitePopularity(String url, Double newPopularity) {
        // Build the query to match documents with the provided URL
        Query query = new Query(Criteria.where("url").is(url));
        Update update = new Update().set("popularity", newPopularity);
        mongoTemplate.updateFirst(query, update, WebsiteEntity.class, "websites");

    }

    public void updateRelevance(String root, String original, String url, int relevance) {
        // Create a query to match the documents
        Query query = new Query();
        query.addCriteria(Criteria.where("root").is(root));

        // Create an update operation to set the relevance field
        Update update = new Update();
        update.set("details.$[elem].relevance", relevance);
        update.filterArray(
                Criteria.where("elem.original").is(original)
                        .and("elem.url").is(url)
        );

        // Execute the update operation
        mongoTemplate.updateMulti(query, update, "words");
    }

    public List<MongoDbEntity> searchByExactWord(String rootWord, String originalWord, int limitElements) {
        // Match stage to filter documents by root field
        MatchOperation matchRoot = Aggregation.match(Criteria.where("root").is(rootWord));

        // Unwind stage to deconstruct the details array
        UnwindOperation unwind = Aggregation.unwind("details");

        // Match stage to filter documents by details.original field
        MatchOperation matchOriginal = Aggregation.match(Criteria.where("details.original").is(originalWord));

        // Sort stage to sort by details.relevance field in descending order
        SortOperation sort = Aggregation.sort(Sort.Direction.DESC, "details.score");

        // Limit returned number of documents
        Aggregation aggregation = Aggregation.newAggregation(matchRoot, unwind, matchOriginal, sort, Aggregation.limit(limitElements));

        // Execute the aggregation query
        AggregationResults<MongoDbEntity> searchResults = mongoTemplate.aggregate(aggregation, "words", MongoDbEntity.class);
        return searchResults.getMappedResults();
    }

    public List<MongoDbEntity> searchByRootWord(String rootWord, int limitElements) {

        // Match stage to filter documents by root field
        MatchOperation matchRoot = Aggregation.match(Criteria.where("root").is(rootWord));

        // Unwind stage to deconstruct the details array
        UnwindOperation unwind = Aggregation.unwind("details");

        // Sort stage to sort by details.relevance field in descending order
        SortOperation sort = Aggregation.sort(Sort.Direction.DESC, "details.score");

        // Limit stage to limit the result to 3 documents
        Aggregation aggregation = Aggregation.newAggregation(matchRoot, unwind, sort, Aggregation.limit(limitElements));

        // Execute the aggregation query and get the result as Document
        AggregationResults<MongoDbEntity> searchResults = mongoTemplate.aggregate(aggregation, "words", MongoDbEntity.class);
        return searchResults.getMappedResults();
    }

    public List<WebsiteEntity> getAllWebsites() {
        return mongoTemplate.findAll(WebsiteEntity.class, "websites");
    }

    public List<MongoDbEntity> getAllWords() {
        return mongoTemplate.findAll(MongoDbEntity.class, "words");
    }


    public boolean computeAllSitesPopularity() {
        try {
            List<WebsiteEntity> allWebsites = this.getAllWebsites();
            int numberOfWebsites = allWebsites.size();
            Map<Integer, String> mappingLocalIdsToWebsites = new HashMap<>();
            Map<String, Integer> mappingWebsitesToLocalIds = new HashMap<>();
            int currentId = 0;
            for (WebsiteEntity website : allWebsites) {
                mappingLocalIdsToWebsites.put(currentId, website.getUrl());
                mappingWebsitesToLocalIds.put(website.getUrl(), currentId);
                currentId++;
            }

            currentId = 0;
            double[][] transitionMatrix = new double[numberOfWebsites][numberOfWebsites];

            for (WebsiteEntity website : allWebsites) {
                for (String url : website.getOutgoing_links()) {
                    transitionMatrix[mappingWebsitesToLocalIds.get(url)][currentId] = (1.0 / website.getOutgoing_links().size());
                }
                currentId++;
            }
            double[] popularityInitialization = new double[numberOfWebsites];
            for (int i = 0; i < numberOfWebsites; ++i)
                popularityInitialization[i] = (1.0 / numberOfWebsites);

            double[] computedPopularity = this.utilityService.computePopularity(transitionMatrix, popularityInitialization);
            for (int i = 0; i < numberOfWebsites; ++i) {
                this.updateWebsitePopularity(mappingLocalIdsToWebsites.get(i), computedPopularity[i]);
            }

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public List<MongoDbEntity> getAllWordsUnWindDetails() {
        AggregationOperation unwind = Aggregation.unwind("details");
        Aggregation aggregation = Aggregation.newAggregation(unwind);

        AggregationResults<MongoDbEntity> results = mongoTemplate.aggregate(aggregation, "words", MongoDbEntity.class);

        return results.getMappedResults();
    }

    public List<WordsCountEntity> getAllCountOFWords() {
        AggregationOperation unwind = Aggregation.unwind("details");
        AggregationOperation group = Aggregation.group("details.original").count().as("count");
        AggregationOperation project = Aggregation.project("count").and("_id").as("original");
        Aggregation aggregation = Aggregation.newAggregation(unwind, group, project);

// Execute the aggregation query
        AggregationResults<WordsCountEntity> results = mongoTemplate.aggregate(aggregation, "words", WordsCountEntity.class);

// Get the list of matched words and their counts
        return results.getMappedResults();
    }


    public Integer getWebsitesCount() {
        return Math.toIntExact(mongoTemplate.count(new Query(), "websites"));
    }

    public void setIdfForOriginalWord(String originalWord, double idf) {

        Query query = new Query();

        // Create an update operation to set the relevance field
        Update update = new Update();
        update.set("details.$[elem].IDF", idf);
        update.filterArray(
                Criteria.where("elem.original").is(originalWord)

        );

        // Execute the update operation
        mongoTemplate.updateMulti(query, update, "words");

    }

    public boolean computeAllWordsRelevance() {
        try {
            List<WordsCountEntity> words = this.getAllCountOFWords();
            Integer numberOfWebsites = this.getWebsitesCount();

            for(WordsCountEntity word: words){
                this.setIdfForOriginalWord(word.getOriginal(), (Math.log((double)numberOfWebsites/word.getCount()) / Math.log(2)));
            }
            this.updateRelevance();

        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
