
import Collections.Word;
import org.springframework.stereotype.Component;

@Component
public interface WordService {

    Word findWord(String word);

}
