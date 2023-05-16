import java.util.List;

public class Tokenizer {



    static String stopWordsFilePath="./backend/src/main/Indexer/StopWords.txt";
    static List<String> StopWords;
    static  String StopWordsAggr;
    static  Stemmer stemmer;


    public Tokenizer(){
        try {
            StopWords = stopWordsReader.ReadStopWords(stopWordsFilePath);
            StopWordsAggr=String.join("",StopWords);
//            System.out.println(StopWordsRegex);

        }catch (Exception e){

        }
        stemmer = new Stemmer();
    }

    static  public  boolean isStopWord(String word){

        return StopWordsAggr.contains(word);

    }
    //writes tokens to DB and returns them for use
    static public String[] writeTokens(String Text, String currentSite, String placeOfOccurrence,double popularity,String title) {
        String[] words = Text.split(" ");


        for (int i = 0; i < words.length; i++) {

            if (words[i]=="")
                continue;

            if (words[i].length()==1)
                continue;
            if (isStopWord(words[i]))
                continue;

            String word = words[i];
            String wordParagraph = getParagraph(words,i);
            String root = stemmer.stem(word);
            DBController.addSiteWordToRoot(root,word,wordParagraph,placeOfOccurrence,currentSite,popularity,title);

        }
        return words;
    }

    static public String[] updateWordsTF(String Text, String currentSite,int TotalNoOfWords) {
        String[] words = Text.split(" ");


        for (int i = 0; i < words.length; i++) {

            if (words[i]=="")
                continue;

            if (words[i].length()==1)
                continue;
            if (isStopWord(words[i]))
                continue;

            String word = words[i];
            String root = stemmer.stem(word);
            System.out.println(root);
            //TODO: write to DB here
            DBController.updateWordSiteTF(root,word,currentSite,TotalNoOfWords);

        }
        return words;
    }

    public static String  getParagraph(String[] words, int index){

        //10 words after the current word and 10 from before it
        final int range = 10;
        int begin = Math.max(0, index - range);
        int end = Math.min(words.length, index + range);

        String result ="";

        for (int i = begin; i < end; i++)
                result=result+ words[i]+" ";

        return result.toString();
    }




}
