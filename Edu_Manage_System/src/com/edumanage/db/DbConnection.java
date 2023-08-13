package com.edumanage.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    //Singleton
    //Rule 1
    private static DbConnection dbConnection = null;

    private Connection connection;
//Rule 2
    private DbConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lms", "root", "1234");
    }
//Rule 3
    public static DbConnection getInstance() throws SQLException, ClassNotFoundException {
        if(dbConnection==null){
            dbConnection=new DbConnection();

        }
        return dbConnection;
    }

    public Connection getConnection(){
        return connection;
    }
}
