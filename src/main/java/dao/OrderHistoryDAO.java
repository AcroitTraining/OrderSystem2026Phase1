package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import model.OrderHistoryInfo;

public class OrderHistoryDAO {
    private final String JDBC_URL = "jdbc:mysql://localhost:3306/order_management";
    private final String DB_USER = "order";
    private final String DB_PASS = "1234";

    public List<OrderHistoryInfo> findOrderDetails(int sessionId) throws SQLException {
        // order_idをキーにして重複を防ぐためのMap
        Map<Integer, OrderHistoryInfo> map = new LinkedHashMap<>();
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("JDBCドライバを読み込めませんでした");
        }
        
        // 金額計算のために product_price と topping_price を取得
        String sql = 
                "SELECT od.order_id, od.product_quantity, od.session_id, od.order_flag, "
                + "p.product_name, p.product_price, "
                + "t.topping_name, t.topping_price, "
                + "mt.topping_quantity "
                + "FROM order_details AS od "
                + "LEFT JOIN product_details AS pd "
                + "ON od.order_id = pd.order_id "
                + "LEFT JOIN product AS p "
                + "ON pd.product_id = p.product_id "
                + "LEFT JOIN multiple_toppings AS mt "
                + "ON od.order_id = mt.order_id "
                + "LEFT JOIN topping AS t "
                + "ON mt.topping_id = t.topping_id "
                + "WHERE od.session_id = ? "
                + "AND od.order_flag = 1 "
                + "AND od.accounting_flag = 0 "
                + "ORDER BY od.order_id ASC";
        
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement pStmt = pStmtWithSession(conn, sql, sessionId)) {
            
            ResultSet rs = pStmt.executeQuery();
            
            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                
                OrderHistoryInfo info = map.get(orderId);
                if (info == null) {
                    info = new OrderHistoryInfo();
                    info.setOrderId(orderId);
                    info.setProductName(rs.getString("product_name"));
                    info.setOrderQuantity(rs.getInt("product_quantity"));
                    info.setOrderFlag(rs.getInt("order_flag"));
                    // 初期金額（商品単価 × 数量）
                    info.setSubTotal(rs.getInt("product_price") * rs.getInt("product_quantity"));
                    map.put(orderId, info);
                }
                
                String toppingName = rs.getString("topping_name");
                if (toppingName != null) {
                    int tQty = rs.getInt("topping_quantity");
                    int tPrice = rs.getInt("topping_price");
                    info.addTopping(toppingName, tQty);
                    // トッピング金額を加算 (トッピング単価 × 個数 × 商品の数量)
                    int currentSubTotal = info.getSubTotal();
                    info.setSubTotal(currentSubTotal + (tPrice * tQty * info.getOrderQuantity()));
                }
            }
        }
        return new ArrayList<>(map.values());
    }

    private PreparedStatement pStmtWithSession(Connection conn, String sql, int sessionId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, sessionId);
        return ps;
    }

    public void updateAccountingFlag(int sessionId) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("JDBCドライバを読み込めませんでした");
        }
        
        String sql = "UPDATE order_details "
        		+ "SET accounting_flag = 1 "
        		+ "WHERE session_id = ?";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement pStmt = conn.prepareStatement(sql)) {
            pStmt.setInt(1, sessionId);
            pStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}