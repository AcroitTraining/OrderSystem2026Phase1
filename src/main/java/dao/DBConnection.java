package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String JDBC_URL =
            "jdbc:mysql://localhost:3306/order_management";

    private static final String DB_USER = "order";

    private static final String DB_PASS = "1234";

    private DBConnection() {
        // インスタンス化させない
    }

    public static Connection getConnection()
            throws SQLException {

        return DriverManager.getConnection(
                JDBC_URL,
                DB_USER,
                DB_PASS
        );
    }
}