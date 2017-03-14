
package par;

import java.util.Queue;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.*;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.*;

public class Parse {
   static String  CheckString;//string used to check if url was previously crawled 
   static final String RemovedString="a|an|and|are|as|at|be|by|but|for|from|if|in|into|is|it|its|no|not||on|or|of|"
           +"such|that|the|their|then|there|these|they|this||to|was|were|will";
   HtmlText Parsed;
    /**
     * @param doc   
     * @param urls   
     * @param crawled     
     * @param IndexQ     
     *    
     */
     public void ReadFile(Document doc,Queue<String> urls,Set<String>crawled,Queue<HtmlText> IndexQ )  {
         //Open db
         synchronized(urls){
           
        Elements NewURLs=doc.select("a[href]");//get all urls in page 
        for (Element URL : NewURLs) {
            CheckString=URL.attr("abs:href").toLowerCase().replaceFirst("www.", "");
            if("_blank".equals(URL.attr( "target)")))
                return;
               synchronized(crawled){
            if(!(crawled.contains(CheckString))&&!(urls.contains(CheckString))&& !CheckString.isEmpty()){
                urls.add(CheckString);
            }
               
           }
          
        }
         }
       if(doc.body()!=null){
        String AllBody=doc.getElementsByTag("li").text()+" "+doc.getElementsByTag("p").text()+" "
                +doc.getElementsByTag("table").text()+" "+doc.getElementsByTag("dl").text();
        AllBody+="ASD"+doc.getElementsByTag("h1").text()+doc.getElementsByTag("h2").text()+"ASD"+doc.getElementsByTag("b").text()+"D";
        AllBody=AllBody.toLowerCase().replaceAll("[^a-zA-Z' ]", " ")
                .replaceAll("\\b("+RemovedString+")\\b\\s+", " ");
        AllBody=StringUtil.normaliseWhitespace(AllBody);
        String[] ConcatStr=AllBody.split("as");
        Parsed=new HtmlText();
        Parsed.Plain=ConcatStr[0];
        Parsed.Headers=ConcatStr[1].replaceFirst("d", "");
        Parsed.Bold=ConcatStr[2].replaceFirst("d", "");
        Parsed.Title=doc.title().toLowerCase().replaceAll("\\b("+RemovedString+")\\b\\s+", "");
        Parsed.Url=doc.location();
        IndexQ.add(Parsed);
       }
       

    }
  
}
