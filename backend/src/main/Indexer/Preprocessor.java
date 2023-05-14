import java.util.ArrayList;
import java.util.List;

public class Preprocessor {

    static String preprocess(String Text){
        Text = Text.toLowerCase();
        // Remove Stop Words
//        Text= Text.replaceAll(StopWordsRegex," ");
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
