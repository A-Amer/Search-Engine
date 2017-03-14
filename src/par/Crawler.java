package par;

import org.jsoup.*;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.*; 
import java.util.logging.Level;
import java.util.logging.Logger;
class HtmlText{
    public String Plain;
    public String Headers;
    public String Bold;
    public String Title;
    public String Url;
}

public class Crawler
{
static Queue<String> Seeds= new LinkedList<>();
static Set<String> VisitedUrls= new HashSet<>(); 
static Queue<HtmlText> IndexQ= new LinkedList<>();



protected boolean checkRobot(String URL) // when to check ?
{return true; }

protected boolean updateRobotList()
{return false;} 

	

public static void main(String[] args){
     
     Seeds.add("https://en-maktoob.yahoo.com/");
     //Seeds.add("https://en.wikipedia.org/wiki/March_2017_Kabul_attack");
     new Thread(new L(Seeds,VisitedUrls,IndexQ)).start();
     new Thread(new L(Seeds,VisitedUrls,IndexQ)).start();
  }

}
class L implements Runnable{
    Queue<String> Seeds;
    Set<String> VisitedUrls;
    Queue<HtmlText> IndexQ;
    final String UserAgent="Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1"; 

 final int StoppingCriteria=5000; 
 public L(Queue<String> seed,Set<String> visited,Queue<HtmlText> indexQ){
     Seeds=seed;
     VisitedUrls=visited;
     IndexQ=indexQ;
 }
    @Override
    public void run() {
       String URL; Document Retrieved; 
	
	while (VisitedUrls.size()<StoppingCriteria)
{ 
  synchronized(Seeds){
  URL= getNextUrl();  /// what if the queue is empty? -->wait and notify
  }
  Retrieved=getHTMLDocument(URL);
  URL=URL.replaceFirst("www.", "");
  VisitedUrls.add(URL);
  if (Retrieved!=null)
  {System.out.println("Retrieved");
 ( new  Parse()).ReadFile(Retrieved, Seeds, VisitedUrls,IndexQ);
  }
  else
  {System.out.println("Couldn't be retrieved");}
}
    }
    protected String getNextUrl()
{ // check that the queue is not empty 
	if (Seeds.isEmpty())
		return null; // Check that this URL is not in the set--> No need bec it's done b4 enqueuing   
      return Seeds.remove(); // 
      

}
protected Document getHTMLDocument(String URL) 
{    Document HTMLdoc=null; 
	try {
	Connection connection=Jsoup.connect(URL).userAgent(UserAgent);
        connection.followRedirects(true);
        Connection.Response r= connection.timeout(10000).execute(); 
        if (!r.contentType().contains("text/html")) // eh lzmteha ?
        {return null;}
        if(r.statusCode()>299)
        {return null;}
        HTMLdoc=connection.get();
	    } 
	catch(HttpStatusException e)
	{ 
           e.printStackTrace();
           return null;
        } catch (IOException ex) {
        Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
    }
	return HTMLdoc; 
}

}


