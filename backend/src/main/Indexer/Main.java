
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;

import javax.print.Doc;
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
//            DBController.addSiteToWord("lol","lolxdd","h1","www.heher.com");
//            Document siter = DBController.getSiteInWord(worder, "www.hehe.com");
//            DBController.addSiteOccurrence(worder,siter,"lolxdd","h1","www.hehe.com");
//            System.out.println(worder);

//            DBController.getSiteinWord("loler","lol.com");
//            DBController.getSiteinWord("loler","www.youtube.com");

            int lastIndex= lastIndexHandler.ReadLastIndex(lastIndexFilePath);

            File producedHTMLFiles= new File(producedHTMLpath);
            producedHTMLFiles.createNewFile();
            Scanner htmlsc= new Scanner(producedHTMLFiles);

            htmlsc.useDelimiter("\n");
            int counter=0;
            //move cursor to lastIndex
            while(counter<lastIndex && htmlsc.hasNext() ) htmlsc.next();

            String currentDoc;

//            Preprocessor preprocessor = new Preprocessor();
            while(htmlsc.hasNext()){
                //TODO: Remove the comment when testing is finished
                //lastIndex++;
                currentDoc=htmlsc.next();
                currentDoc=htmlsc.next();
                currentDoc=htmlsc.next();
                currentDoc=htmlsc.next();
                currentDoc=htmlsc.next();
                DocumentProcessor.process("http://help.websiteos.com/websiteos/example_of_a_simple_html_page.htm");

            }


            //write the last document indexed
            lastIndexHandler.WriteLastIndex(lastIndexFilePath,lastIndex);

            //Don't forget to calculate TF after the indexing finishes. and determine if spam too.



    }


}
