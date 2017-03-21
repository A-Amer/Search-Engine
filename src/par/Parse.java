
package par;


import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.*;
import org.jsoup.safety.Whitelist;


public class Parse implements Runnable{
   static final String REMOVED_STRING="an|and|are|as|at|be|by|but|for|from|if|in|into|is|it|its|no|not||on|or|of|"
           +"such|that|the|their|then|there|these|they|this||to|was|were|will|[a-z]";
   Whitelist l=new Whitelist();
   Document Html;
   
    Parse(Document doc){
        Html=doc;
       
    }
    @Override
    public void run()  {
        l=Whitelist.none();
       
       if(Html.body()!=null){
        HtmlText Parsed=new HtmlText(); 
        Parsed.Headers=Html.body().select("h1,h2,h3,h4").text().toLowerCase().replaceAll("'|`|’", "").replaceAll("[^a-zA-Z]", " ")
                      .replaceAll("\\b("+REMOVED_STRING+")\\b\\s+", " ");
        Parsed.Plain= Html.select("p,li,tbody,table,b,strong").text();
       Parsed.Plain= Parsed.Plain.toLowerCase().replaceAll("'|`|’", "").replaceAll("[^a-zA-Z]", " ")
                     .replaceAll("\\b("+REMOVED_STRING+")\\b\\s+", " ");
        
        Parsed.Bold= Html.body().select("b,strong").text();
        Parsed.Bold=Parsed.Bold.toLowerCase().replaceAll("'|`|’", "").replaceAll("[^a-zA-Z]", " ")
                .replaceAll("\\b("+REMOVED_STRING+")\\b\\s+", " ");
        
        Parsed.Bold=StringUtil.normaliseWhitespace(Parsed.Bold);
        Parsed.Headers=StringUtil.normaliseWhitespace(Parsed.Headers);
        Parsed.Plain=StringUtil.normaliseWhitespace(Parsed.Plain);
        
        Parsed.Title=Html.title().toLowerCase().replaceAll("\\b("+REMOVED_STRING+")\\b\\s+", " ").replaceAll("'|`|’", "").replaceAll("[^a-zA-Z]", " ");
        Parsed.Title=StringUtil.normaliseWhitespace(Parsed.Title);
        Parsed.Url=Html.location();
        
        synchronized(Executer.IndexQ){
          Executer.IndexQ.add(Parsed);
              
    
        }
        //System.out.println(Executer.IndexQ.size());
        
       }
       

    }
  
}
