/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.nodes.Document;


/**
 *
 * @author AAmer
 */


public class ParserManager implements Runnable{
   
    @Override
    public void run() {
        Document Html;
        
        while(true){
            try{
             
               synchronized(Executer.DocQ){ 
                         Html=Executer.DocQ.remove();//remove document in DocQ to parse it
               }
               
            }
            catch(NoSuchElementException Empty){//if the DocQ is empty 
                if(Executer.CrawlerEnd==1){//check if the crawler finished its task and stopping criteria was reached
                    Executer.ParserEnd=1;//then parser to is done
                     return;
                }
                 Html=null;//otherwise wait till the DocQ is filled by the crawler
            }
        
        if(Html!=null){
                Thread Parsing= new Thread(new Parse(Html));
                Parsing.setName("parser");
                Parsing.start();//a thread to parse the document for the indxr
                
                    Thread UrlFetch=new Thread(new HtmlUrlFetcher(Html));
                    UrlFetch.setName("fetcher");
                    UrlFetch.start();//thread to extract urls from document and count in and out degree of pages
       
                try {
                        Parsing.join();//wait till threads finish to get new document
                        UrlFetch.join();
                        
                    }
                catch (InterruptedException ex) {
                        Logger.getLogger(ParserManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
         }
       }
    }
}