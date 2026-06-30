package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderCompleteDAO {
	//DB接続情報
	private final String JDBC_URL = "jdbc:mysql://localhost:3306/order_management";
	private final String DB_USER = "order";
	private final String DB_PASS = "1234";


	public void updateOrderDetails() throws SQLException {
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		}catch(ClassNotFoundException e){
			throw new IllegalStateException("JDBCドライバを読み込めませんでした");
		}

		//DB接続
		try(Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS)){

			//order_details更新のsql
			// order_time に現在日時をセットする処理を追加
			String sql = "UPDATE order_details SET order_flag = 1, order_time = CURRENT_TIMESTAMP WHERE order_flag = 0";
			PreparedStatement pStmt = conn.prepareStatement(sql);
			int rs = pStmt.executeUpdate();

		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	
}
