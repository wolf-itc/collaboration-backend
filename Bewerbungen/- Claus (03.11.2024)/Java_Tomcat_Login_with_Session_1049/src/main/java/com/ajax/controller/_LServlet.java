package com.ajax.controller;
 
// import java.io.IOException;
 
import jakarta.servlet.ServletConfig;
// import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.UnsupportedEncodingException;
// import java.lang.ClassNotFoundException;
import java.net.URLDecoder;
// import java.sql.SQLException;
// import java.sql.SQLException;

import com.ajax.service._LService;


public class _LServlet extends HttpServlet {
        
    _LService service = null;
     
    public void init(ServletConfig config) {
        service = new _LService();
    }
     
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String username = decodeURIComponent(request.getParameter("username"));
        String password = decodeURIComponent(request.getParameter("password"));

        try {
            String message = service.doLogin(username, password, request, response);
            response.getWriter().write(message);
        } //catch (ClassNotFoundException ee) {}
        //catch (SQLException ee) {}
        catch (Exception ee) {}

    }
//------------------------------------------------------------------------------
public static String decodeURIComponent(String s) {
    if (s == null) {
        return null;
    }

  //  String resultd = null;

    try {
        return URLDecoder.decode(s, "UTF-8");
    }

    // This exception should never occur.
    catch (UnsupportedEncodingException ed) {
        return s;
    }

//    return resultd;
}
//------------------------------------------------------------------------------

}
