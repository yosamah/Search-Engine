package Database.Collections;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;
@Data
@AllArgsConstructor // Constructor that takes all fields as arguements
public class Site {
    private List<Integer> indexOfOccurrence;
    private List<String> placeOfOccurrence;
    private float termFrequency;
    private int noOfOccurrences;
    private float pagePopularity;
    private boolean isSpam;
}
