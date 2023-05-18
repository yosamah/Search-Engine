package WebCrawler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;

import com.mongodb.BasicDBList;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class CrawlerController {

    static String DBConnectionString = "mongodb://localhost:27017";
    static MongoDatabase database;
    static MongoCollection<Document> pagesCollection;
    static MongoCollection<Document> futurePagesCollection;
    static MongoCollection<Document> currentlyCrawlingCollection;

    public static void connect() {

        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE);
        MongoClient mongoClient = MongoClients.create(DBConnectionString);

        database = mongoClient.getDatabase("SearchEngine");
        // Creates Collection if it doesn't exist
        try {
            database.createCollection("pages");
            System.out.println("Created pages collection");
        } catch (Exception e) {
            System.out.println("pages collection already exists");
        }
        try {
            database.createCollection("futurePages");
            System.out.println("Created futurePages collection");
        } catch (Exception e) {
            System.out.println("futurePages collection already exists");
        }
        try {
            database.createCollection("currentlyCrawling");
            System.out.println("Created currentlyCrawling collection");
        } catch (Exception e) {
            System.out.println("currentlyCrawling collection already exists");
        }

        System.out.println("===========================================================");
        pagesCollection = database.getCollection("pages");
        futurePagesCollection = database.getCollection("futurePages");
        currentlyCrawlingCollection = database.getCollection("currentlyCrawling");
    }

    // Creates a new page and inserts it into the currentlyCrawling collection
    public void createNewCurrentlyCrawlingPage(String url, String fileName) {
        Document newDoc = new Document("url", url);
        newDoc.put("fileName", fileName);
        currentlyCrawlingCollection.insertOne(newDoc);
    }

    // Removes a document from the currentlyCrawling collection
    public void deleteCurrentlyCrawlingPage(String url) {
        Document query = new Document("url", url);
        currentlyCrawlingCollection.deleteOne(query);
    }

    // Removes a document from the currentlyCrawling collection using the fileName
    public void deleteCurrentlyCrawlingPageByFileName(String fileName) {
        Document query = new Document("fileName", fileName);
        currentlyCrawlingCollection.deleteOne(query);
    }

    // Retrieve all documents in the currentlyCrawling Collection
    public MongoCursor<Document> getAllDocuments() {
        return currentlyCrawlingCollection.find().iterator();
    }

    // Retrieves all the file names of the currentlyCrawling collection
    public String[] getAllFileNames() {
        MongoCursor<Document> cursor = currentlyCrawlingCollection.find().iterator();
        String[] fileNames = new String[(int) currentlyCrawlingCollection.countDocuments()];
        int i = 0;
        while (cursor.hasNext()) {
            Document document = cursor.next();
            fileNames[i] = document.getString("fileName");
            i++;
        }
        return fileNames;
    }

    // Gets the number of urls in the currentlyCrawling collection
    public Boolean noCurrentlyCrawlingPage() {
        int number = (int) currentlyCrawlingCollection.countDocuments();
        return number == 0;
    }

    // Finds a document with a matching URL in any collection
    public Document getDocumentWithUrl(String urlToMatch, MongoCollection<Document> collection) {
        Document query = new Document("url", urlToMatch);

        // Executes the query and retrieve the matching document
        return collection.find(query).first();
    }

    // Creates a new page and inserts it into the pages collection
    public void createNewPage(String url, String filePath, String compactString) {
        Document newPage = new Document();
        newPage.put("url", url);
        newPage.put("filePath", filePath);
        newPage.put("compactString", compactString);
        newPage.put("isIndexed", false);
        newPage.put("popularity", 0);
        newPage.put("outgoingLinks", new BasicDBList());
        pagesCollection.insertOne(newPage);
        // System.out.println("Inserting new page: " + url + " into the pages
        // collection");
    }

    // Creates a new page and inserts it into the futurePages collection
    public void createNewFuturePage(String url) {
        Document newDoc = new Document("url", url);
        futurePagesCollection.insertOne(newDoc);
        // System.out.println("Inserting new page: " + url + " into the futurePages
        // collection");
    }

    // Deletes all the urls in the futurePages collection
    public void deleteUrlsToCrawl() {
        futurePagesCollection.deleteMany(new Document());
    }

    // Gets the number of urls in the futurePages collection
    public int getNumberofCrawledPages() {
        return (int) pagesCollection.countDocuments();
    }

    public void RetrieveUrlsToCrawl(ConcurrentHashMap<String, Boolean> urlsToCrawl) {
        // Gets the iterator for the documents
        try (MongoCursor<Document> cursor = futurePagesCollection.find().iterator()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                String url = document.getString("url").replaceAll("\\s+", "");
                urlsToCrawl.put(url, true);
            }
        }
    }

    public void RetrieveCrawledUrls(ConcurrentHashMap<String, Boolean> visitedUrls,
            ConcurrentHashMap<String, String> compactStringOfPages) {
        try (MongoCursor<Document> cursor = pagesCollection.find().iterator()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                String url = document.getString("url").replaceAll("\\s+", "");
                visitedUrls.put(url, true);

                String compactString = document.getString("compactString");
                if (compactString != null) {
                    compactString = compactString.replaceAll("\\s+", "");
                    compactStringOfPages.put(compactString, url);
                }
            }
        } catch (Exception e) {
            System.out.println("Error in RetrieveCrawledUrls");
        }
    }

    // Appends a given url to the outgoing links of each page in pages collection
    public void appendUrlToOutgoingLinks(String baseUrl, String outgoingLink) {
        Document retrievedPage = getDocumentWithUrl(baseUrl, pagesCollection);

        // Define the update operation using $push
        Document update = new Document("$push", new Document("outgoingLinks", outgoingLink));

        // Perform the update
        pagesCollection.updateOne(retrievedPage, update);
    }

}
