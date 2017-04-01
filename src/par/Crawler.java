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
                return null; // Check that this URL is not in the set--> No need bec it's done b4 enqueuing   
            }
            return Executer.Seeds.remove(); // 
        }

    }

    protected boolean enqueueDocument(Document D) {
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
        boolean RobotAllow = false;
        while (Executer.IndexedPages < Executer.StoppingCriteria && !CrawlerManager.userTerminates) {
            URL = null;
            Retrieved = false;
            RobotAllow = false;

            URL = getNextUrl();

            if (URL != null) {
                System.out.println(Thread.currentThread().getName() + " Retrieved the following URL: " + URL);
                RobotAllow = RobotsManager.checkRobot(URL);

                if (!RobotAllow) {
                    Indexer.InsertSpam(IndexerManager.m, URL);
                }
            }

            if (URL != null && RobotAllow) {

                Executer.PageDegree.putIfAbsent(URL, new InOutDeg());
                Retrieved = getHTMLDocument(URL);

            }
           

        }
        //  Executer.interruptCrawler();
    }

    @Override
    public void run() {

        Crawl();

    }
}
