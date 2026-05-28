/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import com.sun.jdi.connect.spi.Connection;
import java.sql.*;

/**
 *
 * @author ASUS
 */
public class Connector {
    private static String jdbc_driver = "com.mysql.cj.jdbc.Driver";
    private static String nama_db = "warungdb";
    private static String url_db = "jdbc:mysql://localhost:3306/" + nama_db;
    private static String username_db = "root";
    private static String password_db = "";
    
    static Connection conn;
    
    public static Connection connect(){
        try{
            Class.forName(jdbc_driver);
            
            conn = (Connection) DriverManager.getConnection(url_db, username_db, password_db);
            System.out.println("Connection Success");
        } catch(ClassNotFoundException | SQLException e){
            System.out.println("Connection Failed:" + e.getLocalizedMessage());
        }
        return conn;
    }
}
