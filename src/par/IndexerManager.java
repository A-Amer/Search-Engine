/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package par;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author AAmer
 */
public class IndexerManager implements Runnable{
    
   
    protected static DBmanager m = new DBmanager ();
    
    

    @Override
    public void run() {
        Thread t1 = new Thread(new Indexer());
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(IndexerManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        m.CloseConnection();
    }
}
