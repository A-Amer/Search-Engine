package Query;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import org.jsoup.helper.StringUtil;

public class QProcessor {
    static final String REMOVED_STRING = "an|and|are|as|at|be|by|but|for|from|if|"
            + "in|into|is|it|its|no|not||on|or|of|where|which|"
            + "such|that|the|their|then|there|these|they|this|to|was|were|will|[a-z]";///stop words
    static boolean IsPhrase;
    static  ArrayList<LinkedList<UrlInfo>> Result; 
    static  float Idfs[] ;
    static Snip DispList[];
    static int size;
    public static void Process(String Query){
        DBmanager Manager=new DBmanager();
        Queue<String> SortedUrl;
        Query=Query.toLowerCase();
        if(Query.startsWith("\"")&&Query.endsWith("\"")){
            IsPhrase=true;
        }
        else
            IsPhrase=false;
        Query=Query.replaceAll("'|`|’|\"","")
                    .replaceAll("[^a-zA-Z]"," ")
                    .replaceAll("\\b(" + REMOVED_STRING + ")\\b\\s+", " ");
        Query=StringUtil.normaliseWhitespace(Query);
        
        String[] NewQuery;
        
        NewQuery=Query.split(" ");
        Result=new ArrayList<>(NewQuery.length);
        Idfs=new float[NewQuery.length];
        if(IsPhrase){
               SortedUrl=Manager.ExactWord(Query); 
               size=SortedUrl.size();
               DispList=new Snip[size];
                int i=0;
                while(!SortedUrl.isEmpty()){
                    Snip s=Manager.GetSnipExact(Query, SortedUrl.remove());
                    DispList[i]=s;
                    i++;
                }
        }
        else{
            
            for(int i=0;i<NewQuery.length;i++){
                LinkedList<UrlInfo> Add=Manager.AllWord(NewQuery[i],i);
                Result.add(i,Add);
            }
            Ranker.Rank(NewQuery);
            SortedUrl=Ranker.RankedResult;
            size=SortedUrl.size();
            if(size>200)
                size=200;
            DispList=new Snip[size];
            int i=0;
            while(!SortedUrl.isEmpty()&& i<200){
            Snip s=Manager.GetSnip(Query, SortedUrl.remove());
            DispList[i]=s;
            i++;
          }
       }        
        Manager.CloseConnection();
}
    
}
 class UrlInfo{
    String URL;
    int pin;
    int pout;
    int weight;
    int frequency;
     public UrlInfo (UrlInfo ui)
    {
    URL=ui.URL;  // not sure if new or =
    pin= ui.pin; 
    pout=ui.pout; 
    weight=ui.weight;
    frequency=ui.frequency;     
    }
    
    public UrlInfo()
    {}
    @Override
    public String toString()
    {return("URL is "+URL+" ;no of pin "+pin+ " ;no of pout "+pout);}
}
class Snip{
    String url;
    String snippet;
    String title;
}
