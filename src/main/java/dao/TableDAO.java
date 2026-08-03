package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TableDAO {

    // --- DB接続設定（※環境に合わせて書き換えてください） ---
    // もしすでにDB接続を管理する共通クラス（例: DBManager.getConnection() など）があれば、
    // このクラスの接続処理をそちらに置き換えてください。
    private static final String URL = "jdbc:mysql://localhost:3306/order_management"; // DBのURL
    private static final String USER = "order";
    private static final String PASSWORD = "1234";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver"; // MySQLの場合

    /**
     * DB接続を取得する内部メソッド
     */
    private Connection getConnection() throws Exception {
        Class.forName(DRIVER);
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    // --------------------------------------------------

    /**
     * 卓IDを元に、現在のステータス（Open / Close）を取得する
     * 
     * @param tableId 調査したい卓のID
     * @return ステータス文字列（例: "Open", "Close"）。レコードがない場合は "Unknown"
     * @throws Exception DBアクセス時のエラー
     */
    public String getStatus(int sessionId) throws Exception {
    	System.out.println("tabledao");
        String status = "Unknown"; // 初期値
        
        // 実行するSQL文（※テーブル名やカラム名はご自身のDBに合わせてください）
        String sql = "SELECT session_status FROM table_sessions WHERE session_id = ?";

        // try-with-resources 文を使い、Connection、PreparedStatement、ResultSet を自動で確実に閉じる
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // SQLの「?」の部分に卓IDをセット
            pstmt.setInt(1, sessionId);

            // SQLを実行して結果を取得
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // DBのステータスカラムから文字列を取得
                    status = rs.getString("session_status");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("TableDao.getStatus でエラーが発生しました。卓ID: " + sessionId);
            e.printStackTrace();
            throw e; // フィルター側にエラーを伝播させる
        }

        return status;
    }
}