import java.util.List;

public class Tokenizer {





    static public void writeTokens(String Text, String currentSite, String placeOfOccurrence) {
        String[] words = Text.split(" ");


        for (int i = 0; i < words.length; i++) {

            if (words[i]=="")
                continue;

            if (words[i].length()==1)
                continue;

            String word = words[i];
            String wordParagraph = getParagraph(words,i);

        }
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
