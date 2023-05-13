package com.SearchEngine;

import com.mongodb.client.*;
import org.bson.Document;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.MongoTemplate;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.io.File;
import java.util.Iterator;
import java.util.List;

@SpringBootApplication
public class SearchEngineApplication {

	public static void main(String[] args) {

		try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")){

			MongoDatabase database = mongoClient.getDatabase("SearchEngine");
			//create Collection if doesn't exist

			MongoTemplate mongoTemplate = new MongoTemplate(MongoClients.create(), "SearchEngine");
			List<Object> words = mongoTemplate.find(query(where("play").is("player")), Object.class, "words");
			System.out.println("hello");
//
//			try {
//				database.createCollection("words");
//				System.out.println("Collection Created");
//
//			}catch (Exception e){
//				System.out.println("Collection already exists");
//			}

			MongoCollection<Document> WordCollection = database.getCollection("words");
			FindIterable<Document> iterDoc = WordCollection.find();
			Iterator it = iterDoc.iterator();
			while (it.hasNext()) {
				System.out.println(it.next());
			}
			System.out.println("hello");
			//TODO: Decide on index here.. most likely word ascendingly


		}

		SpringApplication.run(SearchEngineApplication.class, args);
	}

}
