
package par;

import java.util.HashMap;
import java.util.Map;
import java.math.BigDecimal;
import java.sql.ResultSet;


public class Indexer {
    
    final private static double AllowedPercentage =0.2 ;
    final private static int PlainValue = 1;
    final private static int BoldValue = 10;
    final private static int HeaderValue = 100;
    final private static int TitleValue = 1000;
   
    
    public static boolean GetWordFreq(Map<String, Integer> map ,String TypeA ,  int ValueA ,String TypeB , int ValueB  )
    {
        String [] WordsA = TypeA.split(" ");
        String [] WordsB = TypeB.split(" ");
        float TotalWordsNumber = WordsA.length + WordsB.length ;

        for (String w : WordsA) 
        {
            Integer n = map.get(w);
            n = (n == null) ? ValueA : n+ValueA;
            map.put(w, n);
        }
        if(ValueA == PlainValue || ValueB == BoldValue)
        {
            for (Object name: map.keySet())
            {
                if((((float) map.get(name))/TotalWordsNumber)>=AllowedPercentage)
                    return true; 
            }
        }
        for (String w : WordsB) 
        {
            Integer n = map.get(w);
            n = (n == null) ? ValueB : n+ValueB;
            map.put(w, n);
        }
//        if(ValueA == PlainValue)
//        {
//            for (Object name: map.keySet())
//            {
//                if((((float) map.get(name))/TotalWordsNumber)>=AllowedPercentage)
//                    return true; 
//            }
//        }
        return false;
    }
    
    public static boolean InsertSpam(DBmanager manager, String url )
    {
        if (manager.DeleteUrlKeyword(url))
        {
            System.out.println("\nDeleted Succesfully");
            return (manager.InsertSpam(url));    
        }
        return false;     
    }
    
    public static boolean WeightOfWords(DBmanager manager,String url , String title, String header , String bold , String plain )
    {
        Map<String, Integer> map = new HashMap<>();
        
        if(GetWordFreq(map,plain,PlainValue,bold,BoldValue))
        {
           if(manager.InsertSpam(url))
               System.out.println("\nURL Inserted as Spam ");
           return true; 
        }
        
        GetWordFreq(map,header,HeaderValue,title,TitleValue);
        String doc = title+header+bold+plain;
        return (InsertInDB(url,doc,manager,map));
            
    }
    
    public static boolean InsertInDB(String url ,String doc ,DBmanager manager ,Map<String, Integer> map)
    {
        if(manager.DeleteUrlKeyword(url))
        {
            System.out.println("\nDeleted Succesfully");
            if (manager.InsertUrlKeyword(url,map))
            {
                System.out.println("\nInserted keywords Succesfully");
                if(manager.InsertUrl(url, doc))
                    System.out.println("\nInserted document Succesfully");             
            }
        }
        return false;     
    }
    
    
    public static void main(String[] args) {
        String s ="aminals cat ahmed bom a a cat cat dog";
        String a ="and a kiss from me to you";
        String p ="with a great big hug with a great big hug with a great big hug with a great big hug" ;
        String b = " we are a happy family";
        //String b ="";
        DBmanager m = new DBmanager ();
         WeightOfWords(m,"www.animals.com" , s, a , b , p );
        //m.DeleteUrlKeyword("www.animals.com");
//        
//        for (Object name: map.keySet()){
//
//            String key =name.toString();
//            String value = map.get(name).toString();  
//            System.out.println(key + " " + value);  
//            m.InsertUrlKeyword("www.sama.com",key,value);
//        }
        m.CloseConnection();
    }
    
}

