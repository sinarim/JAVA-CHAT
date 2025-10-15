package smu.it.socket;

import java.sql.*;

public class DBUtil {
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:xe"; // DB 연결 URL
    private static final String USER = "EDU";
    private static final String PASSWORD = "EDU";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
