package com.ajax.service;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.SQLException;
import java.sql.Statement;
import java.lang.ClassNotFoundException;
import java.lang.String;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.*;


//@WebServlet(name = "_WriteToDBService", urlPatterns = { "/_WriteToDBService" })

public class _WriteToDBService {
    private static final long serialVersionUID = 1L;
  //  public _WriteToDBService() { super(); }


    public static volatile int iii = 0;

        public volatile String message = null;
        public volatile Connection connection = null;

    public synchronized String doRegister(String username,String  password,String  firstname,String  lasttname,String  street,String  nr,String  zipcode,String  city,String  country,String  birtday,String  phone, String mobile_phone, String fax, String email, String language, HttpServletRequest request, HttpServletResponse response) throws ClassNotFoundException, SQLException, Exception {
        // load the sqlite-JDBC driver using the current class loader
        Class.forName("org.sqlite.JDBC");

        String  dbpath=null;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")){
            //Operating system is based on Windows
            dbpath = request.getSession().getServletContext().getRealPath("/files/database/db_user.db").replace("\\", java.nio.file.FileSystems.getDefault().getSeparator()).replace("/", java.nio.file.FileSystems.getDefault().getSeparator());
        }
        else if (os.contains("osx")){
            //Operating system is Apple OSX based
        }
        else if (os.contains("nix") || os.contains("aix") || os.contains("nux")){
            //Operating system is based on Linux/Unix/*AIX
            dbpath = request.getSession().getServletContext().getRealPath("/files/database/db_user.db").replace("\\", "/");
        }

       // String  dbpath = request.getSession().getServletContext().getRealPath("/files/database/db_user.db").replace("\\", "/");


            try {
                this.connection = DriverManager.getConnection("jdbc:sqlite:");
                Statement statement = this.connection.createStatement();
                File dbFile = new File(dbpath);
                if (dbFile.exists()) {
                  //  this._logger.AddInfo("File exists.");
                    statement.executeUpdate("restore from '" + dbpath + "'");
                    //statement.close();
                }

                ResultSet rs = statement.executeQuery("SELECT rowid FROM user WHERE user_name = '" + username + "' OR email = '" + email + "'");

                String id_um = "";
                while (rs.next()) {
                    id_um = rs.getString("rowid");
                }

                if (id_um == "") {
                    statement.executeUpdate("INSERT INTO USER VALUES('" + username + "','" + password + "','" + firstname + "','" + lasttname + "','" + street + "','" + nr + "','" + zipcode + "','" + city + "','" + country + "','" + birtday + "','" + phone + "','" + mobile_phone + "','" + fax + "','" + email + "','" + language + "',false,'User','F000000000')");
                    message = "SUCCESS";
                }else {
                    message = "FAILURE";
                }

                Statement stat = this.connection.createStatement();
                File _dbFile = new File(dbpath);
                if (_dbFile.exists()) {
                    //  this._logger.AddInfo("File exists.");
                    stat.executeUpdate("backup to '" + dbpath + "'");
                    //statement.close();
                }
                //stat.executeUpdate("backup to "+dbpath);
                statement.close();
                stat.close();
                if(connection != null) connection.close();
            } catch (Exception e) {
                //message = "FAILURE";
                message = e.toString();
               // e.printStackTrace();
            }

        return message;
    }


//-------------------------------------------------------------------------------

}
