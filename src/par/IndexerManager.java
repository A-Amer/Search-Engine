/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author AAmer
 */
public class IndexerManager implements Runnable {

    protected static DBmanager m = new DBmanager();

    @Override
    public void run() {
        //create threads
        Thread t1 = new Thread(new Indexer());
        Thread t2 = new Thread(new Indexer());
        Thread t3 = new Thread(new Indexer());
        t1.start();
        t2.start();
        t3.start();
        try {//wait for threads to join
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(IndexerManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        m.CloseConnection();//close connection to database
    }
}
