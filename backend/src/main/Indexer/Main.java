import com.SearchEngine.SearchEngineApplication;
import org.springframework.boot.SpringApplication;

public class Main {


    public static void main(String[] args) {

        DBController dbController= DBController.getInstance();

        for(int i=0;i<20;i++){
            (new Thread(new Indexer())).start();
        }


    }

}
