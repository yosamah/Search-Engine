

import Collections.Site;
import Collections.Word;
import Repositories.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class  WordServiceImpl implements WordService{

    @Autowired
    private WordRepository wordRepository;

    @Override
    public Word findWord(String word){
        return wordRepository.findByWord(word);
    }

//    public void saveWord(Word word);

}
