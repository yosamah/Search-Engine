import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class lastIndexHandler {

    static  public int ReadLastIndex(String lastIndexFilePath) throws Exception{
        File lastIndexedFile = new File(lastIndexFilePath);
        if(lastIndexedFile.createNewFile()){
            System.out.println("Created New file");
            FileWriter myWriter = new FileWriter(lastIndexFilePath);
            myWriter.write("0");
            myWriter.close();
        }

        Scanner LIsc = new Scanner(lastIndexedFile);
        int lastIndex=LIsc.nextInt();
        return  lastIndex;

    }

    static  public void WriteLastIndex(String lastIndexFilePath, int lastIndex) throws  Exception{

        FileWriter myWriter = new FileWriter(lastIndexFilePath);
        System.out.println("===========================================================");
        System.out.println("Wrote " + lastIndex);
        myWriter.write(String.valueOf(lastIndex));
        myWriter.close();
    }
}
