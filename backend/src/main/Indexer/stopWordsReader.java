import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class stopWordsReader {

    static List<String> ReadStopWords(String stopWordsFilePath) throws Exception {
        File StopWordsFile= new File(stopWordsFilePath);
//        System.out.println("Reading from "+ stopWordsFilePath);
        List<String> StopWords = new ArrayList<String>();
        Scanner stopWordsFileScanner= new Scanner(StopWordsFile);
        stopWordsFileScanner.useDelimiter(",");
        while (stopWordsFileScanner.hasNext()){
            StopWords.add(stopWordsFileScanner.next());
        }
        return StopWords;
    }


}
