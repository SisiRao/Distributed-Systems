package project4task2;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author sisi
 */
@WebServlet(urlPatterns = {"/SteamUserGameServlet","/getResults"})
public class SteamUserGameServlet extends HttpServlet {

    SteamUserGameModel steamModel = null;
    DashboardModel dashModel = null;
    
    // Initiate this servlet by instantiating the model that it will use.
    @Override
    public void init() {
        steamModel = new SteamUserGameModel();
        dashModel = new DashboardModel();
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // handle 
        if (request.getServletPath().equals("/SteamUserGameServlet")) {
            
            String steamID = request.getParameter("steamID");
            String[] result = steamModel.doSteamSearch(steamID);
            String curTime = String.valueOf(System.currentTimeMillis());
            // determine what type of device our user is
            String ua = request.getHeader("User-Agent");
            boolean mobile;
            // prepare the appropriate DOCTYPE for the view pages
            if (ua != null && ((ua.indexOf("Android") != -1) || (ua.indexOf("iPhone") != -1))) {
                mobile = true;
                /*
                 * This is the latest XHTML Mobile doctype. To see the difference it
                 * makes, comment it out so that a default desktop doctype is used
                 * and view on an Android or iPhone.
                 */
                request.setAttribute("doctype", "<!DOCTYPE html PUBLIC \"-//WAPFORUM//DTD XHTML Mobile 1.2//EN\" \"http://www.openmobilealliance.org/tech/DTD/xhtml-mobile12.dtd\">");
            } else {
                mobile = false;
                request.setAttribute("doctype", "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
            }

            // log data to database when the request is from a mobile device.
            if (mobile) {
                dashModel.logInfo(ua, curTime, steamID, result[0],result[1]);
            }

            response.setContentType("text/plain");
            PrintWriter out = response.getWriter();
            out.print(result[0]);
            out.flush();
            
        }
        
        if (request.getServletPath().equals("/getResults")) {
            String ua = request.getHeader("User-Agent");
            if (ua != null && ((ua.indexOf("Android") != -1) || (ua.indexOf("iPhone") != -1))) {
                request.setAttribute("doctype", "<!DOCTYPE html PUBLIC \"-//WAPFORUM//DTD XHTML Mobile 1.2//EN\" \"http://www.openmobilealliance.org/tech/DTD/xhtml-mobile12.dtd\">");
            } else {
                request.setAttribute("doctype", "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
            }
            
            // get log information from mongodb & get analytical data
            Entry[] entries = dashModel.getInfo();
            String popularmobile = dashModel.getMostMobileDevice(entries);
            String topgamecnt = dashModel.getTopGameCount(entries);
            String populargame = dashModel.getMostPlayedGame(entries);
            
            // set respond attributes
            request.setAttribute("popularmobile", popularmobile);
            request.setAttribute("topgamecnt", topgamecnt);
            request.setAttribute("populargame", populargame);
            request.setAttribute("entries", entries);
            
            RequestDispatcher view = request.getRequestDispatcher("result.jsp");
            view.forward(request, response);
            
        }
        
            } 

}
