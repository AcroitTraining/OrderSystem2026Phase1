package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import model.ItemDetailsInfo;
public class ToppingListDAO {

    private final String JDBC_URL = "jdbc:mysql://localhost:3306/order_management";
    private final String DB_USER = "order";
    private final String DB_PASS = "1234";

    public List<ItemDetailsInfo> findToppingListByOrderId(int orderId) {
        List<ItemDetailsInfo> list = new ArrayList<>();
        String sql =
                "SELECT t.topping_id, t.topping_name, t.topping_price, t.topping_stock, " +
                "IFNULL(mt.topping_quantity,0) AS topping_quantity " +
                "FROM topping t " +
                "LEFT JOIN multiple_toppings mt " +
                "ON t.topping_id = mt.topping_id AND mt.order_id = ?";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ItemDetailsInfo t = new ItemDetailsInfo();

                t.setToppingId(rs.getInt("topping_id"));
                t.setToppingName(rs.getString("topping_name"));
                t.setToppingPrice(rs.getInt("topping_price"));
                t.setToppingStock(rs.getInt("topping_stock"));
                t.setToppingQuantity(rs.getInt("topping_quantity"));

                list.add(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    // product_details INSERT
    public boolean insertProductDetail(int productId) {
        String sql = "INSERT INTO product_details (product_id) VALUES (?)";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    // 最新order_id
    public int getLastOrderId() {

        String sql = "SELECT MAX(order_id) FROM product_details";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    // order_details insert 
    public boolean insertOrderDetail(
            int orderId,
            int productQuantity,
            int orderPrice,
            int sessionId,
            int orderTime,
            int orderFlag,
            int accountingFlag,
            int productId,
            int toppingId) {
        String sql =
                "INSERT INTO order_details " +
                "(order_id, product_quantity, order_price, session_id, order_time, order_flag, accounting_flag, product_id, topping_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, productQuantity);
            ps.setInt(3, orderPrice);
            ps.setInt(4, sessionId);
            ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            ps.setInt(6, orderFlag);
            ps.setInt(7, accountingFlag);
            ps.setInt(8, productId);
            ps.setInt(9, toppingId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    // トッピング追加
    public boolean insertMutipleToppings(int toppingId, int qty, int orderId) {
        String sql =
                "INSERT INTO multiple_toppings (topping_id, topping_quantity, order_id) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, toppingId);
            ps.setInt(2, qty);
            ps.setInt(3, orderId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}