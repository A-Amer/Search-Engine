package Engine;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

class DocData {

    public String OrderInDoc;
    public Integer Frequency;
}

public class Indexer implements Runnable {

    final private static double AllowedPercentage = 0.4;
    final private static int PlainValue = 1;
    final private static int BoldValue = 2;
    final private static int HeaderValue = 4;
    final private static int TitleValue = 5;

    public static boolean GetWordFreq(Map<String, DocData> map, String TypeA, int ValueA , String prefix ) {
       
        String[] WordsA = TypeA.split(" "); //split the string into array of words
        int i = 1; // keeps track of the word order
        
        for (String w : WordsA) 
        {
            DocData n = map.get(w);
            if (n == null)  //if the word if not inserted in the map before
            {
                n = new DocData();
                n.OrderInDoc = prefix + Integer.toString(i); // first add the prefix which is determined by the string type
                n.Frequency = ValueA; // insert the value
            } 
            else { //if word exists
                n.OrderInDoc = n.OrderInDoc + "," + Integer.toString(i);//add to the prevoius order
                n.Frequency += ValueA; // increment the value
            }

            map.put(w, n); // insert in map
            i++;
        }
        if (ValueA == PlainValue) 
        {
            for (Object name : map.keySet()) 
            {//check for spam using word frequency
                if ((((float) map.get(name).Frequency) / (float) WordsA.length) >= AllowedPercentage) 
                    return true;               
            }
        } 
        if (ValueA != TitleValue) 
        {//add a ";" to differentiate between types in ordering
            map.keySet().forEach((name) -> {
                map.get(name).OrderInDoc += ";";
            });
        }
          
        return false;//if not spam
    }
    
    public static void GetBoldFreq(Map<String, DocData> map, String TypeA, int ValueA)
    {//no ordering for bold as it already exists in the plain text
     //only adding to the frequency
        String[] WordsA = TypeA.split(" ");
        for (String w : WordsA) {
                if (w.isEmpty()) {
                    continue;
                }
                DocData n = map.get(w);
                n.Frequency += ValueA;
                map.put(w, n);
            }
    }

    public static boolean WeightOfWords(DBmanager manager) throws InterruptedException {
        Map<String, DocData> map = new HashMap<>();
        HtmlText DocInfo;
        String prefix = "";

        try {
            synchronized (Executer.IndexQ) {

                DocInfo = Executer.IndexQ.remove();//remove Html Text from the queue

            }
        } catch (NoSuchElementException ex) {
            return false;
        }

        if (GetWordFreq(map, DocInfo.Plain, PlainValue,prefix)) { //map the plain text
            if (InsertSpam(manager, DocInfo.Url)) { // if spam insert into spam table
                System.out.println("\nURL Inserted as Spam ");
            }
            return true;
        }
        try {
            GetBoldFreq(map, DocInfo.Bold, BoldValue); //map the bold text
        } catch (NullPointerException n) {
            System.out.println(DocInfo.Url);

            return false;
        }
        prefix = "0;";//prepare prefix for header, only one zero since the order is plain;header;title
        GetWordFreq(map, DocInfo.Headers, HeaderValue,prefix);
        prefix = "0;0;";//prepare prefix for title
        GetWordFreq(map, DocInfo.Title, TitleValue,prefix);
        
        synchronized (Executer.IndexedPages) {//increment the counter used as indicator for stoping criteria
            Executer.IndexedPages++;
        }
        return (InsertInDB(DocInfo.Url, manager, map));//insert map into database

    }

    public static boolean InsertSpam(DBmanager manager, String url) {
        if (manager.DeleteUrlKeyword(url)) {
            System.out.println("\nDeleted from Spam Succesfully");
            return (manager.InsertSpam(url));
        }
        return false;
    }

    public static boolean InsertInDB(String url, DBmanager manager, Map<String, DocData> map) {
        if (manager.DeleteUrlKeyword(url)) {//delete all keyword related to this URL
            System.out.println("\nDeleted Succesfully");
            if (manager.InsertUrlKeyword(url, map)) {//then insert the updated version
                System.out.println("\nInserted keywords Succesfully");
            }
        }
        return false;
    }

    @Override
    public void run() {
        Map<String, DocData> map = new HashMap<>();//creat map
        while (Executer.ParserEnd != 1) {
            try {
                WeightOfWords(IndexerManager.m);//get keywords,frequency,order
            } catch (InterruptedException ex) {
                Logger.getLogger(Indexer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
