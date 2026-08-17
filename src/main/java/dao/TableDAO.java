package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

public class TableDAO {

	private final DataSource dataSource;

	/**
	 * ① 通常運用（FilterやServletなど）で使うデフォルトコンストラクタ
	 */
	public TableDAO() {
		this(DBConnection.getDataSource());
	}

	/**
	 * ② DIコンストラクタ。DataSourceを外部から注入する。
	 *
	 * @param dataSource コネクション取得元のDataSource
	 */
	public TableDAO(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/** メソッド呼び出しのたびに新しい接続を取得する */
	private Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	/**
	 * セッションIDを元に、現在のステータスを取得する
	 * 
	 * @param sessionId 調査したいセッションID
	 * @return ステータス文字列（例: "active", "closed"）。レコードがない場合は "Unknown"
	 * @throws SQLException DBアクセス時のエラー
	 */
	public String getStatus(int sessionId) throws SQLException {
		System.out.println("TableDAO.getStatus");
		String status = "Unknown"; // 初期値

		String sql = "SELECT session_status FROM table_sessions WHERE session_id = ?";

		try (Connection conn = getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, sessionId);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					status = rs.getString("session_status");
				}
			}

		} catch (SQLException e) {
			System.err.println("TableDAO.getStatus でエラーが発生しました。セッションID: " + sessionId);
			e.printStackTrace();
			throw e;
		}

		return status;
	}
}