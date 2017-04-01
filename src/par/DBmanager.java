package Engine;

import java.sql.*;
import java.util.Map;
import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBmanager {

    // JDBC driver name and database URL
    private static final String JDBC_DRIVER = ("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=indexer";

//  Database credentials
    private static final String USER = "sa";
    private static final String PASS = "@mira123";

    private static Connection CONNECTION;

    public DBmanager() {
        try {
            //STEP 2: Register JDBC driver
            Class.forName(JDBC_DRIVER);
            CONNECTION = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connecting to database...");
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        }
    }

    public int CloseConnection() {
        try {
            if (CONNECTION != null) {
                CONNECTION.close();
                System.out.println("Connection closed");
            }
            return 1;
        } catch (SQLException se) {
        }
        return 0;
    }

    public boolean InsertUrlKeyword(String url, Map<String, DocData> map) {
        //insert the url and its corresponding keywords , frequencies and orders
        CallableStatement cstmt;
        boolean x = true;
        int i = 0;
        try {
            cstmt = CONNECTION.prepareCall("{call AddKeyword(?,?,?,?)}");

            for (String name : map.keySet()) {

                cstmt.setString(1, url);
                cstmt.setString(2, name);
                cstmt.setString(3, map.get(name).Frequency.toString());
                cstmt.setString(4, map.get(name).OrderInDoc);
                x = cstmt.execute();
                i++;
            }

        } catch (SQLException ex) {

            System.out.println(i);
            DeleteUrlKeyword(url);
            synchronized (Executer.IndexedPages) {
                Executer.IndexedPages--;//if error occurs during insertion decrement counter of indexed pages
            }
            return false;
        }
        return !x;
    }

    public boolean DeleteUrlKeyword(String url) {
        CallableStatement cstmt = null;
        boolean x;
        try {
            cstmt = CONNECTION.prepareCall("{call DeleteUrlKeywords(?)}");
            cstmt.setString(1, url);
            x = cstmt.execute();
            System.out.print("\nattempt to delete keyword");

        } catch (SQLException ex) {
            //Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return !x;
    }

    public boolean InsertSpam(String url) {
        CallableStatement cstmt = null;
        boolean x;
        try {
            cstmt = CONNECTION.prepareCall("{call AddSpam(?)}");
            cstmt.setString(1, url);
            x = cstmt.execute();

        } catch (SQLException ex) {
//           Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return !x;
    }

    public void AddSeedRestricted() {
        Statement statement = null;
        ResultSet SeedResult = null;
        ResultSet SpamResult = null;
        ResultSet DegreeResult = null;
        int RowCount = 0;
        try {

            String Query = "SELECT * FROM guest.seed;";
            statement = CONNECTION.createStatement();
            SeedResult = statement.executeQuery(Query);

            while (SeedResult.next()) {
                RowCount++;
                Executer.Seeds.add(SeedResult.getString("seedURL"));
            }
            SeedResult.close();
            Query = "SELECT * FROM guest.spam;";
            SpamResult = statement.executeQuery(Query);
            while (SpamResult.next()) {
                Executer.RestrictedSites.add(SpamResult.getString("spamurl"));
            }
            SpamResult.close();
            if (RowCount > 7) {//only get visited page if a backup was done
                Query = "SELECT * FROM guest.PagesInOut;";
                DegreeResult = statement.executeQuery(Query);
                InOutDeg InsertPageDeg;
                while (DegreeResult.next()) {
                    InsertPageDeg = new InOutDeg();
                    InsertPageDeg.inDeg = DegreeResult.getInt("pin");
                    InsertPageDeg.outDeg = DegreeResult.getInt("pout");
                    Executer.PageDegree.put(DegreeResult.getString("url"), InsertPageDeg);
                }
                DegreeResult.close();
            }
            statement.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public boolean InsertSeed(String url) {
        CallableStatement cstmt;
        boolean x;
        try {
            cstmt = CONNECTION.prepareCall("{call InsertSeeds(?)}");
            cstmt.setString("URL", url);
            x = cstmt.execute();

        } catch (SQLException ex) {
//           Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return !x;
    }

    public boolean InsertPagesInOut(String url, int pin, int pout) {
        CallableStatement cstmt;
        boolean x;
        try {
            cstmt = CONNECTION.prepareCall("{call InsertPagesInOut(?,?,?)}");
            
            cstmt.setString(1, url);
            cstmt.setInt(2, pin);
            cstmt.setInt(3, pout);
            x = cstmt.execute();

        } catch (SQLException ex) {
//           Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return !x;
    }

    public boolean DeleteSeeds() {
        CallableStatement cstmt = null;
        boolean x;
        try {
            cstmt = CONNECTION.prepareCall("{call DeleteSeeds}");

            x = cstmt.execute();
            System.out.print("\n Clearing database");

        } catch (SQLException ex) {
            //Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return !x;
    }

}
