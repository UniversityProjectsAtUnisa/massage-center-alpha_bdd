package centromassaggi.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CustomManager {
    static String url = "jdbc:postgresql://greenist.ddns.net/centromassaggi";
    static String user = "tonino";
    static String pwd = "Pippo";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, pwd);
    }
}