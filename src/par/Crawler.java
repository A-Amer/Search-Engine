package par;

import java.io.BufferedInputStream;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import java.net.*; 
import java.io.*; 
import java.util.*; 
import java.util.logging.Level;
import java.util.logging.Logger;
//import par.Parse;
public class Crawler implements Runnable 
{

ArrayList<String> Disallowed= new ArrayList<>(); 
ArrayList<String> Allowed= new ArrayList<>(); 

final static private  Object o1= new Object();
final static private Object o2=new Object(); 


final String UserAgent="Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1"; 

final int StoppingCriteria=5000; 


protected String getNextUrl()
{ // check that the queue is not empty  
    synchronized (o1) 
    {if (Executer.Seeds.isEmpty())
		return null; // Check that this URL is not in the set--> No need bec it's done b4 enqueuing   
      return Executer.Seeds.remove(); // 
    } 

}

protected boolean EnqueueDocument(Document D)
{ 
 synchronized(Executer.DocQ){ 
    Executer.DocQ.add(D);
 } 
  return true; 
}
protected boolean getHTMLDocument(String URL) 
{    Document HTMLdoc=null; 
	try {

	Connection connection=Jsoup.connect(URL).userAgent(UserAgent);
        Connection.Response r= connection.timeout(100000).execute(); 
        if (!r.contentType().contains("text/html")) // eh lzmteha ?
        {return false;}
        if(r.statusCode()>299)
        {return false;}
        HTMLdoc=connection.get();
	    } 
	catch(HttpStatusException e)
	{ 
           e.printStackTrace();
           return false;
        } catch (IOException ex) {
        Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
    }
        
        catch(IllegalArgumentException ex){
            return false;
        }
	return (EnqueueDocument(HTMLdoc)); 
}

protected boolean checkRobot(String URL) // when to check ?
{  String RobotURL=URL;  URL Roboturl=null;  URL urlToCheck=null; 
	try
    { RobotURL=getRobotsURL(URL);
      urlToCheck = new URL(URL); 
    } 
    
        
	catch(MalformedURLException m) 
	{//System.out.println(Thread.currentThread().getName()+" Wrong URL");
         return false; 
        }// to be removed 
        
    // Check if this list has been downloaded -->NO 
       
    try { 
        Roboturl= new URL(RobotURL);  //automatically verifies that URL exists 
         } 
    catch (MalformedURLException ex)
    {
        //System.out.println(Thread.currentThread().getName()+ "Invalid Robots URL");
        return false; 
    }
         // Iterate through list and check allowed or disallowed 
    
    if (!updateRobotList(Roboturl,Disallowed , Allowed)) return false;   
    String file = urlToCheck.getFile(); // gets the directory we are searching for
    
   for (String s:Allowed)
    {  if ((file.compareToIgnoreCase(s)== 0)) 
       {//System.out.println(Thread.currentThread().getName()+" due to "+s+"  your URL is allowed !!");
        return true; 
        }
    } 
         
    for (String s:Disallowed)
    {  if (file.startsWith(s)) 
        { //System.out.println(Thread.currentThread().getName()+" due to "+s+"  your URL is disallowed !!");
          return false; }
    } 
   
    Disallowed.clear();
    Allowed.clear();
    return true ; 

}      
         
	


protected boolean updateRobotList(URL urlRobot , ArrayList<String> disallowedURLS ,ArrayList<String> allowedURLS  ) //  must make sure that this is for our USerAgent 
{   
    HttpURLConnection c;
    
    try//  must make sure that this is for our USerAgent 
{  
    c = (HttpURLConnection)urlRobot.openConnection();
    c.addRequestProperty("User-Agent", "Mozilla/4.76");
} 
    catch (IOException e ){//System.out.println(Thread.currentThread().getName()+ " connection error!");
        return false;}
    
    try
{        
    BufferedReader reader= new BufferedReader(new InputStreamReader(c.getInputStream()));
    String  RobotTxt= new String(); 
    String path;
    
    while ((RobotTxt=reader.readLine())!=null) //loop until we reach the desired user agent
    {if (RobotTxt.startsWith("User-agent: *"))   break; }
    
    while ((RobotTxt=reader.readLine())!=null)
    {
        if (RobotTxt.startsWith("User"))   break;
        
               if (RobotTxt.indexOf("Disallow:")==0)
   {path=RobotTxt.substring("Disallow:".length());
    int commentIndex;
    commentIndex = path.indexOf("#");
        if (commentIndex != - 1)
    {path =path.substring(0, commentIndex);}
     path=path.trim();
//        System.out.println("Disallowed  "+path);
    if (path!=null)  // if null it allows full access 
    disallowedURLS.add(path);
    }
              else if (RobotTxt.indexOf("Allow:")==0)
    {path=RobotTxt.substring("Allow:".length());
     int commentIndex;
     commentIndex = path.indexOf("#");
        if (commentIndex != - 1)
    {path =path.substring(0, commentIndex);}
     path=path.trim();
//         System.out.println("Allowed  "+path);
     allowedURLS.add(path);
    }
    }
    reader.close(); //Closes buffer and releases memory resources
    }
catch (IOException ex){ 
    // System.out.println("Robots File not found!"); /// if robot file not found ; full crawling is allowed 
     return true; 
}
 return true;    
} 
protected String getRobotsURL(String url) throws MalformedURLException
{
  URL main= new URL(url); 
  StringBuilder URLtemp= new StringBuilder(url); 
  int i = URLtemp.indexOf("://"); // must maintain the protocol 
  URLtemp=URLtemp.delete(i+3 , URLtemp.length()); 
  URLtemp.append(main.getHost()); 
  URLtemp.append("/robots.txt"); 
  
  
 // System.out.println(Thread.currentThread().getName()+"This is the our Robot: " +URLtemp); 
//  URL main= new URL(url); 
//  String URLtemp= main.getHost(); 
//  URLtemp=url.replaceAll(URLtemp,(URLtemp+"/robots.txt"));
//  int m= URLtemp.indexOf("robots.txt"); 
//  URLtemp=URLtemp.substring(0, m+10);  
//  System.out.println(Thread.currentThread().getName()+"reached domain:  "+ URLtemp);
  return URLtemp.toString(); 
  
  }
protected void Crawl() 
{
   String URL = null; 
boolean Retrieved = false; 
boolean RobotAllow = false; 
   while (Executer.VisitedUrls.size()<StoppingCriteria)//will change this with counter
{  
    URL=null ;
    Retrieved=false ; RobotAllow=false; 
    URL= getNextUrl();  /// what if the queue is empty? -->wait and notify
    
    if (URL!=null)
  {  // System.out.println( Thread.currentThread().getName()+ " Retrieved the following URL: "+URL); 
      RobotAllow=checkRobot(URL);
  }
  
  if (URL!=null && RobotAllow) 
  {
   synchronized(o2){
   Executer.VisitedUrls.add(URL);} 
  // Parse.ReadFile(Retrieved, CrawlerManager.Seeds, CrawlerManager.VisitedUrls)
   Retrieved=getHTMLDocument(URL);
  
  }
  
  if (URL!=null && RobotAllow && Retrieved)
  {
   //System.out.println(Thread.currentThread().getName()+"crawled the URL "+URL+" allowed");
  
  } }
}

@Override
public void run()
{
    
Crawl(); 


}
}
/*
public static void main(String[] args){

try {
        Crawler c=new Crawler();
//     c.Seeds.add("https://en-maktoob.yahoo.com/");
//     c.Crawl();
URL fb= new URL("https://en.wikipedia.org/robots.txt"); 
// ArrayList<String> Disallowed= new ArrayList<String>(); 
//         ArrayList<String> Allowed= new ArrayList<String>(); 
c.checkRobot("https://github.com/A-Amer/Search-Engine/blob/master/src/par/DBmanager.java");
    } catch (MalformedURLException ex) {
        Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
        System.out.println("par.Crawler.main()");
    }

     
  }
*/ 
