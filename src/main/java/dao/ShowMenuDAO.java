package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.ProductInfo;

public class ShowMenuDAO {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/order_management";
    private static final String DB_USER = "order";
    private static final String DB_PASS = "1234";

    /**
     * 表示対象(flag=1)かつ未削除(flag=1)のカテゴリ一覧をIDと名前のセットで取得
     */
    public List<ProductInfo> findAllCategories() {
        List<ProductInfo> categoryList = new ArrayList<>();
        String sql = "SELECT category_id, category_name FROM category " +
                     "WHERE (category_display_flag = 1 OR category_display_flag = true) " +
                     "  AND (category_delete_flag = 1 OR category_delete_flag = true) " +
                     "ORDER BY category_id ASC";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProductInfo cat = new ProductInfo();
                    cat.setCategoryId(rs.getInt("category_id"));
                    cat.setCategoryName(rs.getString("category_name"));
                    categoryList.add(cat);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return categoryList;
    }

    /**
     * 商品一覧を取得（p.category_id も併せて取得する）
     */
    public List<ProductInfo> findProductTable() {
        List<ProductInfo> productList = new ArrayList<>();
        
        String sql =
            "SELECT p.product_id, p.product_name, p.category_id, c.category_name, " +
            "p.product_price, p.product_stock, p.product_display_flag " +
            "FROM product p " +
            "JOIN category c ON p.category_id = c.category_id";
            
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productList.add(new ProductInfo(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getInt("category_id"), // ★category_idをセット
                        rs.getString("category_name"),
                        rs.getInt("product_price"),
                        rs.getInt("product_stock"),
                        rs.getInt("product_display_flag")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return productList;
    }

    public int getOrderItemCount(String sessionId) {
        int count = 0;
        String sql = "SELECT SUM(product_quantity) AS cnt "
                + "FROM order_details "
                + "WHERE session_id = ? AND order_flag = 0";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, Integer.parseInt(sessionId));
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    count = rs.getInt("cnt");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }
}