/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package par;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.nodes.Document;

/**
 *
 * @author AA
 */
     
    
   public class CrawlerManager implements Runnable{
   
    @Override
    public void run () {
        String record= "/tmp" ;
   
        String Url = "/tmp.html" ; 
        if (Url.startsWith(record))
            System.out.println("match");
        record="/tmp/" ;  
        if (Url.startsWith(record))
            System.out.println("match");
        
       
        Thread t1=new Thread(new Crawler()); 
        Thread t2=new Thread(new Crawler());
        Thread t3=new Thread(new Crawler());

        t1.setName(" t1" ); 
        t2.setName(" t2" ); 
        t3.setName(" t3" ); 

        t1.start(); 
        t2.start();
        t3.start(); 
        
        try { 
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(CrawlerManager.class.getName()).log(Level.SEVERE, null, ex);
        }
         
   
    
    
    
    }
    
    
    
    
    
   }

