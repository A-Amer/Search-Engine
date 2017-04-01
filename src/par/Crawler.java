package Engine;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import java.io.*;

public class Crawler implements Runnable {

    final String UserAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1"
            + "(KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";

    static RobotsChecker RobotsManager = new RobotsChecker();

    protected String getNextUrl() { // check that the queue is not empty  
        synchronized (Executer.Seeds) {
            if (Executer.Seeds.isEmpty()) {
                return null;   
            }
            return Executer.Seeds.remove(); // 
        }

    }

    protected boolean enqueueDocument(Document D) { // after retrieving the html doc , it's added to the doc Queue between crawler and parser 
        boolean IsAdded;
        synchronized (Executer.DocQ) {
            IsAdded = Executer.DocQ.add(D);
        }
        if (IsAdded) {
            System.out.println(Thread.currentThread().getName() + "Enqueued the doc");
            return true;
        }

        return false;

    }
// retrieve HTML doc 
    protected boolean getHTMLDocument(String URL) {
        Document HTMLdoc = null;
        try {

            Connection connection = Jsoup.connect(URL).userAgent(UserAgent);
            Connection.Response r = connection.url(URL).timeout(100000).execute();
            
            if (!r.contentType().contains("text/html")) {
                return false;
            }
            HTMLdoc = connection.get();
        } catch (HttpStatusException e) {
            Executer.RestrictedSites.add(URL);
            return false;
        } catch (IOException | IllegalArgumentException | NullPointerException ex) {
            return false;
        }
        return (enqueueDocument(HTMLdoc));
    }

    protected void Crawl() {
        String URL = null;
        boolean Retrieved = false;
        boolean RobotAllow = false;   // loop until the stopping criteria or user terminating the crawler 
        while (Executer.IndexedPages < Executer.StoppingCriteria && !CrawlerManager.userTerminates) {
            URL = null;
            Retrieved = false;
            RobotAllow = false;

            URL = getNextUrl(); // fetch the next URL in the queue 

            if (URL != null) {
                System.out.println(Thread.currentThread().getName() + " Retrieved the following URL: " + URL);
                RobotAllow = RobotsManager.checkRobot(URL);
  // if for any reason, it's not allowed to crawl this website, it's considered as a spam to avoid further checking
                if (!RobotAllow) {
                    Indexer.InsertSpam(IndexerManager.m, URL);
                }
            }
// if it's allowed to crawl the document , retrieve itd HTML Doc
            if (URL != null && RobotAllow) {

                Executer.PageDegree.putIfAbsent(URL, new InOutDeg());
                Retrieved = getHTMLDocument(URL);

            }
           

        }
     
    }

    @Override
    public void run() {

        Crawl();

    }
}
