
import org.bson.Document;

import java.io.File;
import java.util.*;

public class Indexer implements  Runnable {

    static String producedHTMLpath = "./Htmldocs.txt";
    static String HTMLdocsPath=System.getProperty("user.dir");

    public void run()  {

        DBController dbController=  DBController.getInstance();
        //TODO: Change interface to either read from filepath from DB
        File producedHTMLFiles = new File(producedHTMLpath);

        try{
        producedHTMLFiles.createNewFile();

        }catch (Exception e){

        }
        Scanner htmlsc=null;
        try {
            htmlsc= new Scanner(producedHTMLFiles);

        }catch (Exception e){

        }

        htmlsc.useDelimiter("\n");
        int counter = 0;

        String currentDoc;
        double currPopularity=0;
        File htmlFile;
        Document htmlDoc;
        synchronized (dbController){
            htmlDoc=DBController.getHTMLDoc();
            DBController.markDocAsIndexed(htmlDoc);
        }
        while(htmlDoc!=null ){
            System.out.println("============================================= LOOOOPINGGGGG=====================================");
            System.out.println("Opening " +HTMLdocsPath+htmlDoc.get("filePath") );

            htmlFile= new File ( HTMLdocsPath+htmlDoc.get("filePath"));
//            org.jsoup.nodes.Document Doc= Jsoup.parse(htmlFile);
            currentDoc=(String) htmlDoc.get("url");
            currPopularity= (int)htmlDoc.get("popularity");
            try {
            DocumentProcessor.process(htmlFile,currentDoc,currPopularity);

            }catch (Exception e){

            }
            synchronized (dbController){
                htmlDoc=DBController.getHTMLDoc();
                if(htmlDoc != null)
                    DBController.markDocAsIndexed(htmlDoc);
            }
        }

//        while (htmlsc.hasNext()) {
//            // TODO: Remove the comment when testing is finished
//            currentDoc = htmlsc.next();
//            DocumentProcessor.process(currentDoc,0);
////            DocumentProcessor.process("https://www.playframework.com/",currPopularity);
//
//        }



    }


}
