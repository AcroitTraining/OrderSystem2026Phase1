package dao;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.mysql.cj.jdbc.MysqlDataSource;

public class DBConnection {

    private static final String JDBC_URL =
            "jdbc:mysql://db:3306/order_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Tokyo";
    private static final String DB_USER = "order";
    private static final String DB_PASS = "1234";

    private static final MysqlDataSource dataSource;

    static {
        dataSource = new MysqlDataSource();
        dataSource.setUrl(JDBC_URL);
        dataSource.setUser(DB_USER);
        dataSource.setPassword(DB_PASS);
    }

    private DBConnection() {
        // インスタンス化させない
    }

    // DataSource を取得するメソッド
    public static DataSource getDataSource() {
        return dataSource;
    }

    // 従来の Connection 直接取得も残しておく（互換性のため）
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}