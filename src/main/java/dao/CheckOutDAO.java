package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CheckOutDAO {
	private final String JDBC_URL = "jdbc:mysql://localhost:3306/order_management";
	private final String DB_USER = "order";
	private final String DB_PASS = "1234";

	private Connection getConnection() throws SQLException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("JDBCドライバを読み込めませんでした");
		}
		return DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
	}

	public void executeCheckout(String tableNumber) throws SQLException {
		Connection conn = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);

			// 現在の有効なセッションIDを取得する
			int sessionId = getCurrentSessionId(conn, tableNumber);

			if (sessionId != -1) {
				// order_detailsの会計フラグを更新（取得したsession_idを使用）
				updateOrderDetails(conn, sessionId);

				// 現在のセッションを closed に更新（取得したsession_idを使用）
				closeCurrentSession(conn, sessionId);
			}

			// 次回用の新しいセッションを作成（ここは卓番号 table_id でOK）
			createNewSession(conn, tableNumber);

			// table_masterのステータスを更新
			updateTableMaster(conn, tableNumber);

			conn.commit();
		} catch (SQLException e) {
			if (conn != null) conn.rollback();
			throw e;
		} finally {
			if (conn != null) conn.close();
		}
	}

	// 現在アクティブなセッションIDを特定するメソッド
	private int getCurrentSessionId(Connection conn, String tableNumber) throws SQLException {
		String sql = 
				"SELECT session_id "
						+ "FROM table_sessions "
						+ "WHERE table_id = ? "
						+ "AND session_status = 'active'";

		try (PreparedStatement pStmt = conn.prepareStatement(sql)) {
			pStmt.setString(1, tableNumber);
			try (ResultSet rs = pStmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt("session_id");
				}
			}
		}
		return -1; // 見つからない場合
	}

	private void updateOrderDetails(Connection conn, int sessionId) throws SQLException {
		String sql = 
				"UPDATE order_details "
						+ "SET accounting_flag = 1 "
						+ "WHERE session_id = ? "
						+ "AND accounting_flag = 0";

		try (PreparedStatement pStmt = conn.prepareStatement(sql)) {
			pStmt.setInt(1, sessionId); // sessionIdを使用
			pStmt.executeUpdate();
		}
	}

	private void closeCurrentSession(Connection conn, int sessionId) throws SQLException {
		String sql = 
				"UPDATE table_sessions "
						+ "SET session_status = 'closed', end_time = NOW() "
						+ "WHERE session_id = ?";

		try (PreparedStatement pStmt = conn.prepareStatement(sql)) {
			pStmt.setInt(1, sessionId);
			pStmt.executeUpdate();
		}
	}

	private void createNewSession(Connection conn, String tableNumber) throws SQLException {
		String sql = "INSERT INTO table_sessions (table_id, session_status, url_token, guest_count) "
				+ "VALUES (?, 'inactive', CONCAT(UUID(), '-', SUBSTRING(MD5(RAND()), 1, 8)), 0);";

		try (PreparedStatement pStmt = conn.prepareStatement(sql)) {
			pStmt.setString(1, tableNumber);
			pStmt.executeUpdate();
		}
	}

	private void updateTableMaster(Connection conn, String tableNumber) throws SQLException {
		String sql = "UPDATE table_master "
				+ "SET table_status = 'inactive', updated_at = CURRENT_TIMESTAMP "
				+ "WHERE table_id = ?";

		try (PreparedStatement pStmt = conn.prepareStatement(sql)) {
			pStmt.setString(1, tableNumber);
			pStmt.executeUpdate();
		}
	}
}