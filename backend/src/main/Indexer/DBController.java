import Collections.Site;
import Collections.Word;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

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

    public static Document getSiteInWord(Document DBword, String SiteURL) {

        List<Document> Wordsites = (List<Document>)DBword.get("sites");
        Document resSite=null;
        for(int i=0; i< Wordsites.size(); i++){
            Document site=Wordsites.get(i);
            String url= (String) site.get("url");
            if(url.equals(SiteURL)){
                resSite=site;
                break;
            }
        }
        return resSite;
    }

    //adds a new site to the list of site in word
    public static void addSiteInWord(Document DBWord, String paragraph, String placeOfOccurrence, String SiteURL){
        BasicDBList placesOfOccurrence = new BasicDBList();
        placesOfOccurrence.add(placeOfOccurrence);
        BasicDBList paragraphs = new BasicDBList();
        paragraphs.add(paragraph);

        Document newSite=  new Document("url", SiteURL).append("placesOfOccurrence", placesOfOccurrence)
                    .append("noOfOccurrences", 1).append("termFrequency", 0)
                    .append("relevance", 0).append("isSpam", false).append("paragraphs", paragraphs);
        List<Document> sites = (List<Document>)DBWord.get("sites");

        sites.add(newSite);
        DBWord.append("sites",sites);
        System.out.println(DBWord);

        String id=  DBWord.get("_id").toString();

        WordCollection.updateOne(new BasicDBObject("_id",new ObjectId(id)),
                                 new BasicDBObject( "$set", new BasicDBObject(DBWord)));
    }

    //adds a new ocurrence to an exitsting site in the word
    public static void addSiteOccurrence(Document DBWord,Document DBSite, String paragraph, String placeOfOccurrence, String SiteURL){
        List<String> placesOfOccurrence = ( List<String>) DBSite.get("placesOfOccurrence");
        placesOfOccurrence.add(placeOfOccurrence);
        List<String> paragraphs = ( List<String>) DBSite.get("paragraphs");
        paragraphs.add(paragraph);
        int noOfOcrrences= (int)DBSite.get("noOfOccurrences");
        noOfOcrrences+=1;

        Document newSite=  new Document("url", SiteURL).append("placesOfOccurrence", placesOfOccurrence)
                .append("noOfOccurrences", noOfOcrrences).append("termFrequency", 0)
                .append("relevance", 0).append("isSpam", false).append("paragraphs", paragraphs);
        List<Document> sites = (List<Document>)DBWord.get("sites");

        sites.remove(DBSite);
        sites.add(newSite);
        DBWord.append("sites",sites);
        System.out.println(DBWord);

        String id=  DBWord.get("_id").toString();

        WordCollection.updateOne(new BasicDBObject("_id",new ObjectId(id)),
                new BasicDBObject( "$set", new BasicDBObject(DBWord)));
    }

    //returns null if didn't find it
    public static Document getWord(String word) {
        WordCollection.find();
//        Bson projectionFields = Projections.fields(
//                Projections.excludeId());
        Document DBWord = WordCollection.find(eq("word", word))
                .first();
        return DBWord;
    }


    //creates a new word and inserts it into the collection
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

    // A function that adds a new word if it doesn't exist, a new site if the existing word doesn't contain it
    // or a new occurrence to existing side.
    public static void addSiteToWord(String Word, String paragraph, String placeOfOccurrence, String SiteURL) {

        Document word = getWord(Word);
        if(word==null){
            createWord(Word,paragraph,placeOfOccurrence, SiteURL);
            return;
        }
        //if there is a word
        Document site =  getSiteInWord(word,SiteURL);
        if(site ==null)
        {
//            System.out.println("Adding new site");
            addSiteInWord(word,paragraph,placeOfOccurrence,SiteURL);
            return;
        }
//            System.out.println("Adding new occurrence");
        addSiteOccurrence(word,site,paragraph,placeOfOccurrence,SiteURL);



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
