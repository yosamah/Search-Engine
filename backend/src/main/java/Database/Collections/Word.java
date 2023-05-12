package Database.Collections;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

// The schema of the Word collection in the Database


//@Data for setters and getters, it requires lombok
@Data
@Document(collection="Words") //mongoDB collection
@AllArgsConstructor // Constructor that takes all fields as arguements
@NoArgsConstructor  // Constructor that takes no arguemnts.. It's needed because we don't have a mongoID when first encountering a word
public class Word {

    @Id
    String WordID;

    String word;
    List<Site> Sites;
}
