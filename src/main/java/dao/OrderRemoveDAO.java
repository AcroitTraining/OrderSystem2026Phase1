package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

public class OrderRemoveDAO {

	private final DataSource dataSource;

	/**
	 * ① 通常運用（Servletなど）で使うデフォルトコンストラクタ
	 */
	public OrderRemoveDAO() {
		this(DBConnection.getDataSource());
	}

	/**
	 * ② DIコンストラクタ。DataSourceを外部から注入する。
	 *
	 * @param dataSource コネクション取得元のDataSource
	 */
	public OrderRemoveDAO(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/** メソッド呼び出しのたびに新しい接続を取得する */
	private Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	public void deleteOrderDetails(int num) throws SQLException {
		String updateToppingSql = "UPDATE topping t "
				+ "JOIN (SELECT topping_id, SUM(topping_quantity) AS total_quantity FROM multiple_toppings WHERE order_id = ? GROUP BY topping_id) a "
				+ "ON t.topping_id = a.topping_id "
				+ "SET t.topping_stock = t.topping_stock + a.total_quantity";

		String deleteOrderSql = "DELETE od, mt, pd FROM order_details AS od "
				+ "LEFT JOIN multiple_toppings AS mt ON od.order_id = mt.order_id "
				+ "LEFT JOIN product_details AS pd ON mt.order_id = pd.order_id "
				+ "WHERE od.order_id = ?";

		try (Connection conn = getConnection()) {
			try {
				conn.setAutoCommit(false); // トランザクション開始

				try (PreparedStatement ps1 = conn.prepareStatement(updateToppingSql);
					 PreparedStatement ps2 = conn.prepareStatement(deleteOrderSql)) {

					ps1.setInt(1, num);
					ps1.executeUpdate();

					ps2.setInt(1, num);
					ps2.executeUpdate();

					conn.commit(); // 両方成功したらコミット

				} catch (SQLException e) {
					conn.rollback(); // 失敗したらロールバック
					throw e;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw e;
			}
		}
	}
}