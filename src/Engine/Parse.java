package Engine;

import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.*;

public class Parse implements Runnable {

    static final String REMOVED_STRING = "an|and|are|as|at|be|by|but|for|from|if|"
            + "in|into|is|it|its|no|not||on|or|of|where|which|"
            + "such|that|the|their|then|there|these|they|this|to|was|were|will|[a-z]";///stop words

    Document Html;//document dequed from DocQ

    Parse(Document doc) {
        Html = doc;

    }

    @Override
    public void run() {
        
        String txt="";
        if(Html.body() != null )
            txt=Html.body().text().toLowerCase();
        if (!"".equals(txt)&& !Html.title().contains("??")) {//run only if body is not emptu
            HtmlText Parsed = new HtmlText();
            Parsed.Headers = Html.body().select("h1,h2,h3,h4").text().toLowerCase().replaceAll("'|`|’", "")
                    .replaceAll("[^a-zA-Z]", " ")
                    .replaceAll("\\b(" + REMOVED_STRING + ")\\b\\s+", " ");/*get the headers change it to lower case
        and remove all non alphabet characters and stopwords
             */
            Parsed.Plain = Html.select("p,li,tbody,table,b,strong,h5,h6").text();
            Parsed.Plain = Parsed.Plain.toLowerCase().replaceAll("'|`|’", "").replaceAll("[^a-zA-Z]", " ")
                    .replaceAll("\\b(" + REMOVED_STRING + ")\\b\\s+", " ");
            //getting text that is in body but not headers and parse it
            Parsed.Bold = Html.body().select("b,strong").text();
            Parsed.Bold = Parsed.Bold.toLowerCase().replaceAll("'|`|’", "").replaceAll("[^a-zA-Z]", " ")
                    .replaceAll("\\b(" + REMOVED_STRING + ")\\b\\s+", " ");
            //get bold text and parse it
            Parsed.Bold = StringUtil.normaliseWhitespace(Parsed.Bold);
            Parsed.Headers = StringUtil.normaliseWhitespace(Parsed.Headers);
            Parsed.Plain = StringUtil.normaliseWhitespace(Parsed.Plain);
            //remove any extra whitespaces
            Parsed.Title = Html.title().toLowerCase().replaceAll("\\b(" + REMOVED_STRING + ")\\b\\s+", " ").replaceAll("'|`|’", "").replaceAll("[^a-zA-Z]", " ");
            Parsed.Title = StringUtil.normaliseWhitespace(Parsed.Title);
            //get the title of page
            Parsed.Url = Html.location();
            //get url of page
            IndexerManager.m.AddText(Parsed.Url,txt, Html.title());
            synchronized (Executer.IndexQ) {
                Executer.IndexQ.add(Parsed);
                //enqueue parsed text for indexer     
            }

        }

    }

}

class HtmlText {

    public String Plain;
    public String Headers;
    public String Bold;
    public String Title;
    public String Url;
}
