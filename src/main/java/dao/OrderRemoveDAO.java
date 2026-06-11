package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderRemoveDAO {
	//DB接続情報
		private final String JDBC_URL = "jdbc:mysql://localhost:3306/order_management";
		private final String DB_USER = "order";
		private final String DB_PASS = "1234";
		
		public void deleteOrderDetails(int num) {
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
			}catch(ClassNotFoundException e){
				throw new IllegalStateException("JDBCドライバを読み込めませんでしたあ");
			}
			try(Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS)){
				
			String sql = "UPDATE topping t JOIN (SELECT topping_id, SUM(topping_quantity) AS total_quantity FROM multiple_toppings WHERE order_id = ? GROUP BY topping_id) a ON t.topping_id = a.topping_id SET t.topping_stock = t.topping_stock + a.total_quantity;";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, num);
			int rs = ps.executeUpdate();
			
			
				String sql2 = "DELETE od, mt, pd FROM order_details AS od "
						+ "LEFT JOIN multiple_toppings AS mt "
						+ "ON od.order_id = mt.order_id "
						+ "LEFT JOIN product_details AS pd "
						+ "ON mt.order_id = pd.order_id "
						+ "WHERE od.order_id = ? ";
				
				PreparedStatement ps2 = conn.prepareStatement(sql2);
				ps2.setInt(1, num);
				ps2.executeUpdate();
				
				
			}catch(SQLException e){
				e.printStackTrace();
				System.out.println("失敗");
			}
		}
}
