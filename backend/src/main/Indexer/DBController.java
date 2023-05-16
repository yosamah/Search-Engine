import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DocumentToDBRefTransformer;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
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
//    static MongoCollection<Document> URLCollection;
    static MongoCollection<Document> pageCollection;

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
//        try {
//            database.createCollection("urls");
//            System.out.println("Created urls Collection");
//        } catch (Exception e) {
//            System.out.println("urls Collection already exists");
//        }


        System.out.println("===========================================================");
//        URLCollection = database.getCollection("urls");
//        htmlDocsCollection = database.getCollection("html");
        WordCollection = database.getCollection("words");
        pageCollection = database.getCollection("pages");


    }

    ////////////////////////////////////////////Pages Collection/////////////////////////////////

    public static Document getHTMLDoc(){
        Bson filter= Filters.eq("isIndexed",false);
        Document htmlDoc= pageCollection.find(filter).first();
        return htmlDoc;
    }

    public static void markDocAsIndexed(Document htmlDoc){
        htmlDoc.put("isIndexed",true);
        String id= htmlDoc.get("_id").toString();
        pageCollection.updateOne(new BasicDBObject("_id",new ObjectId(id)),
                new BasicDBObject( "$set", new BasicDBObject(htmlDoc)));
    }


    //utility function for testing
    public static void markAllasnonIndexed(){
        //empty filter and set every indexed to false
        pageCollection.updateMany(new Document(),new Document("$set", new Document("isIndexed", false)));
    }


    /////////////////////////////////////////////Words Collection//////////////////////////////


    /////Roots
//    root —-->  root: “play”
//    details: [Words]
    public static Document getRoot(String rootWord){
        Document DBRoot = WordCollection.find(eq("root", rootWord))
                .first();
        return DBRoot;
    }

    public static void createRoot(String root) {
        Document newRoot = new Document();
        newRoot.put("root", root);
        newRoot.put("details", new BasicDBList());
        WordCollection.insertOne(newRoot);
    }

    /////Words
//    word —> original : “player”
//    url: “lol.com”
//    paragraphs:[“lol”,”lol”]
//    relevance
//    places of Ocurrences
//    TF
//    popularity
//    isSpam
//    noOfOccurrences

    //creates a new word and inserts it into the collection
    public static void createWordSite(Document root, String word, String paragraph, String placeOfOccurrence, String SiteURL, Double populariy, String title) {
        Document newWord = new Document();
        newWord.put("original", word);
        BasicDBList placesOfOccurrence = new BasicDBList();
        placesOfOccurrence.add(placeOfOccurrence);
        BasicDBList paragraphs = new BasicDBList();
        paragraphs.add(paragraph);

        newWord.append("url", SiteURL).append("placesOfOccurrence", placesOfOccurrence)
                .append("noOfOccurrences", 1).append("termFrequency", 0)
                .append("relevance", 0).append("isSpam", false).append("paragraphs", paragraphs).append("popularity",populariy).append("IDF",0)
                .append("title",title);

        List <Document> details= (List<Document>) root.get("details");
        details.add(newWord);

        root.append("details",details);

        String id=  root.get("_id").toString();

        WordCollection.updateOne(new BasicDBObject("_id",new ObjectId(id)),
                new BasicDBObject( "$set", new BasicDBObject(root)));

    }

    //adds a new ocurrence to an exitsting word in the collection
    public static void addWordSiteOccurrence(Document root, Document word,String paragraph, String placeOfOccurrence, String SiteURL){
        List<String> placesOfOccurrence = ( List<String>) word.get("placesOfOccurrence");
        placesOfOccurrence.add(placeOfOccurrence);
        List<String> paragraphs = ( List<String>) word.get("paragraphs");
        paragraphs.add(paragraph);
        int noOfOcrrences= (int)word.get("noOfOccurrences");
        noOfOcrrences+=1;

        Document newWord=  new Document("url", SiteURL).append("original",word.get("original")).append("placesOfOccurrence", placesOfOccurrence)
                .append("noOfOccurrences", noOfOcrrences).append("termFrequency", 0)
                .append("relevance", 0).append("isSpam", false).append("paragraphs", paragraphs).append("popularity",word.get("popularity")).append("IDF",0)
                .append("title",word.get("title"));
        List<Document> details = (List<Document>)root.get("details");



        details.remove(word);
        details.add(newWord);
        root.append("details",details);
        String id=  root.get("_id").toString();
        WordCollection.updateOne(new BasicDBObject("_id",new ObjectId(id)),
                new BasicDBObject( "$set", new BasicDBObject(root)));
    }



//    public static Document getSiteInWord(Document DBword, String SiteURL) {
//
//        List<Document> Wordsites = (List<Document>)DBword.get("sites");
//        Document resSite=null;
//        for(int i=0; i< Wordsites.size(); i++){
//            Document site=Wordsites.get(i);
//            String url= (String) site.get("url");
//            if(url.equals(SiteURL)){
//                resSite=site;
//                break;
//            }
//        }
//        return resSite;
//    }

    //adds a new site to the list of site in word
//    public static void addSiteInWord(Document DBWord, String paragraph, String placeOfOccurrence, String SiteURL){
//        BasicDBList placesOfOccurrence = new BasicDBList();
//        placesOfOccurrence.add(placeOfOccurrence);
//        BasicDBList paragraphs = new BasicDBList();
//        paragraphs.add(paragraph);
//
//        Document newSite=  new Document("url", SiteURL).append("placesOfOccurrence", placesOfOccurrence)
//                    .append("noOfOccurrences", 1).append("termFrequency", 0)
//                    .append("relevance", 0).append("isSpam", false).append("paragraphs", paragraphs);
//        List<Document> sites = (List<Document>)DBWord.get("sites");
//
//        sites.add(newSite);
//        DBWord.append("sites",sites);
//        System.out.println(DBWord);
//
//        String id=  DBWord.get("_id").toString();
//
//        WordCollection.updateOne(new BasicDBObject("_id",new ObjectId(id)),
//                                 new BasicDBObject( "$set", new BasicDBObject(DBWord)));
//    }



    //returns null if didn't find it
//    public static Document getWord(String word) {
//        Document DBWord = WordCollection.find(eq("word", word))
//                .first();
//        return DBWord;
//    }

    public static Document getSiteWord(Document root  ,String word, String SiteURL) {
        Document DBword = null;
        for (Document wordDoc : (List<Document>) root.get("details")) {
            if (wordDoc.get("original").equals(word) && wordDoc.get("url").equals(SiteURL)) {
                DBword = wordDoc;
                break;
            }

        }
        return DBword;
    }



    // A function that adds a new word if it doesn't exist, a new site if the existing word doesn't contain it
    // or a new occurrence to existing side.
    public static void addSiteWordToRoot(String Root, String Word, String paragraph, String placeOfOccurrence, String SiteURL,double popularity,String title) {

        Document root = getRoot(Root);
        if(root==null){
            createRoot(Root);
            //TODO: add word to root here
            root = getRoot(Root);
            createWordSite(root,Word,paragraph,placeOfOccurrence,SiteURL,popularity,title);
            return;
        }
        //if there is a word-site pair
        Document wordSite= getSiteWord(root,Word,SiteURL);
        if(wordSite ==null)
        {
            System.out.println("Adding new word site");
//            addSiteInWord(word,paragraph,placeOfOccurrence,SiteURL);
            createWordSite(root,Word,paragraph,placeOfOccurrence,SiteURL,popularity,title);
            return;
        }
        addWordSiteOccurrence(root,wordSite,paragraph,placeOfOccurrence,SiteURL);
//            System.out.println("Adding new occurrence");
//        addSiteOccurrence(word,site,paragraph,placeOfOccurrence,SiteURL);

    }

    //TODO: Add functions to handle isSpam and TermFrequecny
    public static void updateWordSiteTF(String Root, String Word, String SiteURL, int TotalNoOfWords){
        Document root = getRoot(Root);
        System.out.println("Currently on " + Root +" " + Word +" " + SiteURL);
        Document wordSite= getSiteWord(root,Word,SiteURL);

        int noOfOcrrences= (int)wordSite.get("noOfOccurrences");
        double TF= (double)noOfOcrrences/TotalNoOfWords;
        boolean isSpam= TF>0.2 ? true: false;

        Document newWord=  new Document("url", SiteURL).append("original",wordSite.get("original")).append("placesOfOccurrence", wordSite.get("placesOfOccurrence"))
                .append("noOfOccurrences", noOfOcrrences).append("termFrequency", TF)
                .append("relevance", 0).append("isSpam", isSpam).append("paragraphs", wordSite.get("paragraphs")).append("popularity",wordSite.get("popularity")).append("IDF",0)
                .append("title",wordSite.get("title"));
        List<Document> details = (List<Document>)root.get("details");



        details.remove(wordSite);
        details.add(newWord);
        root.append("details",details);
        String id=  root.get("_id").toString();
        WordCollection.updateOne(new BasicDBObject("_id",new ObjectId(id)),
                new BasicDBObject( "$set", new BasicDBObject(root)));
    }


    ///////////////////////URL Collection//////////////////////////////
//    public static Document getSite(String siteURL) {
//        Bson projectionFields = Projections.fields(
//                Projections.excludeId());
//        Document DBSite = URLCollection.find(eq("url", siteURL))
//                .projection(projectionFields)
//                .first();
//        return DBSite;
//    }
//    public static void writeSite(String SiteURL){
//        Document site = getSite(SiteURL);
//        if (site !=null)
//            return;
//
//        Document newSite= new Document();
//        newSite.put("url", SiteURL);
//        newSite.put("popularity",0);
//        URLCollection.insertOne(newSite);
//
//    }
//
//    public static void AddSite(String SiteURL){
//        Document site= getSite(SiteURL);
//        if(site != null)
//            return;
//        writeSite(SiteURL);
//    }

}
