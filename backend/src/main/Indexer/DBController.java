import Collections.Site;
import Collections.Word;
import com.mongodb.BasicDBList;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.print.Doc;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.mongodb.client.model.Filters.eq;

public class DBController {


    static String DBConnectionString = "mongodb://localhost:27017";
    static MongoDatabase database;
    static MongoCollection<Document> WordCollection;
    static MongoCollection<Document> URLCollection;

    public static void connect() {

        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE);
        MongoClient mongoClient = MongoClients.create(DBConnectionString);


        database = mongoClient.getDatabase("SearchEngine");
        //create Collection if doesn't exist
        try {
            database.createCollection("words");
            System.out.println("Created Words Collection");
        } catch (Exception e) {
            System.out.println("Words Collection already exists");
        }
        try {
            database.createCollection("urls");
            System.out.println("Created urls Collection");
        } catch (Exception e) {
            System.out.println("urls Collection already exists");
        }


        System.out.println("===========================================================");
        WordCollection = database.getCollection("words");
        URLCollection = database.getCollection("urls");


    }


    //    //        {
//    //            word: "lol",
//    //            sites: [
//    //                    {
//    //                        URL: "lol.com",
//    //                        paragraph:["lol xddd"],
//    //                        placesOfOccurrence:["h1","h2","h3"],
//    //                        noOfOccurrences: 3,
//    //                        termFrequency: 0,
//    //                        pagePopularity:0,
//    //                        isSpam: false
//    //                    }
//    //            ]
//    //        }

    //            Document newWord = new Document();
//            newWord.put("word", "loler");
//    //        {
//    //            word: "lol",
//    //            sites: [
//    //                    {
//    //                        URL: "lol.com",
//    //                        paragraph:["lol xddd"],
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
//            }

    public static Document getSiteinWord(String word, String SiteURL) {
        Bson projectionFields = Projections.fields(
                Projections.excludeId());

        Document DBWord = WordCollection.find(eq("word", word))
                .projection(projectionFields)
                .first();

        List<Document> Wordsites = (List<Document>) DBWord.get("sites");
        Document resSite=null;
        for(int i=0; i< Wordsites.size(); i++){
            Document site=Wordsites.get(i);
            String url= (String) site.get("url");
//                System.out.println(site);
//                System.out.println(url);
            if(url.equals(SiteURL)){
                resSite=site;
                break;
            }
        }
//        System.out.println(Wordsites);
        return resSite;


    }

    //returns null if didn't find it
    public static Document getWord(String word) {
        WordCollection.find();
        Bson projectionFields = Projections.fields(
                Projections.excludeId());
        Document DBWord = WordCollection.find(eq("word", word))
                .projection(projectionFields)
                .first();
        return DBWord;
    }

    public static void createWord(String word, String paragraph, String placeOfOccurrence, String SiteURL) {
        Document newWord = new Document();
        newWord.put("word", word);
        BasicDBList placesOfOccurrence = new BasicDBList();
        placesOfOccurrence.add(placeOfOccurrence);
        BasicDBList paragraphs = new BasicDBList();
        paragraphs.add(paragraph);

        Document newSite = new Document("url", SiteURL).append("placesOfOccurrence", placesOfOccurrence)
                .append("noOfOccurrences", 1).append("termFrequency", 0)
                .append("relevance", 0).append("isSpam", false).append("paragraphs", paragraphs);

        BasicDBList sites = new BasicDBList();
        sites.add(newSite);
        newWord.append("sites", sites);
        WordCollection.insertOne(newWord);

    }

    public static void writeWord(String Word, String paragraph, String placeOfOccurrence, String SiteURL) {

        Document word = getWord(Word);
        if(word==null){
            createWord(Word,paragraph,placeOfOccurrence, SiteURL);
            return;
        }
    }

    ///////////////////////URL Collection//////////////////////////////
    public static Document getSite(String siteURL) {
        Bson projectionFields = Projections.fields(
                Projections.excludeId());
        Document DBSite = URLCollection.find(eq("url", siteURL))
                .projection(projectionFields)
                .first();
        return DBSite;
    }
    public static void writeSite(String SiteURL){
        Document site = getSite(SiteURL);
        if (site !=null)
            return;

        Document newSite= new Document();
        newSite.put("url", SiteURL);
        newSite.put("popularity",0);
        URLCollection.insertOne(newSite);

    }

}
