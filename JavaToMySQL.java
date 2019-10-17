package package1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// Класс для работы с MySQL
public class JavaToMySQL {

    // JDBC URL, username and password of MySQL server
    private static final String url = "jdbc:mysql://localhost:3306/library";
    private static final String user = "admin";
    private static final String password = "User_mysql0$";

    // JDBC variables for opening and managing connection
    private static Connection con ;
    private static Statement stmt;
    public static ResultSet rs;
    // Коннект к серверу
    public void connectToServer() {
        try {
            // открываем коннект к базе данных MySQL server
            con = DriverManager.getConnection(url, user, password);
            // getting Statement object to execute query
            stmt = con.createStatement();
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
    }
    // Закрываем подключение к серверу
    public void closeconnectToServer() {
        //закрываем всё
        try { con.close(); } catch(SQLException se) { /*can't do anything */ }
        try { stmt.close(); } catch(SQLException se) { /*can't do anything */ }
        try { rs.close(); } catch(SQLException se) { /*can't do anything */ }
    }

    // Выролняем запрос или Select или Insert, Update, Delete
    public void execQuery(String sQuery, boolean bQuery) {
        try {
            if (bQuery) {
                // executing SELECT query
                rs = stmt.executeQuery(sQuery);
            }
            else {
                stmt.execute(sQuery);
            }
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
    }
}