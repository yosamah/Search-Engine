import Database.Collections.Site;
import Database.Collections.Word;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class Main {


    public static void main(String[] args) throws Exception, Throwable {
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")){

            MongoDatabase database = mongoClient.getDatabase("SearchEngine");
            //create Collection if doesn't exist
            try {
                database.createCollection("words");
                System.out.println("Collection Created");
            }catch (Exception e){
                System.out.println("Collection already exists");
            }

            MongoCollection<Document> WordCollection = database.getCollection("words");
            //TODO: Decide on index here.. most likely word ascendingly

            File lastIndexedFile = new File("./backend/src/main/Indexer/lastIndex.txt");
            if(lastIndexedFile.createNewFile()){
                System.out.println("Created New file");
                FileWriter myWriter = new FileWriter("./backend/src/main/Indexer/lastIndex.txt");
                myWriter.write("0");
                myWriter.close();
            }

            Scanner sc = new Scanner(lastIndexedFile);
            int lastIndex=sc.nextInt();
            
//
//            //get lastIndexed Document.
//            int lastIndexed = Integer.parseInt(Files.ReadFile("./out/indicator.txt"));
//
//
//            Document newWord = new Document();
//            newWord.put("word", "loler");
//    //        {
//    //            word: "lol",
//    //            sites: [
//    //                    {
//    //                        URL: "lol.com",
//    //                        indicesOfOccurrence:[1,2,3],
//    //                        placesOfOccurrence:["h1","h2","h3"],
//    //                        noOfOccurrences: 3,
//    //                        termFrequency: 0,
//    //                        pagePopularity:0,
//    //                        isSpam: false
//    //                    }
//    //            ]
//    //        }
//
//            BasicDBList indicesOfOccurrence= new BasicDBList();
//            indicesOfOccurrence.add(1);
//            indicesOfOccurrence.add(2);
//            indicesOfOccurrence.add(3);
//            BasicDBList placesOfOccurrence= new BasicDBList();
//            placesOfOccurrence.add("h1");
//            placesOfOccurrence.add("h2");
//            placesOfOccurrence.add("h3");
//
//            Document newSite= new Document("URL","www.youtube.com").append("indicesOfOccurrence", indicesOfOccurrence)
//                    .append("placesOfOccurrence",placesOfOccurrence)
//                    .append("noOfOccurrences",3).append("termFrequency",0)
//                    .append("pagePopularity",0).append("isSpam",false);
//
//            BasicDBList sites= new BasicDBList();
//            sites.add(newSite);
//            sites.add(newSite);
//            newWord.append("Sites",sites);
//            WordCollection.insertOne(newWord);
            }


    }
}
