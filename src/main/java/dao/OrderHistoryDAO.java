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
        Map<Integer, OrderHistoryInfo> map = new LinkedHashMap<>();
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("JDBCドライバを読み込めませんでした");
        }
        
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
                + "AND od.served_flag = 1 "
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
                    info.setSubTotal(rs.getInt("product_price") * rs.getInt("product_quantity"));
                    map.put(orderId, info);
                }
                
                String toppingName = rs.getString("topping_name");
                if (toppingName != null) {
                    int tQty = rs.getInt("topping_quantity");
                    int tPrice = rs.getInt("topping_price");
                    info.addTopping(toppingName, tQty);
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

    /**
     * 会計確定処理（既存のDB構造に適合した完全自動更新版）
     */
    public void processCheckoutAndGenerateNewToken(int tableId) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("JDBCドライバを読み込めませんでした");
        }

        // 1. 会計フラグON
        String updateDetailsSql = "UPDATE order_details SET accounting_flag = 1 WHERE session_id = ?";
        // 2. 旧セッションを closed にして end_time をセット（旧URLを閉める）
        String endSessionSql = "UPDATE table_sessions SET session_status = 'closed', end_time = NOW() WHERE table_id = ? AND session_status = 'active'";
        // 3. 次のお客用の新URLトークンを active として作成
        String createTokenSql = "INSERT INTO table_sessions (table_id, session_status, url_token, start_time, guest_count) VALUES (?, 'active', REPLACE(UUID(), '-', ''), NOW(), 1)";
        // 4. Sender通知用の update_flag を 1 に更新
        String updateMasterSql = "UPDATE table_master SET update_flag = 1 WHERE table_id = ?";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS)) {
            conn.setAutoCommit(false); // トランザクション開始

            try (PreparedStatement ps1 = conn.prepareStatement(updateDetailsSql);
                 PreparedStatement ps2 = conn.prepareStatement(endSessionSql);
                 PreparedStatement ps3 = conn.prepareStatement(createTokenSql);
                 PreparedStatement ps4 = conn.prepareStatement(updateMasterSql)) {

                ps1.setInt(1, tableId);
                ps1.executeUpdate();

                ps2.setInt(1, tableId);
                ps2.executeUpdate();

                ps3.setInt(1, tableId);
                ps3.executeUpdate();

                ps4.setInt(1, tableId);
                ps4.executeUpdate();

                conn.commit();
                System.out.println("★ [会計完了] 卓番 " + tableId + " の旧URLを閉じ、新URLトークンを発行しました（Sender起動準備完了）。");

            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}