package Query;
import static Query.QProcessor.Idfs;
import java.lang.Object; 
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import org.apache.commons.lang3.ArrayUtils;
//TO DO: 
//CHECK ARRAY LENGTH --> check if not empty 

public class Ranker {
 
    static protected LinkedList<String> RankedResult; 
    static int size; 
    
    public static void  Rank(String Query[])
    {      
        size=0; 
        
// get list of weight = 2 . 
         ArrayList<LinkedList<UrlInfo>> ResultExact= new ArrayList<>(Query.length); 
         LinkedList<UrlInfo> Exact;
// iterate on result set 
         for(int i=0;i<Query.length;i++)
         {
//  for the linked list : iterate on it and search for the 
            ResultExact.add(i , new LinkedList<>()); 
            if(QProcessor.Result.get(i)==null) continue; 
           for (UrlInfo ui :QProcessor.Result.get(i)) 
         {
             if (ui.weight==2)
         {ResultExact.get(i).add(ui);} 
         }
        size+=QProcessor.Result.get(i).size(); 
         }
         RankedResult= new LinkedList<String>(); 
 
// Find common URLs in all  if query is more than 1 word 
boolean foundinside=false; 
boolean foundinall=false; 
 if (Query.length>1)
 {  
    Exact= new LinkedList<UrlInfo>(); 
       for(UrlInfo ui:ResultExact.get(0)) 
       {
           foundinall=true;   
            for(int i=1; i<Query.length ; i++)   ///////////////////// Search for a way to do it with AND 
           {    foundinside=false; 
                if (ResultExact.get(i)==null) continue; 
                for (int j=0; j<ResultExact.get(i).size() ; j++)
                     { 
                        if (ResultExact.get(i).get(j).URL.compareToIgnoreCase(ui.URL)==0)
                           { foundinside=true; // this list has the url 
                                break ;
                           } 
                     }
                if(!foundinside)
                {   foundinall=false; 
                    break;}
                
           }
              if( foundinall )   /// if one of the lists doesn't hae this url -> break  
              Exact.add(new UrlInfo(ui));
           }
           
 }
 else 
 {
 Exact= new LinkedList<UrlInfo>(ResultExact.get(0)); 
 }
// now exact has the list of highest priority: the links which have the exact words of all query words  and QProcessor.Result is cleared from these urls

ResultExact.clear(); 

// compare idf -->get index of highest n/2 words 
double div= Query.length/(2.0); 
int n=(int)Math.ceil(div);   
int topIdfs[]=new int[n]; 

for (int i =0 ; i < topIdfs.length ; i++)
   { topIdfs[i]=getiMax(QProcessor.Idfs , Query);}
        
// get the common links which have any of the stems of the top n/2 words 
LinkedList <UrlInfo> TopIdfList = new LinkedList<>();
String CheckUrl;

if(topIdfs.length>1)
{   
    for(UrlInfo ui: QProcessor.Result.get(topIdfs[0])) 
        {    foundinside=false; 
             foundinall=true; 
             CheckUrl=ui.URL;
            for(int i=1 ; i<topIdfs.length ; i++)   //////loop on all lists 
           {    // if this list is empty continue
                 foundinside=false; 
                if (QProcessor.Result.get(topIdfs[i])==null) continue; 
               
                for (int j=0 ; j<QProcessor.Result.get(topIdfs[i]).size(); j++)  //loop on one list 
                {     
                    if (CheckUrl.compareToIgnoreCase(QProcessor.Result.get(topIdfs[i]).get(j).URL)==0)
                        {foundinside=true; 
                         break;
                        }
                }
                    if(!foundinside) 
                    {foundinall=false;
                     break; 
                    }
            }
        if(foundinall)
             TopIdfList.add(new UrlInfo(ui)); 
        } 
        }
else 
{TopIdfList.addAll(QProcessor.Result.get(topIdfs[0]));}

// shuffle chunks according to popularity: authorities first
//Compares its two arguments for order. Returns a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
Collections.sort(Exact , (UrlInfo u1, UrlInfo u2) -> {
    if (u1.frequency<u2.frequency) return 1 ;
    if(u1.frequency>u2.frequency) return -1 ;
    if (u1.pin<u2.pin) return 1 ;
    if(u1.pin>u2.pin) return -1 ;
    return 0;
         });

Collections.sort(TopIdfList , (UrlInfo u1, UrlInfo u2) -> {
    if (u1.frequency<u2.frequency) return 1 ;
    if(u1.frequency>u2.frequency) return -1 ;
    if (u1.pin<u2.pin) return 1 ;
    if(u1.pin>u2.pin) return -1 ;
    return 0;
         });


// add the URLS to the  to RankedResult 
for (int i = 0 ; i < Exact.size() ; i++)
{ RankedResult.addLast(Exact.get(i).URL);}



for (int i = 0 ; i < TopIdfList.size() ; i++)
{   if (!RankedResult.contains(TopIdfList.get(i).URL))
    RankedResult.addLast(TopIdfList.get(i).URL);}

//get the rest of the URLs from Q.Result
LinkedList <UrlInfo> Remaining = new LinkedList<>();
 
if(QProcessor.Result.size()>1)
{
for (int i=1 ; i<QProcessor.Result.size() ; i++)
{ 
Remaining.addAll(QProcessor.Result.get(i));
}
}
// for memory purposes 
QProcessor.Result.clear(); 

Collections.sort(Remaining , (UrlInfo u1, UrlInfo u2) -> {
        if (u1.pin<u2.pin) return 1 ;
    if(u1.pin>u2.pin) return -1 ;
    return 0;
         });
      
for (int i = 0 ; i < Remaining.size(); i++)
{   if (!RankedResult.contains(Remaining.get(i).URL))
    RankedResult.addLast(Remaining.get(i).URL);

}


System.out.println("Printing Exact: ");  
System.out.println(Exact.toString()); 
System.out.println("Printing Top: "); 
System.out.println(TopIdfList.toString());
System.out.println("Printing Remaining: "); 
System.out.println(Remaining.toString());
System.out.println("Printing Ranked Result: "); 
System.out.println(RankedResult);

// remember to clear exact , remaining , top

}    
    
    
    public static void RankPhrase()
    {
        
    Collections.sort(QProcessor.Result.get(0) , (UrlInfo u1, UrlInfo u2) -> {
    if (u1.pin<u2.pin) return 1 ;
    if(u1.pin>u2.pin) return -1 ;
    return 0;
         });
   
    
    }
static int getiMax(float arr[] , String[] Query)
    { int max=0;
     for(int i=1;i<arr.length;i++)
        {
            if(arr[i]>arr[max]) 
                max=i;
        }
     float removed=arr[max];
     
     arr = ArrayUtils.removeElement(arr, removed);
     Query=ArrayUtils.removeElement(Query, Query[max]); 
     return max; 
    
   }
    
//    boolean SortByPopularity (LinkedList<UrlInfo> list)
//    { //shell insertion sort-> O(N^0.75) and no extra storage needed 
//        for (int k=list.size()/2 ; k>0 ; k/=2)
//            
//        
//        
//        return true; }
}