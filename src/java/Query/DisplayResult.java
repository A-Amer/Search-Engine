package Query;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.Math;


public class DisplayResult extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String text = request.getParameter("SearchBox");
        String st = request.getParameter("start");
        
        /////////////////////////////////////////////////////
        QProcessor.Process(text);
        Snip results [] = QProcessor.DispList;
        int resultno = QProcessor.size;
        //////////////////////////////////////////////////////
        int URLperPage = 10;
        if(st==null)
            st="1";
        try (PrintWriter out = response.getWriter()) {

            PrintHead(out);
            out.println("<body>\n<br>\n"+
                    "<a href=\"/searchengine/\">\n"+
                    "<img  src=\"Capture.JPG\" alt=\"Logo\" style=\"width:400px;height:60px;\">\n"+
                    "</a>\n"+
                    "<form action=\"DisplayResult\" method=\"GET\" id=\"DisplayResult\">\n"+
                    "<input class=\"box\" type=\"text\" name=\"SearchBox\" value='"+text+"'>\n" +
                    "<button class=\"button\">Search</button>\n"+
                    "</form><br>");
             out.println("<div>\n");
            int str = Integer.parseInt(st);
            int j = (str-1)*URLperPage;
            while( j < URLperPage*str && j < resultno)
            {
                out.println("<h3 style=\"display:inline-block;text-overflow: ellipsis; white-space: nowrap;\"><a href="+results[j].url+" target=\"_self\">"+results[j].title+"</a></h3>");
                out.println("<h4>"+results[j].url+"</h4>");
                out.println("<p>"+results[j].snippet+"</p>");
                j++;
            }
            out.println("</div>\n");
            out.println("<footer class=\"container\">\n" +
                    "  <center><ul class=\"pagination\">\n" );
            int end =(int) Math.ceil((double)resultno/(double)URLperPage);         
            for(int i=1; i <= end; i++)
            {
                if(Integer.toString(i).equals(st))
                    out.println(    "    <li class=\"active\"><a href=\"#\">"+i+"</a></li>\n" );   
                else
                    out.println(    "    <li><a href='?SearchBox="+text+"&start="+i+"'>"+i+"</a></li>\n" );
            }
            out.println("  </ul></center>" );
            out.println("</footer>");
            out.println("</body>");
            out.println("</html>");

        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
    private void PrintHead(PrintWriter out)
    {
        out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<meta charset=\"utf-8\">\n" +
"    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
"  <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\">\n" +
"    <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js\"></script>\n" +
"  <script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js\"></script>");
            out.println("<title>MYSearchEngine</title>\n"+
                    "<style>\n"+
                    "html {\n" +
                    "    position: relative;\n" +
                    "    min-height: 100%;\n" +
                    "}\n" +
                    "body {\n" +
                    "    margin-bottom: 100px;\n" +
                    "}\n"+
                    "div {"+
                    "    margin-left: 100px;\n"+
                    "}\n"+
                    "footer {\n" +                 
                    "    position: absolute;\n" +
                    "    left: 0;\n" +
                    "    bottom: 0;\n" + 
                    "    height: 100px;\n"+
                    "    background-color: white;\n" +
                    "    color: white;\n" +
                    "}\n"+
                    ".button {\n" +
                    "    background-color: #4CAF50;\n" +
                    "    border: none;\n" +
                    "    color: white;\n" +
                    "    padding: 5px 32px;\n" +
                    "    text-align: center;\n" +
                    "    text-decoration: none;\n" +
                    "    display: inline-block;\n" +
                    "    font-size: 16px;\n" +
                    "    margin: 4px 2px;\n" +
                    "    cursor: pointer;\n" +
                    "}\n"+
                    "h3 {\n" +
                    "   line-height:10px;\n"+                 
                    "}\n" +                    
                    "h4 {\n" +
                    "    color:rgb(215, 0 ,42);\n" +
                    "    margin-bottom: 2px;\n"+                      
                    "}\n" +
                    "p {\n" +
                    "    color: sienna;\n" +
                    "    margin-bottom: 2px;\n"+                      
                    "}\n" +                    
                    "input.box {\n" +
                    "    height: 30px;\n" +
                    "    width: 300px;\n" +
                    "    font-size: 16px;\n" +
                    "    margin-left: 20px;\n"+
                    "    background-color: white;\n" +
                    "}\n" +
                    "a:link {\n" +
                    "    color: blue;\n" +
                    "    text-decoration: none;\n" +
                    "}\n" +
                    "a:visited {\n" +
                    "    color: purple;\n" +
                    "}\n" +
                    "a:hover {\n" +
                    "    text-decoration: underline;\n" +
                    "}"+
                    "</style>"
                    );
            out.println("</head>");
    }

}
