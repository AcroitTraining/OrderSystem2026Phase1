package integration;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

class TC001OrderStartIT {

    /*
     * ==========================================
     * テスト対象
     * ==========================================
     */

    private static final int TEST_TABLE_ID = 1;

    private static final String BASE_URL =
            "http://localhost:8081/OrderSystem2026Phase1";

    private static final String HA_STATUS_URL =
            "http://localhost:8081"
            + "/OrderSystem2026Phase5"
            + "/api/dashboard/status";


    /*
     * ==========================================
     * DB接続設定
     * ==========================================
     */

    private static final String DB_NAME =
            "order_management";

    private static final String DB_USER =
            "order";

    private static final String DB_PASSWORD =
            "1234";

    /*
     * Dockerのホスト側公開ポート
     *
     * db-a
     * 3307 → 3306
     *
     * db-b
     * 3308 → 3306
     */
    private static final int DB_A_PORT = 3307;
    private static final int DB_B_PORT = 3308;


    /*
     * ==========================================
     * Playwright
     * ==========================================
     */

    private static Playwright playwright;
    private static Browser browser;

    private BrowserContext context;
    private Page page;


    /*
     * Phase5 Status APIから
     * currentPrimaryNodeNameを取得するための正規表現。
     */
    private static final Pattern PRIMARY_PATTERN =
            Pattern.compile(
                    "\"currentPrimaryNodeName\"\\s*:\\s*\"(db-a|db-b)\"");


    /*
     * ==========================================
     * テスト全体開始前
     * ==========================================
     */
    @BeforeAll
    static void beforeAll() {

        playwright = Playwright.create();

        browser =
                playwright.chromium()
                        .launch(
                                new BrowserType.LaunchOptions()
                                        .setHeadless(false)
                                        .setSlowMo(500));
    }


    /*
     * ==========================================
     * 各テスト開始前
     * ==========================================
     */
    @BeforeEach
    void beforeEach()
            throws Exception {

        /*
         * TC-001を毎回同じ条件で実行できるように
         * 卓1をテスト開始前の状態へ戻す。
         */
        resetTestData();

        /*
         * Cookie / HttpSessionを
         * テストごとに分離する。
         */
        context =
                browser.newContext();

        page =
                context.newPage();
    }


    /*
     * ==========================================
     * TC-001
     *
     * 注文開始
     * ==========================================
     */
    @Test
    void tc001OrderStart()
            throws Exception {

        /*
         * --------------------------------------
         * 1. トップ画面
         * --------------------------------------
         */
        page.navigate(
                BASE_URL + "/");

        assertThat(page)
                .hasTitle(
                        "開店処理画面");


        /*
         * --------------------------------------
         * 2. 卓番号1を入力
         * --------------------------------------
         */
        page.locator(
                "input[name='tableId']")
                .fill(
                        String.valueOf(
                                TEST_TABLE_ID));


        /*
         * --------------------------------------
         * 3. 登録
         * --------------------------------------
         */
        page.locator(
                "input[type='submit'][value='登録']")
                .click();


        /*
         * --------------------------------------
         * 4. 注文開始画面
         * --------------------------------------
         */
        assertThat(page)
                .hasTitle(
                        "注文開始");


        /*
         * 初期人数 = 1
         */
        assertThat(
                page.locator(
                        "#guest-count-display"))
                .hasText(
                        "1");


        /*
         * --------------------------------------
         * 5. 人数 +
         * --------------------------------------
         */
        page.locator(
                "button[name='action'][value='plus']")
                .click();

        assertThat(
                page.locator(
                        "#guest-count-display"))
                .hasText(
                        "2");


        /*
         * --------------------------------------
         * 6. 人数 -
         * --------------------------------------
         */
        page.locator(
                "button[name='action'][value='minus']")
                .click();

        assertThat(
                page.locator(
                        "#guest-count-display"))
                .hasText(
                        "1");


        /*
         * --------------------------------------
         * 7. 注文開始
         * --------------------------------------
         */
        page.locator(
                "button[name='action'][value='start']")
                .click();


        /*
         * --------------------------------------
         * 8. メニュー画面
         * --------------------------------------
         */
        assertThat(page)
                .hasTitle(
                        "メニュー表示");

        assertThat(
                page.locator(
                        ".table-num"))
                .hasText(
                        "1卓");


        /*
         * ======================================
         * 9. DB確認
         * ======================================
         */

        verifyTableMaster();

        verifyTableSession();
    }


    /*
     * ==========================================
     * table_master確認
     * ==========================================
     */
    private void verifyTableMaster()
            throws Exception {

        String sql =
                "SELECT table_status "
                + "FROM table_master "
                + "WHERE table_id = ?";

        try (
                Connection conn =
                        getPrimaryDbConnection();

                PreparedStatement ps =
                        conn.prepareStatement(
                                sql)
        ) {

            ps.setInt(
                    1,
                    TEST_TABLE_ID);

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                /*
                 * 卓1が存在することを確認。
                 */
                if (!rs.next()) {

                    throw new AssertionError(
                            "table_masterに卓"
                            + TEST_TABLE_ID
                            + "が存在しません。");
                }

                String tableStatus =
                        rs.getString(
                                "table_status");

                /*
                 * 注文開始後なので
                 *
                 * inactive
                 *     ↓
                 * active
                 *
                 * になっている必要がある。
                 */
                assertEquals(
                        "active",
                        tableStatus,
                        "table_master.table_status");
            }
        }
    }


    /*
     * ==========================================
     * table_sessions確認
     * ==========================================
     */
    private void verifyTableSession()
            throws Exception {

        String sql =
                "SELECT "
                + "session_id, "
                + "session_status, "
                + "guest_count, "
                + "start_time, "
                + "end_time "
                + "FROM table_sessions "
                + "WHERE table_id = ? "
                + "AND session_status != 'closed' "
                + "ORDER BY session_id DESC "
                + "LIMIT 1";

        try (
                Connection conn =
                        getPrimaryDbConnection();

                PreparedStatement ps =
                        conn.prepareStatement(
                                sql)
        ) {

            ps.setInt(
                    1,
                    TEST_TABLE_ID);

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                if (!rs.next()) {

                    throw new AssertionError(
                            "卓"
                            + TEST_TABLE_ID
                            + "の有効なセッションが"
                            + "存在しません。");
                }

                int sessionId =
                        rs.getInt(
                                "session_id");

                String sessionStatus =
                        rs.getString(
                                "session_status");

                int guestCount =
                        rs.getInt(
                                "guest_count");

                Object startTime =
                        rs.getTimestamp(
                                "start_time");

                Object endTime =
                        rs.getTimestamp(
                                "end_time");


                /*
                 * session_status
                 *
                 * inactive → active
                 */
                assertEquals(
                        "active",
                        sessionStatus,
                        "table_sessions.session_status");


                /*
                 * TC-001では
                 *
                 * 1 → 2 → 1
                 *
                 * にしてから注文開始している。
                 */
                assertEquals(
                        1,
                        guestCount,
                        "table_sessions.guest_count");


                /*
                 * 注文開始時刻が設定されている。
                 */
                assertNotNull(
                        startTime,
                        "table_sessions.start_time");


                /*
                 * 会計前なので
                 * end_timeはNULL。
                 */
                assertEquals(
                        null,
                        endTime,
                        "table_sessions.end_time");


                System.out.println(
                        "[TC-001 DB CHECK]"
                        + " sessionId="
                        + sessionId
                        + " status="
                        + sessionStatus
                        + " guestCount="
                        + guestCount
                        + " startTime="
                        + startTime);
            }
        }
    }


    /*
     * ==========================================
     * テストデータ初期化
     * ==========================================
     */
    private void resetTestData()
            throws Exception {

        try (
                Connection conn =
                        getPrimaryDbConnection()
        ) {

            conn.setAutoCommit(
                    false);

            try {

                /*
                 * --------------------------------
                 * table_master
                 * --------------------------------
                 */
                String tableMasterSql =
                        "UPDATE table_master "
                        + "SET table_status = 'inactive', "
                        + "updated_at = NOW() "
                        + "WHERE table_id = ?";

                try (
                        PreparedStatement ps =
                                conn.prepareStatement(
                                        tableMasterSql)
                ) {

                    ps.setInt(
                            1,
                            TEST_TABLE_ID);

                    ps.executeUpdate();
                }


                /*
                 * --------------------------------
                 * 最新の未終了セッションを取得
                 * --------------------------------
                 */
                Integer sessionId =
                        findLatestOpenSessionId(
                                conn,
                                TEST_TABLE_ID);

                if (sessionId == null) {

                    throw new IllegalStateException(
                            "卓"
                            + TEST_TABLE_ID
                            + "にテスト可能な"
                            + "未終了セッションがありません。");
                }


                /*
                 * --------------------------------
                 * table_sessions初期化
                 * --------------------------------
                 */
                String sessionSql =
                        "UPDATE table_sessions "
                        + "SET session_status = 'inactive', "
                        + "guest_count = 0, "
                        + "start_time = NULL, "
                        + "end_time = NULL "
                        + "WHERE session_id = ?";

                try (
                        PreparedStatement ps =
                                conn.prepareStatement(
                                        sessionSql)
                ) {

                    ps.setInt(
                            1,
                            sessionId);

                    ps.executeUpdate();
                }

                conn.commit();

                System.out.println(
                        "[TC-001 PREPARE]"
                        + " tableId="
                        + TEST_TABLE_ID
                        + " sessionId="
                        + sessionId);

            } catch (Exception e) {

                conn.rollback();

                throw e;

            } finally {

                conn.setAutoCommit(
                        true);
            }
        }
    }


    /*
     * ==========================================
     * 最新の未終了session_id取得
     * ==========================================
     */
    private Integer findLatestOpenSessionId(
            Connection conn,
            int tableId)
            throws SQLException {

        String sql =
                "SELECT session_id "
                + "FROM table_sessions "
                + "WHERE table_id = ? "
                + "AND session_status != 'closed' "
                + "ORDER BY session_id DESC "
                + "LIMIT 1";

        try (
                PreparedStatement ps =
                        conn.prepareStatement(
                                sql)
        ) {

            ps.setInt(
                    1,
                    tableId);

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                if (rs.next()) {

                    return rs.getInt(
                            "session_id");
                }
            }
        }

        return null;
    }


    /*
     * ==========================================
     * Primary DBへの接続
     * ==========================================
     */
    private Connection getPrimaryDbConnection()
            throws Exception {

        String primary =
                getCurrentPrimaryNodeName();

        int port;

        if ("db-a".equals(
                primary)) {

            port = DB_A_PORT;

        } else if ("db-b".equals(
                primary)) {

            port = DB_B_PORT;

        } else {

            throw new IllegalStateException(
                    "Unknown Primary DB: "
                    + primary);
        }

        String jdbcUrl =
                "jdbc:mysql://localhost:"
                + port
                + "/"
                + DB_NAME
                + "?useSSL=false"
                + "&allowPublicKeyRetrieval=true"
                + "&serverTimezone=Asia/Tokyo";

        System.out.println(
                "[TC DB CONNECTION]"
                + " primary="
                + primary
                + " port="
                + port);

        return DriverManager.getConnection(
                jdbcUrl,
                DB_USER,
                DB_PASSWORD);
    }


    /*
     * ==========================================
     * Phase5からCurrent Primary取得
     * ==========================================
     */
    private String getCurrentPrimaryNodeName()
            throws Exception {

        HttpClient client =
                HttpClient.newBuilder()
                        .connectTimeout(
                                Duration.ofSeconds(
                                        3))
                        .build();

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        HA_STATUS_URL))
                        .timeout(
                                Duration.ofSeconds(
                                        5))
                        .GET()
                        .build();

        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers
                                .ofString());

        if (response.statusCode() != 200) {

            throw new IllegalStateException(
                    "Phase5 Status API HTTP "
                    + response.statusCode());
        }

        Matcher matcher =
                PRIMARY_PATTERN.matcher(
                        response.body());

        if (!matcher.find()) {

            throw new IllegalStateException(
                    "currentPrimaryNodeName"
                    + " がPhase5 Status APIから"
                    + "取得できませんでした。");
        }

        return matcher.group(
                1);
    }


    /*
     * ==========================================
     * 各テスト終了後
     * ==========================================
     */
    @AfterEach
    void afterEach() {

        if (context != null) {
            context.close();
        }
    }


    /*
     * ==========================================
     * 全テスト終了後
     * ==========================================
     */
    @AfterAll
    static void afterAll() {

        if (browser != null) {
            browser.close();
        }

        if (playwright != null) {
            playwright.close();
        }
    }
}