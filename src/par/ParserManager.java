/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package par;

import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.nodes.Document;


/**
 *
 * @author AAmer
 */
public class ParserManager implements Runnable{
    
    @Override
    public void run(){
       
        Thread Par1=new Thread(new DocumentFetcher());
        Thread Par2=new Thread(new DocumentFetcher());
        Thread Par3=new Thread(new DocumentFetcher());
        Par1.start();
        Par2.start();
        Par3.start();
        try {
            Par1.join();
            Par2.join();
            Par3.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(ParserManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}

class DocumentFetcher implements Runnable{
   
    @Override
    public void run() {
        Document Html=null ;
        
        while(true){
            try{
               synchronized(Executer.DocQ){ 
                         Html=Executer.DocQ.remove();
               }
               
            }
            catch(NoSuchElementException Empty){
               
                 Html=null;
            } 
        
        if(Html!=null){
                Thread Parsing= new Thread(new Parse(Html));
                Thread UrlFetch=new Thread(new HtmlUrlFetcher(Html));
                Parsing.start();
                UrlFetch.start();
                    try {
                        Parsing.join();
                        UrlFetch.join();
                        
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DocumentFetcher.class.getName()).log(Level.SEVERE, null, ex);
                    }
         }
       }
    }
}