import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.print.Doc;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DocumentProcessor {


    static String elements="h1h2h3h4h5h6p";
    static List<String> elementsList= Arrays.asList("title", "h1", "h2","h3","h4","h5","h6","p");

    // we save them in a set in order to loop on them later when we finish processing
    static Set<String> uniqueWords = new HashSet<String>();


    public static void process(String DocumentURL) throws IOException {

        //first add it to Sites Collection

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
            String [] tokens = Tokenizer.writeTokens(preprocessedString,DocumentURL,element);
             uniqueWords.addAll(Arrays.asList(tokens));
        }
        //TODO: update Term Frequency and isSpam
        System.out.println(TotalNumberofWords);
        System.out.println(uniqueWords);
        System.out.println(uniqueWords.size());
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
