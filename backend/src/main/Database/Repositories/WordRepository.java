package Repositories;

import Collections.Word;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


//A Repository is the interface between the application and the collection in the Database

// Repository of <Document, Datatype of ID>
@Repository
public interface WordRepository extends MongoRepository<Word,String> {

    //spring is smart enough to understand, no need to write query
    Word findByWord(String word);

}
