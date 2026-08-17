package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import model.ItemDetailsInfo;
import model.OrderListInfo;

public class ToppingDAO {

	private final DataSource dataSource;

	/**
	 * ① 通常運用（Servletなど）で使うデフォルトコンストラクタ
	 */
	public ToppingDAO() {
		this(DBConnection.getDataSource());
	}

	/**
	 * ② DIコンストラクタ。DataSourceを外部から注入する。
	 *
	 * @param dataSource コネクション取得元のDataSource
	 */
	public ToppingDAO(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/** メソッド呼び出しのたびに新しい接続を取得する */
	private Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	// 注文情報（商品名・価格等）を取得するメソッド
	public OrderListInfo findOrderInfo(int orderId) throws SQLException {
		OrderListInfo ol = null;
		String sql = 
			"SELECT od.order_id, p.product_name, p.product_price " +
			"FROM order_details od " +
			"JOIN product_details pd ON od.order_id = pd.order_id " +
			"JOIN product p ON pd.product_id = p.product_id " +
			"WHERE od.order_id = ?";

		try (Connection conn = getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, orderId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					ol = new OrderListInfo();
					ol.setOrderId(rs.getInt("order_id"));
					ol.setProductName(rs.getString("product_name"));
					ol.setProductPrice(rs.getInt("product_price"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
		return ol;
	}

	// 注文IDから商品IDを直接特定して返すメソッド
	public int getProductIdByOrderId(int orderId) throws SQLException {
		String sql = "SELECT product_id FROM product_details WHERE order_id = ?";

		try (Connection conn = getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, orderId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getInt("product_id");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
		return 0;
	}

	// 特定の商品IDに紐づくトッピング一覧（数量情報付き）を取得するメソッド
	public List<ItemDetailsInfo> findToppingListByProductId(int productId, int orderId) throws SQLException {
		List<ItemDetailsInfo> list = new ArrayList<>();
		String sql =
			"SELECT t.topping_id, t.topping_name, t.topping_price, t.topping_stock, " +
			"IFNULL(mt.topping_quantity, 0) AS topping_quantity " +
			"FROM product_topping pt " +
			"JOIN topping t ON pt.topping_id = t.topping_id " +
			"LEFT JOIN multiple_toppings mt ON t.topping_id = mt.topping_id AND mt.order_id = ? " +
			"WHERE pt.product_id = ?";

		try (Connection conn = getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, orderId);
			ps.setInt(2, productId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					ItemDetailsInfo t = new ItemDetailsInfo();
					t.setToppingId(rs.getInt("topping_id"));
					t.setToppingName(rs.getString("topping_name"));
					t.setToppingPrice(rs.getInt("topping_price"));
					t.setToppingStock(rs.getInt("topping_stock"));
					t.setToppingQuantity(rs.getInt("topping_quantity"));
					list.add(t);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
		return list;
	}

	// トッピング取得（全件ベース・互換性維持用）
	public List<ItemDetailsInfo> findToppingListByOrderId(int orderId) throws SQLException {
		List<ItemDetailsInfo> list = new ArrayList<>();
		String sql =
			"SELECT t.topping_id, t.topping_name, t.topping_price, t.topping_stock, " +
			"IFNULL(mt.topping_quantity,0) AS topping_quantity " +
			"FROM topping t " +
			"LEFT JOIN multiple_toppings mt " +
			"ON t.topping_id = mt.topping_id AND mt.order_id = ?";

		try (Connection conn = getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, orderId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					ItemDetailsInfo t = new ItemDetailsInfo();
					t.setToppingId(rs.getInt("topping_id"));
					t.setToppingName(rs.getString("topping_name"));
					t.setToppingPrice(rs.getInt("topping_price"));
					t.setToppingStock(rs.getInt("topping_stock"));
					t.setToppingQuantity(rs.getInt("topping_quantity"));
					list.add(t);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
		return list;
	}

	// トッピング削除
	public void deleteTopping(int orderId, int toppingId) throws SQLException {
		String sql = "DELETE FROM multiple_toppings WHERE order_id = ? AND topping_id = ?";

		try (Connection conn = getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, orderId);
			ps.setInt(2, toppingId);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	// トッピング数量の更新
	public void updateToppingQuantity(int orderId, int toppingId, int qty) throws SQLException {
		String sql = "UPDATE multiple_toppings SET topping_quantity = ? WHERE order_id = ? AND topping_id = ?";

		try (Connection conn = getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, qty);
			ps.setInt(2, orderId);
			ps.setInt(3, toppingId);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	// 注文総額の更新
	public void updateOrderPrice(int orderId, int orderPrice) throws SQLException {
		String sql = "UPDATE order_details SET order_price = ? WHERE order_id = ?";

		try (Connection conn = getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, orderPrice);
			ps.setInt(2, orderId);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	// 新規トッピング追加
	public void insertTopping(int orderId, int toppingId, int qty) throws SQLException {
		String sql = "INSERT INTO multiple_toppings (order_id, topping_id, topping_quantity) VALUES (?, ?, ?)";

		try (Connection conn = getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, orderId);
			ps.setInt(2, toppingId);
			ps.setInt(3, qty);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	// トッピング在庫数の更新（減算）
	public void updateToppingStock(int toppingId, int quantityDiff) throws SQLException {
		String sql = "UPDATE topping SET topping_stock = topping_stock - ? WHERE topping_id = ?";

		try (Connection conn = getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, quantityDiff);
			ps.setInt(2, toppingId);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}
}