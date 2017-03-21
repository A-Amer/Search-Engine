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
import org.jsoup.nodes.Document;

/**
 *
 * @author AAmer
 */
class HtmlText{
    public String Plain;
    public String Headers;
    public String Bold;
    public String Title;
    public String Url;
}

 class Executer
{
protected static Queue<String> Seeds= new LinkedList<>();
protected static Set<String> VisitedUrls= new HashSet<>(); 
protected static Queue<HtmlText> IndexQ= new LinkedList<>();
protected static Queue<Document> DocQ= new LinkedList<>();


public static void main(String[] args){
     //seeds will be added from db
    Seeds.add("https://en-maktoob.yahoo.com/");
     Seeds.add("https://en.wikipedia.org/wiki/Main_Page");
     Thread Parsing=new Thread(new ParserManager());
     Thread Crawling=new Thread(new CrawlerManager());
     Thread Indexing=new Thread(new IndexerManager());
     
     Crawling.start();
     Parsing.start();
     Indexing.start();
  }

}
