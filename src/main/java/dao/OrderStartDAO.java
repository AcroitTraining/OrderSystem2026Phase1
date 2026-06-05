package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderStartDAO {
    private final String JDBC_URL = "jdbc:mysql://localhost:3306/order_management";
    private final String DB_USER = "order";
    private final String DB_PASS = "1234";

    public void updateStatus(int tableId, int guestCount) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("JDBCドライバを読み込めませんでした");
        }

        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS)) {
            conn.setAutoCommit(false);

            try {
                String tmSql = 
                		"UPDATE table_master "
                		+ "SET table_status = 'active', updated_at = NOW() "
                		+ "WHERE table_id = ?";
                
                try (PreparedStatement pStmt1 = conn.prepareStatement(tmSql)) {
                    pStmt1.setInt(1, tableId);
                    pStmt1.executeUpdate();
                }

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
        }
    }

    public int findSessionId(int tableId) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("JDBCドライバを読み込めませんでした");
        }

        int sessionId = 0;
        String sql = "SELECT table_id "
                   + "FROM table_sessions "
                   + "WHERE table_id = ? "
                   + "AND session_status = 'active'";
        
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement pStmt = conn.prepareStatement(sql)) {
            
            pStmt.setInt(1, tableId);
            try (ResultSet rs = pStmt.executeQuery()) {
                if (rs.next()) {
                    sessionId = rs.getInt("table_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sessionId;
    }
}