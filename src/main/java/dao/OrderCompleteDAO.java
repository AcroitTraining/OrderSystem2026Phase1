package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

public class OrderCompleteDAO {

	private final DataSource dataSource;

	/**
	 * ① 通常運用（Servletなど）で使うデフォルトコンストラクタ
	 */
	public OrderCompleteDAO() {
		this(DBConnection.getDataSource());
	}

	/**
	 * ② DIコンストラクタ。DataSourceを外部から注入する。
	 *
	 * @param dataSource コネクション取得元のDataSource
	 */
	public OrderCompleteDAO(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/** メソッド呼び出しのたびに新しい接続を取得する */
	private Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	/**
	 * 未注文状態の注文詳細を注文確定状態（order_flag = 1）にし、注文時刻を記録する。
	 */
	public void updateOrderDetails() throws SQLException {
		String sql = "UPDATE order_details SET order_flag = 1, order_time = CURRENT_TIMESTAMP WHERE order_flag = 0";

		try (Connection conn = getConnection();
				PreparedStatement pStmt = conn.prepareStatement(sql)) {

			pStmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}
}