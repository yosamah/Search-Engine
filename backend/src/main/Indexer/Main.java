
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

    static String producedHTMLpath = "./Htmldocs.txt";
    static String HTMLdocsPath=System.getProperty("user.dir");

    public static void main(String[] args) throws Exception, Throwable {

        DBController.connect();

        //TODO: Change interface to either read from filepath from DB
        File producedHTMLFiles = new File(producedHTMLpath);
        producedHTMLFiles.createNewFile();
        Scanner htmlsc = new Scanner(producedHTMLFiles);

        htmlsc.useDelimiter("\n");
        int counter = 0;

        String currentDoc;
        double currPopularity=0;
        File htmlFile;
        Document htmlDoc=DBController.getHTMLDoc();
        while(htmlDoc!=null ){
            System.out.println("============================================= LOOOOPINGGGGG=====================================");
            System.out.println("Opening " +HTMLdocsPath+htmlDoc.get("filePath") );

            htmlFile= new File ( HTMLdocsPath+htmlDoc.get("filePath"));
//            org.jsoup.nodes.Document Doc= Jsoup.parse(htmlFile);
            currentDoc=(String) htmlDoc.get("url");
            currPopularity= (int)htmlDoc.get("popularity");
            DocumentProcessor.process(htmlFile,currentDoc,currPopularity);

            DBController.markDocAsIndexed(htmlDoc);
            htmlDoc=DBController.getHTMLDoc();
        }

//        while (htmlsc.hasNext()) {
//            // TODO: Remove the comment when testing is finished
//            // lastIndex++;
//            currentDoc = htmlsc.next();
//            DocumentProcessor.process(currentDoc,0);
////            DocumentProcessor.process("https://www.playframework.com/",currPopularity);
//
//        }

        // write the last document indexed
//        lastIndexHandler.WriteLastIndex(lastIndexFilePath, lastIndex);

        // Don't forget to calculate TF after the indexing finishes. and determine if
        // spam too.

    }


}
