import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.print.Doc;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DocumentProcessor {


    static String elements="h1h2h3h4h5h6p";
    static List<String> elementsList= Arrays.asList("title", "h1", "h2","h3","h4","h5","h6","p");

    // we save them in a set in order to loop on them later when we finish processing


    public static void process(String DocumentURL, double popularity) throws IOException {

        //first add it to Sites Collection
        Set<String> uniqueWords = new HashSet<String>();


        Document htmldoc = Jsoup.connect(DocumentURL).get();
        String preprocessedString;
        int TotalNumberofWords=0;
        Tokenizer tok= new Tokenizer();
        for(int i =0 ;i <elementsList.size(); i++){
            String element= elementsList.get(i);
            preprocessedString= processElement(htmldoc,element);
            if (preprocessedString.length()==0){
                continue;
            }
            TotalNumberofWords+=preprocessedString.length();
            String [] tokens = Tokenizer.writeTokens(preprocessedString,DocumentURL,element,popularity);
             uniqueWords.addAll(Arrays.asList(tokens));
        }
        //TODO: update Term Frequency and isSpam
        String uniqueWordsText=String.join(" ",uniqueWords);
        Tokenizer.updateWordsTF(uniqueWordsText,DocumentURL,TotalNumberofWords);
//        System.out.println(TotalNumberofWords);
//        String[] uniqueWordsArr=uniqueWords.toArray(new String[0]);
//        System.out.println(uniqueWords);
//        System.out.println(String.join(" ",uniqueWords));

//        for (int i=0; i<uniqueWords.size();i++){
//
//        }
//        System.out.println(uniqueWords.size());
    }

    public static void process(File htmlFile , String DocumentURL, double popularity) throws IOException {

        //first add it to Sites Collection
        Set<String> uniqueWords = new HashSet<String>();


        Document htmldoc = Jsoup.parse(htmlFile);
        String preprocessedString;
        int TotalNumberofWords=0;
        Tokenizer tok= new Tokenizer();
        for(int i =0 ;i <elementsList.size(); i++){
            String element= elementsList.get(i);
            preprocessedString= processElement(htmldoc,element);
            if (preprocessedString.length()==0){
                continue;
            }
            TotalNumberofWords+=preprocessedString.length();
            String [] tokens = Tokenizer.writeTokens(preprocessedString,DocumentURL,element,popularity);
            uniqueWords.addAll(Arrays.asList(tokens));
        }
        //TODO: update Term Frequency and isSpam
        String uniqueWordsText=String.join(" ",uniqueWords);
        Tokenizer.updateWordsTF(uniqueWordsText,DocumentURL,TotalNumberofWords);
    }


    public static String processElement(Document htmldoc, String element){
        String preprocessedText;
        if(elements.contains(element)){
            preprocessedText=Preprocessor.preprocess(htmldoc.select(element).text());
            return preprocessedText;
        }
        preprocessedText=Preprocessor.preprocess(htmldoc.title());
        return preprocessedText;
    }
}
