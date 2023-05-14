import java.util.List;

public class Tokenizer {



    static String stopWordsFilePath="./backend/src/main/Indexer/StopWords.txt";
    static List<String> StopWords;
    static  String StopWordsRegex;


    public Tokenizer(){
        try {
            StopWords = stopWordsReader.ReadStopWords(stopWordsFilePath);
            StopWordsRegex=String.join("",StopWords);
//            System.out.println(StopWordsRegex);

        }catch (Exception e){

        }
//        StopWordsRegex="(";
//        for(int i=0;i <StopWords.size();i++)
//        {
//            if(i!=StopWords.size()-1){
//                //notice the ingrained space here
//                StopWordsRegex+=StopWords.get(i)+"|";
//            }
//            else {
//                //notice the ingrained space here
//                StopWordsRegex+=StopWords.get(i)+") ";
//            }
//
//        }
    }

    static  public  boolean isStopWord(String word){

        return StopWordsRegex.contains(word);

    }
    //writes tokens to DB and returns them for use
    static public String[] writeTokens(String Text, String currentSite, String placeOfOccurrence) {
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

            //TODO: write to DB here
            DBController.addSiteToWord(word,wordParagraph,placeOfOccurrence,currentSite);

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
