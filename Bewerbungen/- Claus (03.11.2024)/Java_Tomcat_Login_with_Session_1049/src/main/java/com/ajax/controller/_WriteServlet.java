package com.ajax.controller;
 
import java.io.IOException;
 
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
//import javax.servlet.SingleThreadModel;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.ajax.service._WriteToDBService;

public class _WriteServlet extends HttpServlet {

    _WriteToDBService service = null;

    public synchronized void init(ServletConfig config)throws ServletException {
        service = new _WriteToDBService();
    }

        public static volatile List<String[]> _rowList = new ArrayList<String[]>();
        // public static String _message = null;

        public static volatile boolean iiii = true;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        synchronized(this) {
            String username = decodeURIComponent(request.getParameter("username"));
            String password = decodeURIComponent(request.getParameter("password"));
            String firstname = decodeURIComponent(request.getParameter("firstname"));
            String lasttname = decodeURIComponent(request.getParameter("lasttname"));
            String street = decodeURIComponent(request.getParameter("street"));
            String nr = decodeURIComponent(request.getParameter("nr"));
            String zipcode = decodeURIComponent(request.getParameter("zipcode"));
            String city = decodeURIComponent(request.getParameter("city"));
            String country = decodeURIComponent(request.getParameter("country"));
            String birtday = decodeURIComponent(request.getParameter("birtday"));
            String phone = decodeURIComponent(request.getParameter("phone"));
            String mobile_phone = decodeURIComponent(request.getParameter("mobile_phone"));
            String fax = decodeURIComponent(request.getParameter("fax"));
            String email = decodeURIComponent(request.getParameter("email"));
            String language = decodeURIComponent(request.getParameter("language"));

            _rowList.add(new String[]{username, password, firstname, lasttname, street, nr, zipcode, city, country, birtday, phone, mobile_phone, fax, email, language});


            while (true) {

            try {
                // Some implementation here
                TimeUnit.MILLISECONDS.sleep(50);

                if (!_rowList.isEmpty()) {

                    String _message = service.doRegister(_rowList.get(0)[0], _rowList.get(0)[1], _rowList.get(0)[2], _rowList.get(0)[3], _rowList.get(0)[4], _rowList.get(0)[5], _rowList.get(0)[6], _rowList.get(0)[7], _rowList.get(0)[8], _rowList.get(0)[9], _rowList.get(0)[10], _rowList.get(0)[11], _rowList.get(0)[12], _rowList.get(0)[13], _rowList.get(0)[14], request, response);
                    //String _message = service.doRegister(username, password, firstname, lasttname, street, nr, zipcode, city, country, birtday, phone, mobile_phone, fax, email, language, request, response);
                    response.getWriter().write(_message);
                    _rowList.remove(0);
                }else { break; }

            } catch (SQLException ex) {

            } catch (Exception ee) {

            }


            }
        }
    }
//------------------------------------------------------------------------------
public static String decodeURIComponent(String s) {
    if (s == null) {
        return null;
    }

    String resultd = null;

    try {
        resultd = URLDecoder.decode(s, "UTF-8");
    }

    // This exception should never occur.
    catch (UnsupportedEncodingException ed) {
        resultd = s;
    }

    return resultd;
}
//------------------------------------------------------------------------------

}
