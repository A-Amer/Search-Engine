package Engine;

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CrawlerManager implements Runnable {

   
    int nThreads;

    static protected boolean userTerminates;

    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    CrawlerManager() {
        userTerminates = false;

    }

    ; 
  
 
    @Override
    public void run() {
        Thread.currentThread().setName("CrawlerManager");
        System.out.println("Enter the number of crawler threads: ");
        try { 
           nThreads= Integer.parseInt(reader.readLine());
       } catch (IOException e) {
           Logger.getLogger(CrawlerManager.class.getName()).log(Level.SEVERE, null, e);
       }

        Thread[] CrawlerThreads = new Thread[nThreads];
        System.out.println("If you wish to stop the crawler: press any key");
        for (int i = 0; i < nThreads; i++) {
            CrawlerThreads[i] = new Thread(new Crawler());
            CrawlerThreads[i].setName("T" + i);
            CrawlerThreads[i].start();
        }

        try {
      
            while (!reader.ready()) {
                if (Executer.IndexedPages >= Executer.StoppingCriteria) {
                    break;
                }

            }

            if (Executer.IndexedPages < Executer.StoppingCriteria) // if crawler finished , it should interrupt input and clear db 
            {
                DBmanager DB = new DBmanager();
                userTerminates = true;
                System.out.println("Backing up seeds");
                synchronized (Executer.Seeds) {
                    Executer.Seeds.forEach((s) -> {
                        DB.InsertSeed(s);
                    });
                };

                synchronized (Executer.PageDegree) {
                    Executer.PageDegree.keySet().forEach((s) -> {

                        DB.InsertPagesInOut(s, Executer.PageDegree.get(s).inDeg,
                                Executer.PageDegree.get(s).outDeg);
                    });
                    DB.CloseConnection();

                }

            }
        } catch (IOException err) {
            System.out.println("Error");
        } finally {
            for (int i = 0; i < nThreads; i++) {
                try {
                    CrawlerThreads[i].join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(CrawlerManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            Executer.CrawlerEnd = 1;

        }
    }
}
