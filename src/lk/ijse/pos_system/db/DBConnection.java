package lk.ijse.pos_system.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static DBConnection dbConnection=null;
    private Connection connection;

    private DBConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/POS_System",
                "root",
                "shiny1234");
    }

    public static DBConnection getInstance() throws ClassNotFoundException, SQLException {
        if (dbConnection==null){
            dbConnection= new DBConnection();
        }
        return dbConnection;

        /* return (dbConnection==null)?(dbConnection= new DbConnection()):(dbConnection);*/
    }

    public Connection getConnection(){
        return connection;
    }
}
