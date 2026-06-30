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

import model.OrderListInfo;

public class OrderListDAO {
	//DB接続情報
	private final String JDBC_URL = "jdbc:mysql://localhost:3306/order_management";
	private final String DB_USER = "order";
	private final String DB_PASS = "1234";


	public List<OrderListInfo> findorderDetails(int sessionId) throws SQLException {

		Map<Integer, OrderListInfo> map = new LinkedHashMap<>();
		//JDBCドライバを読み込む
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		}catch(ClassNotFoundException e){
			throw new IllegalStateException("JDBCドライバを読み込めませんでしたあ♡");
		}

		try(Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS)){
			String sql = 
	                "SELECT od.order_id, od.product_quantity, od.session_id, od.order_flag, "
	                + "p.product_name, p.product_price, od.order_price, p.category_name, "
	                + "t.topping_name, t.topping_price, t.topping_stock, p.product_stock, "
	                + "mt.topping_quantity, (od.product_quantity * od.order_price) AS sub_total "
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
	                + "AND od.order_flag = 0 "
	                + "AND od.accounting_flag = 0 "
	                + "ORDER BY od.order_id ASC";
			PreparedStatement pStmt = conn.prepareStatement(sql);
			pStmt.setInt(1, sessionId);
			ResultSet rs = pStmt.executeQuery();
			
			while (rs.next()) {
                int orderId = rs.getInt("order_id");
                
                OrderListInfo info = map.get(orderId);
                if (info == null) {
                    info = new OrderListInfo();
                    info.setOrderId(orderId);
                    info.setCategoryName(rs.getString("category_name"));
                    info.setProductName(rs.getString("product_name"));
                    info.setOrderQuantity(rs.getInt("product_quantity"));
                    info.setOrderFlag(rs.getInt("order_flag"));
                    info.setOrderPrice(rs.getInt("order_price"));
                    info.setProductPrice(rs.getInt("product_price"));
                    info.setProductStock(rs.getInt("product_stock"));
                    info.setToppingStock(rs.getInt("topping_stock"));
                    info.setToppingQuantity(rs.getInt("topping_quantity"));
                    info.setToppingPrice(rs.getInt("topping_price"));
                    // 初期金額（商品単価 × 数量）
                    info.setSubTotal(rs.getInt("product_price") * rs.getInt("product_quantity"));
                    map.put(orderId, info);
                }
                
                String toppingName = rs.getString("topping_name");
                if (toppingName != null) {
                    int tQty = rs.getInt("topping_quantity");
                    int tPrice = rs.getInt("topping_price");
                    info.addTopping(toppingName, tQty, tPrice);
                    // トッピング金額を加算 (トッピング単価 × 個数 × 商品の数量)
                    int currentSubTotal = info.getSubTotal();
                    info.setSubTotal(currentSubTotal + (tPrice * tQty * info.getOrderQuantity()));
                }
                int cname = info.getToppingStock();
            }

			}catch(SQLException e){
				e.printStackTrace();
			}
			return new ArrayList<>(map.values());
	}



		public void updateOrderDetails(int n, int oid) throws SQLException {
			//JDBCドライバを読み込む
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
			}catch(ClassNotFoundException e){
				throw new IllegalStateException("JDBCドライバを読み込めませんでした");
			}

			//DB接続
			try(Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS)){

				//order_details更新のsql
				if(n > 0) {
					String sql = "UPDATE order_details SET product_quantity = CASE WHEN product_quantity < 10 then product_quantity + 1 ELSE product_quantity = 10 END WHERE order_id = ?";
					PreparedStatement ps = conn.prepareStatement(sql);
					ps.setInt(1, oid);
					int rs = ps.executeUpdate();
				}else {
					String sql = "UPDATE order_details SET product_quantity = CASE WHEN product_quantity > 1 then product_quantity - 1 ELSE product_quantity = 1 END WHERE order_id = ?";
					PreparedStatement ps = conn.prepareStatement(sql);
					ps.setInt(1, oid);
					int rs = ps.executeUpdate();
				}


			}catch(SQLException e) {
				e.printStackTrace();
			}
		}

		public OrderListInfo findAllOrderPrice(int sid)throws SQLException{
			OrderListInfo ol2 = null;
			//JDBCドライバを読み込む
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
			}catch(ClassNotFoundException e){
				throw new IllegalStateException("JDBCドライバを読み込めませんでした");
			}

			//DB接続
			try(Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS)){
				String sql = "SELECT SUM(product_quantity * order_price) AS all_order_price FROM order_details WHERE order_flag = 0 AND session_id = ? ";

				PreparedStatement pStmt = conn.prepareStatement(sql);
				pStmt.setInt(1, sid);
				ResultSet rs = pStmt.executeQuery();
				if(rs.next()) {
					int allOrderPrice = rs.getInt("all_order_price");
					ol2 = new OrderListInfo(allOrderPrice);
					ol2.setAllOrderPrice(allOrderPrice);
					int goukei = ol2.getAllOrderPrice();

				}
			}catch(SQLException e) {
				e.printStackTrace();
			}
			return ol2;
		}
		
		public void updateStock(int oid, int n) {
			//JDBCドライバを読み込む
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
			}catch(ClassNotFoundException e){
				throw new IllegalStateException("JDBCドライバを読み込めませんでした");
			}

			//DB接続
			try(Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS)){

				//order_details更新のsql
				if(n > 0) {
					String sql = "UPDATE order_details AS od "
							+ "LEFT JOIN multiple_toppings AS mt "
							+ "ON od.order_id = mt.order_id "
							+ "LEFT JOIN product_details AS pd "
							+ "ON pd.order_id = od.order_id "
							+ "LEFT JOIN product AS p "
							+ "ON pd.product_id = p.product_id "
							+ "LEFT JOIN topping AS t "
							+ "ON mt.topping_id = t.topping_id "
							+ "SET p.product_stock = p.product_stock - 1 "
							+ "WHERE od.order_id = ? AND order_flag = 0 ";
					PreparedStatement ps = conn.prepareStatement(sql);
					ps.setInt(1, oid);
					int rs = ps.executeUpdate();
				}else {
					String sql = "UPDATE order_details AS od "
							+ "LEFT JOIN multiple_toppings AS mt "
							+ "ON od.order_id = mt.order_id "
							+ "LEFT JOIN product AS p "
							+ "ON od.product_id = p.product_id "
							+ "LEFT JOIN topping AS t "
							+ "ON mt.topping_id = t.topping_id "
							+ "SET p.product_stock = p.product_stock + 1"
							+ "WHERE od.order_id = ? AND order_flag = 0 ";
					PreparedStatement ps = conn.prepareStatement(sql);
					ps.setInt(1, oid);
					int rs = ps.executeUpdate();
				}
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		public void updateToppingStock(int oid, int n) {
			//JDBCドライバを読み込む
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
			}catch(ClassNotFoundException e){
				throw new IllegalStateException("JDBCドライバを読み込めませんでした");
			}

			//DB接続
			try(Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS)){

				//order_details更新のsql
				if(n > 0) {
					String sql = "UPDATE topping t JOIN (SELECT topping_id, SUM(topping_quantity) AS total_quantity FROM multiple_toppings WHERE order_id = ? GROUP BY topping_id) a ON t.topping_id = a.topping_id SET t.topping_stock = t.topping_stock - a.total_quantity;";
					PreparedStatement ps = conn.prepareStatement(sql);
					ps.setInt(1, oid);
					int rs = ps.executeUpdate();
				}else {
					String sql = "UPDATE topping t JOIN (SELECT topping_id, SUM(topping_quantity) AS total_quantity FROM multiple_toppings WHERE order_id = ? GROUP BY topping_id) a ON t.topping_id = a.topping_id SET t.topping_stock = t.topping_stock + a.total_quantity;";
					PreparedStatement ps = conn.prepareStatement(sql);
					ps.setInt(1, oid);
					int rs = ps.executeUpdate();
				}


			}catch(SQLException e) {
				e.printStackTrace();
			}
		}

	}
