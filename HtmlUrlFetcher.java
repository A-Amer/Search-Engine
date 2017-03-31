package Engine;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 *
 * @author AAmer
 */
public class HtmlUrlFetcher implements Runnable {
   Document Html;//document dequed from DocQ
   final int max=20;//Maximum number of pages from the same domain
    HtmlUrlFetcher(Document doc){
        Html=doc;

    }
    @Override
    public void run(){
        String CheckString;
        int SameDomain=0;
        int out=0;
        InOutDeg Current;
        boolean PresentCheck;
        String domain="";
        
       try {
           domain=new URI(Html.location()).getAuthority();//get the domain of the current page
       } catch (URISyntaxException ex) {
           Logger.getLogger(Parse.class.getName()).log(Level.SEVERE, null, ex);
       }
        Elements NewURLs=Html.select("a");//get all urls in page 
        for (Element URL : NewURLs) {
            
            CheckString=URL.attr("abs:href").toLowerCase();//get the absolute path of each url .Lower cae to avoid duplicates
            
            if(CheckString.contains("#")||CheckString.isEmpty()||
                    Executer.RestrictedSites.contains(CheckString)||
                    "Yippy".equals(URL.ownText()/*to be removed*/)){
                continue;
            }/*if the url is empty,refers to a restricted site,refers to the same page
            we will ignore it*/
            if(CheckString.contains(domain)){//if the url is from the same domain as the current
                if(SameDomain>max)//if the maximum no. of urls from the same domain was reached ignore url
                    continue;
                SameDomain++;
                            
            }
            if(!CheckString.endsWith("/")){//to avoid duplicates
               CheckString= CheckString.concat("/");
            }
 
            synchronized(Executer.PageDegree){
                   
                   PresentCheck=(Executer.PageDegree.containsKey(CheckString));
                   //check if the seed was crawled before or already in seed queue
               }

            if(!PresentCheck&&Executer.IndexedPages<=Executer.StoppingCriteria 
                    && Executer.interruptFlag!=-1){
                //add to the seed queue if not present and stopping condition was not reached
                synchronized(Executer.Seeds){
                Executer.Seeds.add(CheckString);
                }
         
          }
            synchronized(Executer.PageDegree){
             Current=Executer.PageDegree.get(CheckString);
            }//get the current in degree of the page if its present else create new
            
            if(Current==null){
                    Current=new InOutDeg();
                    
                }
                Current.inDeg+=1;//increase the in degree of the url by one
                synchronized(Executer.PageDegree){
                Executer.PageDegree.put(CheckString, Current);
                }
                out++;//increase the count of the out degree of the current page
        }
        Current=Executer.PageDegree.get(Html.location());
        if(Current==null){
            Current=new InOutDeg();
        }
       
       Current.outDeg=out;
       synchronized(Executer.PageDegree){
       Executer.PageDegree.put(Html.location(),Current);//add the out degree of the current page
       }
    
    }
}
