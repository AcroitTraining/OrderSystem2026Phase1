package dao;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.mysql.cj.jdbc.MysqlDataSource;

public class DBConnection {

    private static final String DOCKER_JDBC_URL =
            "jdbc:mysql://db:3306/order_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Tokyo";

    private static final String LOCAL_JDBC_URL =
            "jdbc:mysql://localhost:3306/order_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Tokyo";

    private static final String DB_USER = "order";
    private static final String DB_PASS = "1234";

    private static final MysqlDataSource dataSource;

    static {
        MysqlDataSource dockerDataSource = new MysqlDataSource();
        dockerDataSource.setUrl(DOCKER_JDBC_URL);
        dockerDataSource.setUser(DB_USER);
        dockerDataSource.setPassword(DB_PASS);

        MysqlDataSource localDataSource = new MysqlDataSource();
        localDataSource.setUrl(LOCAL_JDBC_URL);
        localDataSource.setUser(DB_USER);
        localDataSource.setPassword(DB_PASS);

        MysqlDataSource selectedDataSource;

        try (Connection conn = dockerDataSource.getConnection()) {
            selectedDataSource = dockerDataSource;
            System.out.println("DB接続: Docker");
        } catch (SQLException e) {
            selectedDataSource = localDataSource;
            System.out.println("DB接続: localhost");
        }

        dataSource = selectedDataSource;
    }

    private DBConnection() {
        // インスタンス化させない
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}