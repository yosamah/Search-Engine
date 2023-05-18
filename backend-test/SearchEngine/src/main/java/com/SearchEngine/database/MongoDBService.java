package com.SearchEngine.database;

import com.SearchEngine.UtilityService;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
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
import java.util.stream.Collectors;

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
        // updates the score of all words and urls in the database
        for (Document document : wordsDocuments) {
            List<Document> details = (List<Document>) document.get("details");
            List<Document> updatedDetails = new ArrayList<>();

            for (Document detail : details) {
                int relevance = detail.getInteger("relevance");
                int popularity = detail.getInteger("popularity");
                double score = (relevance * popularityPercentage + popularity * relevancePercentage);
                // score is a linear combination of popularity and relevance

                Document updatedDetail = new Document(detail)
                        .append("score", (int) score);

                updatedDetails.add(updatedDetail);
            }

            document.put("details", updatedDetails);
            wordsCollection.replaceOne(new Document("_id", document.get("_id")), document);
        }
    }

    public void updateRelevance() {
        // updates the relevance of all words and urls in the database
        for (Document document : wordsDocuments) {
            List<Document> details = (List<Document>) document.get("details");
            List<Document> updatedDetails = new ArrayList<>();

            for (Document detail : details) {
                double idf = detail.getDouble("IDF");
                double termFrequency = detail.getDouble("termFrequency");
                double relevance = idf * termFrequency;
                // relevance is the product of IDF and term frequency

                Document updatedDetail = new Document(detail)
                        .append("relevance", relevance);

                updatedDetails.add(updatedDetail);
            }

            document.put("details", updatedDetails);
            wordsCollection.replaceOne(new Document("_id", document.get("_id")), document);
        }
    }

    public void updatePopularity(String url, Double popularity) {
        // update the popularity of the given URL (in all words)
        Query query = new Query();
        // match documents with the provided URL
        query.addCriteria(Criteria.where("details.url").is(url));

        // Build the update to set the popularity field in the matched elements of the details array
        Update update = new Update().set("details.$[elem].popularity", popularity);
        update.filterArray(Criteria.where("elem.url").is(url));

        // Execute the update query
        mongoTemplate.updateMulti(query, update, "words");
    }

    public void updateWebsitePopularity(String url, Double newPopularity) {
        // updates the popularity of a certain website in the websites collection
        Query query = new Query(Criteria.where("url").is(url));
        Update update = new Update().set("popularity", newPopularity);
        mongoTemplate.updateFirst(query, update, WebsiteEntity.class, "pages");

    }

    public void updateRelevance(String root, String original, String url, int relevance) {
        // updates relevance of a certain word and url in the database (no longer needed)
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

    public List<WordsEntity> searchByExactWord(String rootWord, String originalWord, int limitElements) {
        // search for an exact (original) word in the database and returns 
        // its links sorted descendingly according to their score

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
        AggregationResults<WordsEntity> searchResults = mongoTemplate.aggregate(aggregation, "words", WordsEntity.class);
        return searchResults.getMappedResults();
    }

    public List<WordsEntity> searchByRootWord(String rootWord, int limitElements) {
        // search for a word by its root in the database and returns 
        // its links sorted descendingly according to their score

        // Match stage to filter documents by root field
        MatchOperation matchRoot = Aggregation.match(Criteria.where("root").is(rootWord));

        // Unwind stage to deconstruct the details array
        UnwindOperation unwind = Aggregation.unwind("details");

        // Sort stage to sort by details.relevance field in descending order
        SortOperation sort = Aggregation.sort(Sort.Direction.DESC, "details.score");

        // Limit stage to limit the result to 3 documents
        Aggregation aggregation = Aggregation.newAggregation(matchRoot, unwind, sort, Aggregation.limit(limitElements));

        // Execute the aggregation query and get the result as Document
        AggregationResults<WordsEntity> searchResults = mongoTemplate.aggregate(aggregation, "words", WordsEntity.class);
        return searchResults.getMappedResults();
    }

    public List<WebsiteEntity> getAllWebsites() {
        // returns the Websites collection
        return mongoTemplate.findAll(WebsiteEntity.class, "pages");
    }

    public List<WordsEntity> getAllWords() {
        // returns the Words collection
        return mongoTemplate.findAll(WordsEntity.class, "words");
    }


    public boolean computeAllSitesPopularity() {
        // computes the popularity of all websites in the database
        int currentId = 0;
        try {
            List<WebsiteEntity> allWebsites = this.getAllWebsites();        // getting all websites
            int numberOfWebsites = allWebsites.size();                      // number of websites
            Map<Integer, String> mappingLocalIdsToWebsites = new HashMap<>();       // mapping local ids to websites
            // we need this map so as to know which site corresponds to which index in the transition matrix
            // this will be used when writing to the DB
            Map<String, Integer> mappingWebsitesToLocalIds = new HashMap<>();       // mapping websites to local ids
            // we need this map so as to maintain the order of websites in the transition matrix
            // this will be used when computing the popularity

                  // 0 indexing
            for (WebsiteEntity website : allWebsites) {
                // buliding the hashmaps
                mappingLocalIdsToWebsites.put(currentId, website.getUrl());
                mappingWebsitesToLocalIds.put(website.getUrl(), currentId);
                currentId++;
            }

            currentId = 0;      // reseting the current id
            double[][] transitionMatrix = new double[numberOfWebsites][numberOfWebsites];
            // transition matrix is a square matrix of size (number of websites) x (number of websites)
            // number of websites should be 6000

            // each column represents a website
            // and each row represents a link from a the column website to the row website
            // e.g, having three websites A, B, C
            // and A has a link to B and C
            // and B has a link to C
            // and C has a link to A
            // then the transition matrix will be
            //    A    B   C
            // A  0    0   1
            // B  0.5  0   0
            // C  0.5  1   0

            for (WebsiteEntity website : allWebsites) {
                for (String url : website.getOutgoingLinks()) {
                    // filling the column of the current website
                    if(mappingWebsitesToLocalIds.containsKey(url))
                        transitionMatrix[mappingWebsitesToLocalIds.get(url)][currentId] = (1.0 / website.getOutgoingLinks().size());

                    // website has a connection to the url, so fill the column of the website with
                    // 1 / number of outgoing links in each url row

                }
                currentId++;        // increase the id counter to get the next website
            }

            double[] popularityInitialization = new double[numberOfWebsites];
            // initially, set the popularity of each website to 1 / total number of websites
            // should be 1 / 6000
            for (int i = 0; i < numberOfWebsites; ++i)
                popularityInitialization[i] = (1.0 / numberOfWebsites);

            // compute the popularity of each website
            double[] computedPopularity = this.utilityService.computePopularity(transitionMatrix, popularityInitialization);

            // update the popularity of each website in the database
            for (int i = 0; i < numberOfWebsites; ++i) {
                this.updateWebsitePopularity(mappingLocalIdsToWebsites.get(i), computedPopularity[i]);
                // looping on the ids and updating the popularity of each website
            }

        } catch (Exception e) {
            System.out.println(e);
            return false;    // failure
        }
        return true;        // success
    }

    public List<WordsEntity> getAllWordsUnWindDetails() {
        AggregationOperation unwind = Aggregation.unwind("details");
        Aggregation aggregation = Aggregation.newAggregation(unwind);

        AggregationResults<WordsEntity> results = mongoTemplate.aggregate(aggregation, "words", WordsEntity.class);

        return results.getMappedResults();
    }

    public List<WordsCountEntity> getAllCountOFWords() {
        // get every word and its count
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
        // returns the number of websites in the database
        return Math.toIntExact(mongoTemplate.count(new Query(), "pages"));
    }

    public void setIdfForOriginalWord(String originalWord, double idf) {
        // Set the IDF of the original (exact) word for all urls
        // IDF is independent of the url, so we set it for all urls
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
        // computes the relevance of all words in the database
        try {
            List<WordsCountEntity> words = this.getAllCountOFWords();
            Integer numberOfWebsites = this.getWebsitesCount();

            // compute the IDF of each word
            for (WordsCountEntity word : words)
                this.setIdfForOriginalWord(word.getOriginal(), (Math.log((double) numberOfWebsites / word.getCount()) / Math.log(2)));


            // compute the relevance of each word
            this.updateRelevance();

        } catch (Exception e) {
            return false;   // failure
        }

        return true;        // success
    }

    public List<ScoresEntity> getScoreOfMultipleData(List<List<String>> matchingConditionsArray) {

        List<Criteria> criteriaList = new ArrayList<>();

        for (List<String> inputArray : matchingConditionsArray) {
            String url = inputArray.get(0);
            String original = inputArray.get(1);
            String root = inputArray.get(2);

            Criteria criteria = Criteria.where("details")
                    .elemMatch(Criteria.where("url").is(url).and("original").is(original))
                    .and("root").is(root);

            criteriaList.add(criteria);
        }

        Criteria matchCriteria = new Criteria().orOperator(criteriaList.toArray(new Criteria[0]));

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(matchCriteria),
                Aggregation.project("details.score", "details.url", "root").andExclude("_id")
        );

        AggregationResults<ScoresEntity> results = mongoTemplate.aggregate(aggregation, "words", ScoresEntity.class);
        return results.getMappedResults();


    }


    public Double getScore(String root, String original, String url) {
        // Create a query to match the documents
        Query query = new Query();
        query.addCriteria(Criteria.where("root").is(root));

        // Create a projection to retrieve only the matching relevance
        ProjectionOperation projection = Aggregation.project("details")
                .andExclude("_id");

        // Create an unwind operation on the "details" array
        UnwindOperation unwind = Aggregation.unwind("details");

        // Create a match operation to filter the "details" array elements
        MatchOperation match = Aggregation.match(
                Criteria.where("details.original").is(original)
                        .and("details.url").is(url)
        );

        // Create a projection to extract the relevance field
        ProjectionOperation relevanceProjection = Aggregation.project("details.score");

        // Execute the aggregation pipeline
        Aggregation aggregation = Aggregation.newAggregation(
                projection,
                unwind,
                match,
                relevanceProjection
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "words", Document.class);
        List<Document> mappedResults = results.getMappedResults();

        // Extract the relevance values from the mapped results
        List<Double> relevanceList = new ArrayList<>();
        for (Document document : mappedResults) {
            Double relevance = document.getDouble("score");
            relevanceList.add(relevance);
        }

        if (relevanceList.size() == 0)
            return 0.0;
        return relevanceList.get(0);
    }

    public MongoCollection<Document> createOrGetCollection(String collectionName) {
        if (!mongoTemplate.collectionExists(collectionName)) {
            mongoTemplate.createCollection(collectionName);
        }
        return mongoTemplate.getCollection(collectionName);
    }

    public List<String> searchQuery(String searchText) {
        // Create a query with regex pattern for case-insensitive partial matching
        Criteria criteria = Criteria.where("query").regex("(?i).*" + searchText + ".*");
        Query query = Query.query(criteria);

        // Execute the query and retrieve matching documents as a list
        List<Document> documents = mongoTemplate.find(query, Document.class, "queries");

        // Extract the "query" field from each document into a list of strings
        return documents.stream()
                .map(document -> document.getString("query"))
                .collect(Collectors.toList());

    }

    public void createQuery(String queryText){
        MongoCollection<Document> queryCollection  =this.createOrGetCollection("queries");

        Document existingQuery = queryCollection.find(Filters.eq("query", queryText)).first();
        if (existingQuery != null) {
            // Query already exists, skip insertion
            return;
        }

        // Create the new query document
        Document newQuery = new Document("query", queryText);

        // Insert the new query document
        queryCollection.insertOne(newQuery);

    }
}
