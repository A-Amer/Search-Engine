/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
public class RobotsChecker {
   

protected HashMap<String, ArrayList<String>> Allowed= new HashMap<String,ArrayList<String>>(); 
protected HashMap<String, ArrayList<String>> Disallowed= new HashMap<String,ArrayList<String>>(); 

public RobotsChecker(){}; 
protected URL getRobotsURL(String url) throws MalformedURLException
{
  URL main= new URL(url); 
  StringBuilder URLtemp= new StringBuilder(url); 
  int i = URLtemp.indexOf("://"); // must maintain the protocol 
  URLtemp=URLtemp.delete(i+3 , URLtemp.length()); 
  URLtemp.append(main.getHost()); 
  URLtemp.append("/robots.txt"); 
  
  System.out.println(Thread.currentThread().getName()+"This is our Robot: " +URLtemp); 
  return new URL(URLtemp.toString()) ;
  
  }    
  
    
protected boolean checkRobot(String URL) 
{  
    URL Roboturl=null;  URL UrlToCheck=null; 
	try
    { UrlToCheck= new URL(URL); 
     Roboturl=getRobotsURL(URL);
      
    } 
    catch(MalformedURLException m) 
   {     System.out.println(Thread.currentThread().getName()+"Invalid Robots URL");
         return false;
   }
   
   // if Robots file is not already downloaded , get it
   if (!Allowed.containsKey(Roboturl.getHost())&& !Disallowed.containsKey(Roboturl.getHost())); 
   {  if (updateRobotList(Roboturl)) 
       return true;   //return true bec robot file not found or Disallow: 
   }
    
   String file = UrlToCheck.getFile(); // gets the directory we are searching for
     try{
     for (String s:Allowed.get(Roboturl.getHost()))
    {  if ((file.compareToIgnoreCase(s)== 0)) 
       {System.out.println(Thread.currentThread().getName()+" due to "+s+"  your URL is allowed !!");
        return true; 
        }
    } 
     
     
         
    for (String s:Disallowed.get(Roboturl.getHost()))
    {  if (file.startsWith(s)) 
       {  System.out.println(Thread.currentThread().getName()+" due to "+s+"  your URL is disallowed !!");
          return false; }
    } 
     }
    catch(NullPointerException ex){
         return false;
     }

    return true ; 

}      
         


protected boolean updateRobotList(URL urlRobot ) //  must make sure that this is for our USerAgent 
{   
    HttpURLConnection c;
    
    try 
{  
    c = (HttpURLConnection)urlRobot.openConnection();
    c.addRequestProperty("User-Agent", "Mozilla/4.76");
} 

  catch (IOException|ClassCastException e )
{
    System.out.println(Thread.currentThread().getName()+ " connection error!"); 
    return false;
}
    
  try
{        
    BufferedReader reader= new BufferedReader(new InputStreamReader(c.getInputStream()));
    String  RobotTxt= new String(); 
    String path;
    ArrayList <String> disallowed = new ArrayList<String>(); 
    ArrayList <String> allowed= new ArrayList<String>(); 
    
    while ((RobotTxt=reader.readLine())!=null) //loop until we reach the desired user agent
    {if (RobotTxt.startsWith("User-agent: *"))   break; }
    
    while ((RobotTxt=reader.readLine())!=null)
    {  if (RobotTxt.startsWith("User"))   break;
        
      if (RobotTxt.indexOf("Disallow:")==0)
   
     {path=RobotTxt.substring("Disallow:".length());
      int commentIndex;
      commentIndex = path.indexOf("#");
      if (commentIndex != - 1)
      {path = path.substring(0, commentIndex);}
       path = path.trim();

     if (path.isEmpty() || path==null)  // if empty then it allows full access Diasllow:
     {  
         System.out.println(Thread.currentThread().getName() +" finds that everything is allowed here: "+urlRobot);
         return true; }
     disallowed.add(path); 
     }
              
      else if (RobotTxt.indexOf("Allow:")==0)
    { path=RobotTxt.substring("Allow:".length());
      int commentIndex;
      commentIndex = path.indexOf("#");
      if (commentIndex != - 1)
     {path =path.substring(0, commentIndex);}
     path=path.trim();
     allowed.add(path);
    }
    }
    reader.close(); //Closes buffer and releases memory resources
    Allowed.put(urlRobot.getHost(),allowed); 
    Disallowed.put(urlRobot.getHost(), disallowed); 
    allowed=null; 
    disallowed=null; 
    }

  catch (IOException ex){ 
     System.out.println(Thread.currentThread().getName()+ "hasn't found Robots file for "+ urlRobot); /// if robot file not found ; full crawling is allowed 
     return true; 
                       }
 return false;    
} 

}