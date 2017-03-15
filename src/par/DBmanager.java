
package par;
import java.sql.*;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBmanager {
    
    // JDBC driver name and database URL
   private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
   private static final String DB_URL = "jdbc:mysql://localhost:3306/indexer?autoReconnect=true&useSSL=false";

   //  Database credentials
   private static final String USER = "root";
   private static final String PASS = "123456";
   
   private static Connection CONNECTION;
   
   public DBmanager()
   {
       try
       {
            //STEP 2: Register JDBC driver
            Class.forName(JDBC_DRIVER);
            CONNECTION = DriverManager.getConnection(DB_URL,USER,PASS);
            System.out.println("Connecting to database...");
       }
       catch(Exception e)
       {
            //Handle errors for Class.forName
            e.printStackTrace();
       }
    }
  
    public int CloseConnection()
    {
        try{
            if(CONNECTION!=null)
            {
               CONNECTION.close();
               System.out.println("Connection closed");
            }
            return 1;
      }catch(SQLException se){
      }
        return 0;
    }
    
    public boolean InsertUrlKeyword(String url , Map<String, Integer> map )
    {
        CallableStatement cstmt;
        boolean x = true;
        try{
            cstmt = CONNECTION.prepareCall("{call AddKeyword(?,?,?)}");
            
            for (Object name: map.keySet())
            {
                cstmt.setString(1, url );
                cstmt.setString(2, name.toString());
                cstmt.setString(3,map.get(name).toString());
                x = cstmt.execute();
                System.out.println(x);
            }

        } catch (SQLException ex) {
//           Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
             return false;
       }
        return !x;
    }
    
    public boolean DeleteUrlKeyword(String url)
    {
        CallableStatement cstmt = null;
        boolean x ;
        try{
            cstmt = CONNECTION.prepareCall("{call DeleteUrlKeywords(?)}");
            cstmt.setString(1, url );
             x = cstmt.execute();
            System.out.print(x);

        } catch (SQLException ex) {
           //Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
           return false;
       }
        return !x;
    }
    
    public boolean InsertSpam(String url)
    {
        CallableStatement cstmt = null;
        boolean x ;
        try{
            cstmt = CONNECTION.prepareCall("{call AddSpam(?)}");
            cstmt.setString(1, url );
            x = cstmt.execute();
            
        } catch (SQLException ex) {
//           Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
             return false;
       }
        return !x;
    }
    
    public boolean InsertUrl(String url,String doc)
    {
        CallableStatement cstmt = null;
        boolean x ;
        try{
            cstmt = CONNECTION.prepareCall("{call AddUrl(?,?)}");
            cstmt.setString(1, url );
            cstmt.setString(1, doc );
            x = cstmt.execute();
            
        } catch (SQLException ex) {
//           Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
             return false;
       }
        return !x;
    }
        
}
