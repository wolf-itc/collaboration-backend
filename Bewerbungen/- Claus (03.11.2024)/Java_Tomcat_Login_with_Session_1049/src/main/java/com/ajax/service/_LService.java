package com.ajax.service;
 
import java.nio.file.FileSystem;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import java.sql.SQLException;
import java.sql.Statement;
import java.lang.ClassNotFoundException;
import java.lang.String;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.PatternSyntaxException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;



public class _LService extends HttpServlet {

        public String message = null;

        public String doLogin(String username, String password, HttpServletRequest request, HttpServletResponse response) throws ClassNotFoundException, SQLException, Exception {
        // load the sqlite-JDBC driver using the current class loader
        Class.forName("org.sqlite.JDBC");
        //Connection connection = null;
        Connection con = null;
        Connection connection = null;
        Statement statement = null;

            String  dbpath=null;
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")){
                //Operating system is based on Windows
                dbpath = request.getSession().getServletContext().getRealPath("/files/database/db_user.db").replace("\\", java.nio.file.FileSystems.getDefault().getSeparator()).replace("/", java.nio.file.FileSystems.getDefault().getSeparator());

                //dbpath = (new StringBuilder()).append(getServletConfig().getServletContext().getRealPath("/")).append("files/database/").append("db_user").append(".db").toString();

            }
            else if (os.contains("osx")){
                //Operating system is Apple OSX based
            }
            else if (os.contains("nix") || os.contains("aix") || os.contains("nux")){
                //Operating system is based on Linux/Unix/*AIX
                dbpath = request.getSession().getServletContext().getRealPath("/files/database/db_user.db").replace("\\", "/");
            }


//            String  dbpath = request.getSession().getServletContext().getRealPath("/files/database/db_user.db").replace("\\", "/");

        try {
             connection = DriverManager.getConnection("jdbc:sqlite:"+dbpath);
             statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("SELECT password FROM user WHERE user_name = '" + username + "'");

             if (password.equals(rs.getString("password"))) {
                 message = "SUCCESS";
                 try {
                     message = Login(username, rs.getString("password"), request, response);
                 }catch (Exception uu){
                     message = "FAILURE";
                 }
             }else{
                 message = "FAILURE";
             }
         } catch (Exception e) {
                message = "FAILURE";
            //message = e.toString();
               // e.printStackTrace();
        }

            statement.close();
            if(connection != null) connection.close();


        return message;
        //    return dbpath;
    }


    public String Login(String usrr, String pwdd, HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        HttpSession session = request.getSession();
        session.setAttribute("user", usrr);
        //setting session to expiry in 30 mins
        session.setMaxInactiveInterval(30*60);
        Cookie userName = new Cookie("user", usrr);
        userName.setMaxAge(30*60);
        response.addCookie(userName);

        String encodedURL = response.encodeRedirectURL("LoginSuccess.jsp");
        response.sendRedirect(encodedURL);
        return "OKOK";
    }

}
