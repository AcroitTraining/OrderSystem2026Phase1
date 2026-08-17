package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import model.TableInfo;

public class OrderStartDAO {

	private final DataSource dataSource;

	/**
	 * ① 通常運用（Servletなど）で使うデフォルトコンストラクタ
	 */
	public OrderStartDAO() {
		this(DBConnection.getDataSource());
	}

	/**
	 * ② DIコンストラクタ。DataSourceを外部から注入する。
	 *
	 * @param dataSource コネクション取得元のDataSource
	 */
	public OrderStartDAO(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/** メソッド呼び出しのたびに新しい接続を取得する */
	private Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	/**
	 * 卓のステータス・人数を更新する。
	 */
	public void updateStatus(int tableId, int guestCount) throws SQLException {
		String tmSql = 
				"UPDATE table_master "
				+ "SET table_status = 'active', updated_at = NOW() "
				+ "WHERE table_id = ?";

		String tsSql = 
				"UPDATE table_sessions "
				+ "SET session_status = CASE "
				+ "WHEN session_status = 'inactive' THEN 'active' "
				+ "ELSE session_status "
				+ "END, "
				+ "start_time = CASE "
				+ "WHEN start_time IS NULL THEN NOW() "
				+ "ELSE start_time "
				+ "END, "
				+ "guest_count = CASE "
				+ "WHEN guest_count = 0 THEN ? "
				+ "ELSE guest_count "
				+ "END "
				+ "WHERE table_id = ?";

		try (Connection conn = getConnection()) {
			try {
				conn.setAutoCommit(false); // トランザクション開始

				try (PreparedStatement pStmt1 = conn.prepareStatement(tmSql)) {
					pStmt1.setInt(1, tableId);
					pStmt1.executeUpdate();
				}

				try (PreparedStatement pStmt2 = conn.prepareStatement(tsSql)) {
					pStmt2.setInt(1, guestCount);
					pStmt2.setInt(2, tableId);
					pStmt2.executeUpdate();
				}

				conn.commit();
			} catch (SQLException e) {
				conn.rollback();
				throw e;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 指定した卓の稼働中セッションIDを取得する。
	 * 該当セッションが存在しない場合は null を返す。
	 */
	public Integer findSessionId(int tableId) throws SQLException {
		String sql = "SELECT session_id "
				   + "FROM table_sessions "
				   + "WHERE table_id = ? "
				   + "AND session_status = 'active'";

		try (Connection conn = getConnection();
			 PreparedStatement pStmt = conn.prepareStatement(sql)) {

			pStmt.setInt(1, tableId);
			try (ResultSet rs = pStmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt("session_id");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}

		return null;
	}

	/**
	 * URLトークンから該当する卓のセッション情報を取得する。
	 * 見つからない、またはクローズ済みの場合は null を返す。
	 */
	public TableInfo findTableSessionByToken(String token) throws SQLException {
		if (token == null || token.isEmpty()) {
			return null;
		}

		String sql = "SELECT table_id, session_id, session_status "
				   + "FROM table_sessions "
				   + "WHERE url_token = ? "
				   + "AND session_status != 'closed'";

		try (Connection conn = getConnection();
			 PreparedStatement pStmt = conn.prepareStatement(sql)) {

			pStmt.setString(1, token);
			try (ResultSet rs = pStmt.executeQuery()) {
				if (rs.next()) {
					int tableId = rs.getInt("table_id");
					int sessionId = rs.getInt("session_id");
					String sessionStatus = rs.getString("session_status");

					return new TableInfo(tableId, sessionId, sessionStatus);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}

		return null;
	}
}