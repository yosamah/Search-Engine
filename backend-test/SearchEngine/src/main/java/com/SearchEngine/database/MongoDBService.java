package com.SearchEngine.database;

import com.mongodb.client.MongoCollection;
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
import java.util.List;

@Service
public class MongoDBService {

    private final MongoTemplate mongoTemplate;
    MongoCollection<Document> wordsCollection;
    List<Document> wordsDocuments;

    @Autowired
    public MongoDBService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
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
                int popularity = detail.getInteger(	"popularity");
                double score =( relevance * popularityPercentage + popularity * relevancePercentage);

                Document updatedDetail = new Document(detail)
                        .append("score", (int) score);

                updatedDetails.add(updatedDetail);
            }

            document.put("details", updatedDetails);
            wordsCollection.replaceOne(new Document("_id", document.get("_id")), document);
        }
    }

    public void updatePopularity(String url, int popularity) {
        // Build the query to match documents with the provided URL
        Query query = new Query();
        query.addCriteria(Criteria.where("details.url").is(url));

        // Build the update to set the popularity field in the matched elements of the details array
        Update update = new Update().set("details.$[elem].popularity", popularity);
        update.filterArray(Criteria.where("elem.url").is(url));

        // Execute the update query
        mongoTemplate.updateMulti(query, update, "words");
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
}
