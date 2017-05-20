package Query;
import java.sql.*;
import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import java.util.LinkedList;
import java.util.List;
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
        } catch (ClassNotFoundException | SQLException e) {
            //Handle errors for Class.forName

        }
    }
//    public void IntersectTable(String Words){
//        String Query = "delete guest.Inters;";
//        try {
//            CONNECTION.createStatement().execute(Query);
//        } catch (SQLException ex) {
//            Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        Statement statement = null;
//        Query = "insert into guest.Inters select url from guest.Snippet where txt LIKE'%"+Words+"%';";
//        
//        try {
//            
//            CONNECTION.createStatement().execute(Query);
//        } catch (SQLException ex) {
//            Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
   public String[] GetStems(String word){
     CallableStatement cstmt = null;
      boolean x;
      int i=0;
      String []ts=new String[1];
      LinkedList<String> words=new LinkedList<>();
         try {
            cstmt=CONNECTION.prepareCall("{call GetStem(?)}");
            cstmt.setString("word", word);
            x = cstmt.execute();
            ResultSet free=cstmt.getResultSet();
            while(free.next()){
                words.add(free.getString("keyword")); 
                i++;
            }
        
            cstmt.close();
            free.close();

        } catch (SQLException ex) {
            Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
         return words.toArray(ts);
}
    public Snip GetSnipExact(String word,String url){
        CallableStatement cstmt = null;
        boolean x;
        Snip PageSnip=new Snip();
        PageSnip.url=url;
        PageSnip.snippet="";
        try {
            cstmt=CONNECTION.prepareCall("{call GetSnip(?)}");
            cstmt.setString("url", url);
            x = cstmt.execute();
            ResultSet free=cstmt.getResultSet();
            free.next();
            String txt=free.getString("txt");
            txt=txt.replaceAll("\\?\\?", "");
            char i=0;
            int index=0;
            while(i<3){
                if(txt.length()<=250){
                    PageSnip.snippet=txt;
                    break;
                }
                int min=txt.length()-1;
                int ind=0;
                String name;
                index=txt.indexOf(word, index);
                if(index<min && index!=-1)
                    min=index;
                if(min==txt.length()-1){
                    if(txt.indexOf(" ", txt.length()-160)!=-1)
                        PageSnip.snippet=PageSnip.snippet.concat(txt.substring(txt.indexOf(" ", txt.length()-160)));
                    else
                        PageSnip.snippet=PageSnip.snippet.concat(txt.substring(txt.length()-160));
                    break;
                }
                if(txt.length()-1<min+80){
                     name = txt.substring(min, txt.length()-1);
                }
                else
                {
                     if(txt.indexOf(" ", min+60)!=-1)
                     name = txt.substring(min, txt.indexOf(" ", min+60)); 
                    else
                        name = txt.substring(min, min+60); 
                }
                name=name.replaceAll("\\b("+word+"|"+word+"."+"|"+word+":"
                        +"|"+word+"!"+"|"+word+"?"+"|"+word+","+")\\b\\s+","<b>"+word+" </b>");
                PageSnip.snippet=PageSnip.snippet.concat(name+"...<br>");
                if(txt.length()-1< min+60)
                    index=txt.length()-1;
                else
                    index=min+60;
                i++;
            }
            PageSnip.title=free.getString("title");
        } catch (SQLException ex) {
            Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return PageSnip;
    }
    public Snip GetSnip(String Qry,String url){
        CallableStatement cstmt = null;
        boolean x;
        String[]word=GetStems(Qry);
        Snip PageSnip=new Snip();
        PageSnip.url=url;
        PageSnip.snippet="";
        try {
            
            cstmt=CONNECTION.prepareCall("{call GetSnip(?)}");
            cstmt.setString("url", url);
            x = cstmt.execute();
            ResultSet free=cstmt.getResultSet();
            free.next();
            String txt=free.getString("txt");
            txt=txt.replaceAll("\\?\\?", "");
            char i=0;
            int []index=new int[word.length];
            for(int j=0;j<index.length;j++){
                   index[j]=0; 
                   
                }
             int ind=0;
            while(i<3){
                if(txt.length()<=250){
                    PageSnip.snippet=txt;
                    break;
                }
                int min=txt.length()-1;
               
                String name;
                if(txt.indexOf(Qry,ind)!=-1){
                    ind=txt.indexOf(Qry,ind);
                    if(txt.length()-1<ind+80){
                     name = txt.substring(ind, txt.length()-1);
                }
                else
                {
                    if(txt.indexOf(" ", ind+60)!=-1)
                     name = txt.substring(ind, txt.indexOf(" ", ind+60)); 
                    else
                        name = txt.substring(ind, ind+60);
                }
                if(txt.length()-1< ind+60)
                    ind=txt.length()-1;
                else
                    ind=min+60;
                    
                }
                else{
                for(int j=0;j<index.length;j++)
                {
                    index[j]=txt.indexOf(word[j], index[j]);
                    if(index[j]<min && index[j]!=-1)
                        min=index[j];
                }
                if(min==txt.length()-1){
                    if(txt.indexOf(" ", txt.length()-160)!=-1)
                        PageSnip.snippet=PageSnip.snippet.concat(txt.substring(txt.indexOf(" ", txt.length()-160)));
                    else
                        PageSnip.snippet=PageSnip.snippet.concat(txt.substring(txt.length()-160));
                    break;
                }
                
                if(txt.length()-1<min+80){
                     name = txt.substring(min, txt.length()-1);
                }
                else
                {
                    if(txt.indexOf(" ", min+60)!=-1)
                     name = txt.substring(min, txt.indexOf(" ", min+60)); 
                    else
                        name = txt.substring(min, min+60); 
                }
               }
                for(int j=0;j<word.length;j++){
                name=name.replaceAll("\\b("+word[j]+"|"+word[j]+"."+"|"+word[j]+":"
                        +"|"+word[j]+"!"+"|"+word[j]+"?"+")\\b\\s+","<b>"+word[j]+" </b>")
                        .replaceAll("\\b("+word[j]+","+")\\b\\s+","<b>"+word[j]+" </b>,");
                }
                PageSnip.snippet=PageSnip.snippet.concat(name+"...<br>");
                
                for(int j=0;j<index.length;j++)
                {
                    if(txt.length()-1< min+60)
                        index[j]=txt.length()-1;
                    else
                        index[j]=min+60;
                }
                i++;
            }
            PageSnip.title=free.getString("title");
        } catch (SQLException ex) {
            Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return PageSnip;
    }
    public LinkedList AllWord(String word,int indx){
         CallableStatement cstmt = null;
         LinkedList<UrlInfo> WordUrls=new LinkedList<>();
        boolean x;
        int TermCount=0;
        try {
            
            cstmt=CONNECTION.prepareCall("{call GetFree(?)}");
            cstmt.setString("word", word);
            x = cstmt.execute();
            ResultSet free=cstmt.getResultSet();
            while(free.next()){
                UrlInfo Info=new UrlInfo();
                if(free.getString("keyword").equals(word)){
                    Info.weight=2;
                    TermCount++;
                }
                else{
                    Info.weight=1;
                }
                Info.URL=free.getString("URL");
                Info.frequency=free.getInt("frequency");
                Info.pin=free.getInt("pin");
                Info.pout=free.getInt("pout");
                WordUrls.add(Info);
                
            }
            cstmt.close();
            free.close();
           cstmt=CONNECTION.prepareCall("{call GetTot}");
            x = cstmt.execute();
            free=cstmt.getResultSet();
            free.next();
            int tot=free.getInt("Tot"); 
            QProcessor.Idfs[indx]=TermCount/(float)tot; 

        } catch (SQLException ex) {
            Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
            
        }
        return WordUrls;
    }
    public LinkedList ExactWord(String word){
         CallableStatement cstmt = null;
         LinkedList<String> WordUrls=new LinkedList<>();
        boolean x;
        try {
            
            cstmt=CONNECTION.prepareCall("{call GetExact(?)}");
            cstmt.setString("word", word);
            x = cstmt.execute();
            ResultSet free=cstmt.getResultSet();
            while(free.next()){
                WordUrls.add(free.getString("URL"));  
            }
        
            cstmt.close();
            free.close();

        } catch (SQLException ex) {
            Logger.getLogger(DBmanager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
            
        }
        
        return WordUrls;
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
}

    