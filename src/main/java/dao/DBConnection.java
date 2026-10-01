package dao;

import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.time.Duration;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import com.mysql.cj.jdbc.MysqlDataSource;

/**
 * 実行環境に応じて接続先DBを切り替えるクラス。
 *
 * AUTO:
 *   Eclipse等のローカル環境 → LOCAL
 *   Docker(Node環境)        → HA
 *
 * LOCAL:
 *   DB_HOST / DB_PORTへ直接接続
 *
 * HA:
 *   Phase5 Dashboard Status APIから
 *   currentPrimaryNodeNameを取得して
 *   db-a / db-bの現在Primaryへ接続
 */
public class DBConnection {

    /*
     * ========================================
     * 接続モード
     * ========================================
     *
     * AUTO  : 実行環境から自動判定
     * LOCAL : localhost等へ直接接続
     * HA    : Phase5 APIからPrimary DBを判定
     */
    private enum ConnectionMode {
        AUTO,
        LOCAL,
        HA
    }

    private static final String DB_MODE =
            getEnvironmentOrDefault(
                    "DB_MODE",
                    "AUTO");

    /*
     * ========================================
     * LOCAL接続設定
     * ========================================
     */

    private static final String DB_HOST =
            getEnvironmentOrDefault(
                    "DB_HOST",
                    "localhost");

    private static final String DB_PORT =
            getEnvironmentOrDefault(
                    "DB_PORT",
                    "3306");

    /*
     * ========================================
     * 共通DB設定
     * ========================================
     */

    private static final String DB_NAME =
            getEnvironmentOrDefault(
                    "DB_NAME",
                    "order_management");

    private static final String DB_USER =
            getEnvironmentOrDefault(
                    "DB_USER",
                    "order");

    private static final String DB_PASS =
            getEnvironmentOrDefault(
                    "DB_PASSWORD",
                    "1234");

    /*
     * ========================================
     * HA設定
     * ========================================
     */

    private static final String HA_STATUS_URL =
            getEnvironmentOrDefault(
                    "HA_STATUS_URL",
                    "http://localhost:8080"
                    + "/OrderSystem2026Phase5"
                    + "/api/dashboard/status");

    private static final Duration HTTP_CONNECT_TIMEOUT =
            Duration.ofSeconds(2);

    private static final Duration HTTP_REQUEST_TIMEOUT =
            Duration.ofSeconds(3);

    private static final Pattern PRIMARY_PATTERN =
            Pattern.compile(
                    "\"currentPrimaryNodeName\"\\s*:\\s*\"(db-a|db-b)\"");

    private static final Pattern DUAL_PRIMARY_PATTERN =
            Pattern.compile(
                    "\"dualPrimaryDetected\"\\s*:\\s*(true|false)");

    private static final HttpClient httpClient =
            HttpClient.newBuilder()
                    .connectTimeout(
                            HTTP_CONNECT_TIMEOUT)
                    .build();

    /*
     * Connection取得時に接続先を決定するDataSource。
     */
    private static final DataSource dataSource =
            new RuntimeAwareDataSource();

    private DBConnection() {
        // インスタンス化させない
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    public static Connection getConnection()
            throws SQLException {

        return dataSource.getConnection();
    }

    /**
     * 実際に使用する接続モードを判定する。
     */
    private static ConnectionMode resolveConnectionMode()
            throws SQLException {

        final ConnectionMode configuredMode;

        try {

            configuredMode =
                    ConnectionMode.valueOf(
                            DB_MODE
                                    .trim()
                                    .toUpperCase(
                                            Locale.ROOT));

        } catch (IllegalArgumentException e) {

            throw new SQLException(
                    "Unsupported DB_MODE: "
                            + DB_MODE
                            + " (AUTO / LOCAL / HA only)",
                    e);
        }

        /*
         * 明示指定されている場合は
         * そのモードを使用する。
         */
        if (configuredMode != ConnectionMode.AUTO) {
            return configuredMode;
        }

        /*
         * Docker ComposeではNODE_NAMEが設定されている。
         *
         * node-a / node-bコンテナ内
         *      ↓
         * HAモード
         */
        String nodeName =
                System.getenv(
                        "NODE_NAME");

        if (nodeName != null
                && !nodeName.isBlank()) {

            return ConnectionMode.HA;
        }

        /*
         * Eclipseなど、
         * NODE_NAMEが存在しない環境
         *      ↓
         * LOCALモード
         */
        return ConnectionMode.LOCAL;
    }

    /**
     * 実行環境に応じたMysqlDataSourceを生成する。
     */
    private static MysqlDataSource createDataSource()
            throws SQLException {

        ConnectionMode mode =
                resolveConnectionMode();

        String host;

        switch (mode) {

        case LOCAL:

            host = DB_HOST;

            break;

        case HA:

            host = resolveCurrentPrimary();

            break;

        default:

            throw new SQLException(
                    "Unexpected connection mode: "
                            + mode);
        }

        String jdbcUrl =
                "jdbc:mysql://"
                        + host
                        + ":"
                        + DB_PORT
                        + "/"
                        + DB_NAME
                        + "?useSSL=false"
                        + "&allowPublicKeyRetrieval=true"
                        + "&serverTimezone=Asia/Tokyo";

        MysqlDataSource mysqlDataSource =
                new MysqlDataSource();

        mysqlDataSource.setUrl(
                jdbcUrl);

        mysqlDataSource.setUser(
                DB_USER);

        mysqlDataSource.setPassword(
                DB_PASS);

        System.out.println(
                "[DB-CONNECTION]"
                        + " mode="
                        + mode
                        + " host="
                        + host
                        + " port="
                        + DB_PORT
                        + " database="
                        + DB_NAME);

        return mysqlDataSource;
    }

    /**
     * Phase5 HA Status APIから
     * 現在PrimaryのDB名を取得する。
     */
    private static String resolveCurrentPrimary()
            throws SQLException {

        try {

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(
                                            HA_STATUS_URL))
                            .timeout(
                                    HTTP_REQUEST_TIMEOUT)
                            .GET()
                            .build();

            HttpResponse<String> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers
                                    .ofString());

            if (response.statusCode() != 200) {

                throw new SQLException(
                        "Phase5 HA Status API returned HTTP "
                                + response.statusCode());
            }

            String body =
                    response.body();

            /*
             * ====================================
             * Dual Primary確認
             * ====================================
             */
            Matcher dualPrimaryMatcher =
                    DUAL_PRIMARY_PATTERN
                            .matcher(
                                    body);

            if (!dualPrimaryMatcher.find()) {

                throw new SQLException(
                        "dualPrimaryDetected "
                                + "was not found in HA Status API");
            }

            boolean dualPrimaryDetected =
                    Boolean.parseBoolean(
                            dualPrimaryMatcher.group(
                                    1));

            if (dualPrimaryDetected) {

                throw new SQLException(
                        "Database connection blocked: "
                                + "DUAL_PRIMARY_DETECTED");
            }

            /*
             * ====================================
             * Primary取得
             * ====================================
             */
            Matcher primaryMatcher =
                    PRIMARY_PATTERN
                            .matcher(
                                    body);

            if (!primaryMatcher.find()) {

                throw new SQLException(
                        "currentPrimaryNodeName "
                                + "was not found in HA Status API");
            }

            String primaryNodeName =
                    primaryMatcher.group(
                            1);

            /*
             * 防御的チェック
             */
            if (!"db-a".equals(
                    primaryNodeName)
                    && !"db-b".equals(
                            primaryNodeName)) {

                throw new SQLException(
                        "Unsupported Primary Database: "
                                + primaryNodeName);
            }

            return primaryNodeName;

        } catch (InterruptedException e) {

            Thread.currentThread()
                    .interrupt();

            throw new SQLException(
                    "Interrupted while resolving "
                            + "Primary Database",
                    e);

        } catch (SQLException e) {

            throw e;

        } catch (Exception e) {

            throw new SQLException(
                    "Failed to resolve "
                            + "Primary Database",
                    e);
        }
    }

    /**
     * 環境変数取得。
     * 未設定ならデフォルト値を返す。
     */
    private static String getEnvironmentOrDefault(
            String name,
            String defaultValue) {

        String value =
                System.getenv(
                        name);

        if (value == null
                || value.isBlank()) {

            return defaultValue;
        }

        return value;
    }

    /**
     * Connection取得時に
     * LOCAL / HAを判断するDataSource。
     */
    private static class RuntimeAwareDataSource
            implements DataSource {

        @Override
        public Connection getConnection()
                throws SQLException {

            return createDataSource()
                    .getConnection();
        }

        @Override
        public Connection getConnection(
                String username,
                String password)
                throws SQLException {

            MysqlDataSource mysqlDataSource =
                    createDataSource();

            mysqlDataSource.setUser(
                    username);

            mysqlDataSource.setPassword(
                    password);

            return mysqlDataSource
                    .getConnection();
        }

        @Override
        public PrintWriter getLogWriter()
                throws SQLException {

            return null;
        }

        @Override
        public void setLogWriter(
                PrintWriter out)
                throws SQLException {

            // 使用しない
        }

        @Override
        public void setLoginTimeout(
                int seconds)
                throws SQLException {

            // 使用しない
        }

        @Override
        public int getLoginTimeout()
                throws SQLException {

            return 0;
        }

        @Override
        public Logger getParentLogger()
                throws SQLFeatureNotSupportedException {

            throw new SQLFeatureNotSupportedException();
        }

        @Override
        public <T> T unwrap(
                Class<T> iface)
                throws SQLException {

            throw new SQLException(
                    "unwrap is not supported");
        }

        @Override
        public boolean isWrapperFor(
                Class<?> iface)
                throws SQLException {

            return false;
        }
    }
}