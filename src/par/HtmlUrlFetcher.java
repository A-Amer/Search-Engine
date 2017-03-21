package par;

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
   Document Html;
  
    HtmlUrlFetcher(Document doc){
        Html=doc;

    }
    @Override
    public void run(){
        String CheckString;
        int SameDomain=0;
        String domain="";
        int max=15;
       try {
           domain=new URI(Html.location()).getHost();
       } catch (URISyntaxException ex) {
           Logger.getLogger(Parse.class.getName()).log(Level.SEVERE, null, ex);
       }
        Elements NewURLs=Html.select("a");//get all urls in page 
        for (Element URL : NewURLs) {
            CheckString=URL.attr("abs:href");
            if(CheckString.contains("#")){
                continue;
            }
            if(CheckString.contains(domain)){
                if(SameDomain>max)
                    continue;
                SameDomain++;
                            
            }
            if( (CheckString.startsWith("https")||CheckString.startsWith("http"))
                    &&!CheckString.isEmpty()&&(CheckString.contains("www.")||CheckString.contains("en"))){
               CheckString=CheckString.replaceFirst("www.", "");
            if(!(Executer.VisitedUrls.contains(CheckString))&&!(Executer.Seeds.contains(CheckString))){
                Executer.Seeds.add(CheckString);
            }
               
           
          }
        }
    
    }
}
