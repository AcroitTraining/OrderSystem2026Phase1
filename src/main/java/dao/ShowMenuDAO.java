package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import model.ProductInfo;

public class ShowMenuDAO {

	private final DataSource dataSource;

	/**
	 * ① 通常運用（Servletなど）で使うデフォルトコンストラクタ
	 */
	public ShowMenuDAO() {
		this(DBConnection.getDataSource());
	}

	/**
	 * ② DIコンストラクタ。DataSourceを外部から注入する。
	 *
	 * @param dataSource コネクション取得元のDataSource
	 */
	public ShowMenuDAO(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/** メソッド呼び出しのたびに新しい接続を取得する */
	private Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	/**
	 * 表示対象(flag=1)かつ未削除(flag=1)のカテゴリ一覧をIDと名前のセットで取得
	 */
	public List<ProductInfo> findAllCategories() throws SQLException {
		List<ProductInfo> categoryList = new ArrayList<>();
		String sql = "SELECT category_id, category_name FROM category " +
					 "WHERE (category_display_flag = 1 OR category_display_flag = true) " +
					 "  AND (category_delete_flag = 1 OR category_delete_flag = true) " +
					 "ORDER BY category_id ASC";

		try (Connection conn = getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				ProductInfo cat = new ProductInfo();
				cat.setCategoryId(rs.getInt("category_id"));
				cat.setCategoryName(rs.getString("category_name"));
				categoryList.add(cat);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
		return categoryList;
	}

	/**
	 * 商品一覧を取得（p.category_id も併せて取得する）
	 */
	public List<ProductInfo> findProductTable() throws SQLException {
		List<ProductInfo> productList = new ArrayList<>();

		String sql =
			"SELECT p.product_id, p.product_name, p.category_id, c.category_name, " +
			"p.product_price, p.product_stock, p.product_display_flag " +
			"FROM product p " +
			"JOIN category c ON p.category_id = c.category_id";

		try (Connection conn = getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				productList.add(new ProductInfo(
					rs.getInt("product_id"),
					rs.getString("product_name"),
					rs.getInt("category_id"), // category_idをセット
					rs.getString("category_name"),
					rs.getInt("product_price"),
					rs.getInt("product_stock"),
					rs.getInt("product_display_flag")
				));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
		return productList;
	}

	/**
	 * カート内の注文商品数を取得する
	 */
	public int getOrderItemCount(String sessionId) throws SQLException {
		if (sessionId == null || sessionId.trim().isEmpty()) {
			return 0;
		}

		int count = 0;
		String sql = "SELECT SUM(product_quantity) AS cnt "
				+ "FROM order_details "
				+ "WHERE session_id = ? AND order_flag = 0";

		try (Connection conn = getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, Integer.parseInt(sessionId));
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					count = rs.getInt("cnt");
				}
			}
		} catch (NumberFormatException e) {
			return 0;
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
		return count;
	}
}