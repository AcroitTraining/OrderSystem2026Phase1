package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.ItemDetailsInfo;
import model.OrderListInfo;

public class ToppingDAO {
    private final String JDBC_URL = "jdbc:mysql://localhost:3306/order_management";
    private final String DB_USER = "order";
    private final String DB_PASS = "1234";
    public OrderListInfo findOrderInfo(int orderId) {
        OrderListInfo ol = null;
        String sql =
            "SELECT od.order_id, p.product_name, p.product_price " +
            "FROM order_details od " +
            "JOIN product p ON od.product_id = p.product_id " +
            "WHERE od.order_id = ?";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ol = new OrderListInfo();
                ol.setOrderId(rs.getInt("order_id"));
                ol.setProductName(rs.getString("product_name"));
                ol.setProductPrice(rs.getInt("product_price"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ol;
    }
    
    // トッピング取得
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


 // トッピング d e l e t e
 public void deleteTopping(int orderId, int toppingId) {
     String sql =
         "DELETE FROM multiple_toppings " +
         "WHERE order_id = ? AND topping_id = ?";
     try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
          PreparedStatement ps = conn.prepareStatement(sql)) {
         ps.setInt(1, orderId);
         ps.setInt(2, toppingId);
         ps.executeUpdate();
     } catch (Exception e) {
         e.printStackTrace();
     }
 }
    // inser and update
    public void updateToppingQuantity(int orderId, int toppingId, int qty) {
        String sql =
            "UPDATE multiple_toppings " +
            "SET topping_quantity = ? " +
            "WHERE order_id = ? AND topping_id = ?";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, qty);
            ps.setInt(2, orderId);
            ps.setInt(3, toppingId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 新しいinsert
    public void insertTopping(int orderId, int toppingId, int qty) {
        String sql =
            "INSERT INTO multiple_toppings (order_id, topping_id, topping_quantity) " +
            "VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, toppingId);
            ps.setInt(3, qty);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}