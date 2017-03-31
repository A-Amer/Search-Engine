package Engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author AA
 */
     
    
   public class CrawlerManager implements Runnable {
   static protected BufferedReader cin= new BufferedReader(new InputStreamReader(System.in)); 
   static protected boolean Interrupt=false;
   int nThreads; 
    CrawlerManager(){}; 
   
    @Override
    public void run () {
        
        System.out.println("Enter the number of crawler threads: ");
       try { 
           nThreads= Integer.parseInt(cin.readLine());
       } catch (IOException ex) {
           Logger.getLogger(CrawlerManager.class.getName()).log(Level.SEVERE, null, ex);
       }
        Thread[] CrawlerThreads= new Thread[nThreads];  
         
        for (int i=0 ; i<nThreads ; i++ )
        {  CrawlerThreads[i]=new Thread(new Crawler()); 
           CrawlerThreads[i].setName("T"+i ); 
           CrawlerThreads[i].start(); 
        } 
        
         
         System.out.println("If you wish to stop the crawler: press -1");
       
       try {
           while (!cin.ready()||!cin.readLine().contains("-1"))
           {
               if (Executer.IndexedPages>=Executer.StoppingCriteria)
                   break;
                
           }
       } 
       catch (IOException ex) {
           Logger.getLogger(CrawlerManager.class.getName()).log(Level.SEVERE, null, ex);
       }
        if (Executer.IndexedPages<Executer.StoppingCriteria)
        {  
               System.out.println("Crawler is terminated! "); 
               Interrupt=true;

               for (int i=0 ; i<nThreads ; i++)
                {
                        try {
                        CrawlerThreads[i].join();
                   } catch (InterruptedException ex) {
                       Logger.getLogger(CrawlerManager.class.getName()).log(Level.SEVERE, null, ex);
                   }
            }
               System.out.println("Backing up seeds"); 
               Executer.Seeds.forEach((s) -> {
                     IndexerManager.m.InsertSeed(s);
                                             });
               Executer.PageDegree.keySet().forEach((s) -> {
                     IndexerManager.m.InsertPagesInOut(s, Executer.PageDegree.get(s).inDeg, 
                      Executer.PageDegree.get(s).outDeg);});

           
        }
        else{
            for (int i=0 ; i<nThreads ; i++ )
            {  
                try { 
                    CrawlerThreads[i].join();
                } 
                catch (InterruptedException ex) {
                    Logger.getLogger(CrawlerManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
       
        Executer.CrawlerEnd=1;            
       
        
   }
   
     }