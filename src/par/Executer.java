/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.nodes.Document;

/**
 *
 * @author AAmer
 */
class Executer {

    protected static Queue<String> Seeds = new LinkedList<>();
    protected static Map<String, InOutDeg> PageDegree = new LinkedHashMap<>();
    protected static Queue<HtmlText> IndexQ = new LinkedList<>();
    protected static Queue<Document> DocQ = new LinkedList<>();
    protected static Set<String> RestrictedSites = new HashSet<>();
    protected static Integer IndexedPages = 0;
    final static int StoppingCriteria = 50;
    static int CrawlerEnd = 0, ParserEnd = 0;

    public static void main(String[] args) {
        try {

            //seeds will be added from db
            IndexerManager.m.AddSeedRestricted();

            Thread Parsing = new Thread(new ParserManager());
            Thread Crawling = new Thread(new CrawlerManager());
            Thread Indexing = new Thread(new IndexerManager());

            Crawling.start();
            Parsing.start();
            Indexing.start();
            Crawling.join();
            Parsing.join();
            Indexing.join();
            if (!CrawlerManager.userTerminates) {
                DBmanager AddToDb = new DBmanager();
                List<Entry<String, InOutDeg>> Seed = InOutDeg.entriesSortedByValues(PageDegree);
                AddToDb.DeleteSeeds();
                for (int i = 0; i < 5; i++) {
                    AddToDb.InsertSeed(Seed.get(i).getKey());
                }
                Seed.forEach((s) -> {
                    AddToDb.InsertPagesInOut(s.getKey(), s.getValue().inDeg,
                            s.getValue().outDeg);
                }
                );

                AddToDb.CloseConnection();
            }
        } 
        catch (InterruptedException ex) {
            Logger.getLogger(Executer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

class InOutDeg implements Comparable {

    int inDeg = 0;
    int outDeg = 0;

    @Override
    public int compareTo(Object t) {
        InOutDeg obj = (InOutDeg) t;
        if (obj.outDeg < outDeg) {
            return 1;
        } else if (obj.outDeg == outDeg) {
            if (obj.inDeg < inDeg) {
                return 1;
            }
            if (obj.inDeg == inDeg) {
                return 0;
            }
        }
        return -1;
    }

    static <K, V extends Comparable<? super V>>
            List<Entry<K, V>> entriesSortedByValues(Map<K, V> map) {

        List<Entry<K, V>> sortedEntries = new ArrayList<>(map.entrySet());

        Collections.sort(sortedEntries, (Entry<K, V> e1, Entry<K, V> e2) -> e2.getValue().compareTo(e1.getValue()));

        return sortedEntries;
    }
}
