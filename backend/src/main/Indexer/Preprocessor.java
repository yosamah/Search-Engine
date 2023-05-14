import java.util.ArrayList;
import java.util.List;

public class Preprocessor {


    static String stopWordsFilePath="./backend/src/main/Indexer/StopWords.txt";
    static List<String> StopWords;
    static  String StopWordsRegex;
    Preprocessor(){
        try {
        StopWords = stopWordsReader.ReadStopWords(stopWordsFilePath);

        }catch (Exception e){

        }
        StopWordsRegex="(";
        for(int i=0;i <StopWords.size();i++)
        {
            if(i!=StopWords.size()-1){
                //notice the ingrained space here
                StopWordsRegex+=StopWords.get(i)+"|";
            }
            else {
                //notice the ingrained space here
                StopWordsRegex+=StopWords.get(i)+") ";
            }

        }
//        System.out.println(StopWordsRegex);
    }

    static String preprocess(String Text){
        Text = Text.toLowerCase();
        // Remove Stop Words
        Text= Text.replaceAll(StopWordsRegex," ");
        // --Remove hyperlinks
        Text = Text.replaceAll(
                "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
                "");
        // --Remove special charachters
        Text = Text.replaceAll("-", " ");
        Text = Text.replaceAll("[-®#%~!@#$%^&*()_+/*?<>':;–.,`’\"\\[\\]]+", " ");
        // Replace Single occurring characters
        Text = Text.replaceAll(" [a-zA-Z0-9] ", " ");
        // --Replace 2 or more white spaces with a single white space
        Text = Text.replaceAll("\\s{2,}", " ");
        return Text;
    }
}
