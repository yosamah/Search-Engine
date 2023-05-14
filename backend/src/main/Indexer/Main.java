
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class Main {

    static String lastIndexFilePath="./backend/src/main/Indexer/lastIndex.txt";
    static String producedHTMLpath="./Htmldocs.txt";
    static String stopWordsFilePath="./backend/src/main/Indexer/StopWords.txt";
//    static MongoDatabase database;
//    static MongoCollection<Document> WordCollection;



    public static void main(String[] args) throws Exception, Throwable {


            DBController.connect();
//            Document worder = DBController.getWord("loler");
//            System.out.println(worder);

            DBController.getSiteinWord("loler","lol.com");
            DBController.getSiteinWord("loler","www.youtube.com");

            int lastIndex= lastIndexHandler.ReadLastIndex(lastIndexFilePath);

            File producedHTMLFiles= new File(producedHTMLpath);
            producedHTMLFiles.createNewFile();
            Scanner htmlsc= new Scanner(producedHTMLFiles);

            htmlsc.useDelimiter("\n");
            int counter=0;
            //move cursor to lastIndex
            while(counter<lastIndex && htmlsc.hasNext() ) htmlsc.next();

            String currentDoc;
            
            Preprocessor preprocessor = new Preprocessor();
            while(htmlsc.hasNext()){
                //TODO: Remove the comment when testing is finished
                //lastIndex++;
                currentDoc=htmlsc.next();
                currentDoc=htmlsc.next();
                currentDoc=htmlsc.next();
                currentDoc=htmlsc.next();
                currentDoc=htmlsc.next();
                org.jsoup.nodes.Document temp = Jsoup.connect(currentDoc).get();
//                System.out.println(temp.title());
//                System.out.println(temp.text());
//                System.out.println(temp.select("h2").text());
//                System.out.println(temp.select("h1").text());
//                System.out.println(temp.select("p").text());
//
//
//                System.out.println(preprocessor.preprocess( temp.select("h2").text()));
//                System.out.println(preprocessor.preprocess(temp.select("h1").text()));
//                System.out.println(preprocessor.preprocess(temp.select("p").text()));

            }


            //write the last document indexed
            lastIndexHandler.WriteLastIndex(lastIndexFilePath,lastIndex);



//            System.out.println(lastIndex);
//
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
//    //                        paragraph:["lol xddd"],
//    //                        placesOfOccurrence:["h1","h2","h3"],
//    //                        noOfOccurrences: 3,
//    //                        termFrequency: 0,
//    //                        pagePopularity:0,
//    //                        isSpam: false
//    //                    }
//    //            ]
//    //        }


    }


}
