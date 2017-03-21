
package par;

import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;


class DocData{
    public String OrderInDoc;
    public Integer Frequency;
}

public class Indexer implements Runnable{
    
    final private static double AllowedPercentage =1 ;
    final private static int PlainValue = 1;
    final private static int BoldValue = 10;
    final private static int HeaderValue = 100;
    final private static int TitleValue = 1000;
    
    
   
    
    public static boolean GetWordFreq(Map<String, DocData> map ,String TypeA , int ValueA )
    {
        String [] WordsA = TypeA.split(" ");
        String prefix = "";
        if(ValueA == TitleValue)
            prefix = "0;0;";
        if (ValueA == HeaderValue)
            prefix = "0;";
        if(ValueA != BoldValue)
        {
            int i = 1;
            for (String w : WordsA) 
            {
                DocData n = map.get(w);
                if (n==null)
                {
                    n =  new DocData ();
                    n.OrderInDoc =  prefix + Integer.toString(i);
                    n.Frequency = ValueA;
                }
                else
                {
                    n.OrderInDoc = n.OrderInDoc+","+Integer.toString(i);
                    n.Frequency += ValueA;
                }

                map.put(w, n);
                i++;
            }
            if(ValueA == PlainValue)
            {
                for (Object name: map.keySet())
                {
                    if((((float) map.get(name).Frequency)/(float)WordsA.length)>=AllowedPercentage)
                        return true; 
                }
            }
            else if (ValueA != TitleValue)
            {
                map.keySet().forEach((name) -> {
                    map.get(name).OrderInDoc +=";";
                });
            }
        }
        else
        {
            for (String w : WordsA) 
            {
                if(w.isEmpty())
                    continue;
                DocData n = map.get(w);
                n.Frequency += ValueA;
                map.put(w, n);
            }
        }

        return false;
    }
    
    public static boolean WeightOfWords(DBmanager manager) throws InterruptedException
    {
        Map<String, DocData> map = new HashMap<>();
        HtmlText DocInfo;
     
            if(Executer.IndexQ.isEmpty()){
               // System.out.println("Empty Q");
                return false; 
            }
            
            synchronized(Executer.IndexQ)
        {
             DocInfo = Executer.IndexQ.remove();
        }
        
        if(GetWordFreq(map,DocInfo.Plain,PlainValue))
        {
           if(InsertSpam(manager,DocInfo.Url))
               System.out.println("\nURL Inserted as Spam ");
           return true; 
        }
        try{
        GetWordFreq(map,DocInfo.Bold,BoldValue);}
        catch(NullPointerException n){
            System.out.println(DocInfo.Url);
           
            return false;
        }
        GetWordFreq(map,DocInfo.Headers,HeaderValue);
        GetWordFreq(map,DocInfo.Title,TitleValue);
        return (InsertInDB(DocInfo.Url,manager,map));
            
    }
    
     public static boolean InsertSpam(DBmanager manager, String url )
    {
        if (manager.DeleteUrlKeyword(url))
        {
            System.out.println("\nDeleted from Spam Succesfully");
            return (manager.InsertSpam(url));    
        }
        return false;     
    }
     
    public static boolean InsertInDB(String url ,DBmanager manager ,Map<String, DocData> map)
    {
        if(manager.DeleteUrlKeyword(url))
        {
            System.out.println("\nDeleted Succesfully");
            if (manager.InsertUrlKeyword(url,map))
                System.out.println("\nInserted keywords Succesfully");           
        }
        return false;     
    }
    
    
    @Override
    public void run() {
        Map<String, DocData> map = new HashMap<>();
        while(true){
            try {
                WeightOfWords(IndexerManager.m);
            } catch (InterruptedException ex) {
                Logger.getLogger(Indexer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
