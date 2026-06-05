package model;

import java.sql.SQLException;

import dao.OrderListDAO;

public class OrderListLogic {
	OrderListInfo ol = new OrderListInfo();
	OrderListDAO olDAO = new OrderListDAO();
	int toppingStock = ol.getToppingStock();
	int productStock = ol.getProductStock();
	int order = ol.getOrderQuantity();
	//String toppingQuantity = ol.getToppingQuantity();
	int orderPrice = ol.getOrderPrice();
	int productPrice = ol.getProductPrice();
	//String toppingPrice = ol.getToppingPrice();
	int subTotal = ol.getSubTotal();
	int allOrderPrice = ol.getAllOrderPrice();

	//public static void
	public void calcOrderQuantity(int n, int oid) {
		System.out.println("ロジック呼び出されました。calcOrderQuantity");

		/*//ストック上限の処理
		if(order >= 5) {
			order = 5;
			System.out.println("商品在庫上限");
			
		}else if(toppingQuantity >= 10){
			order = 10;
			System.out.println("トッピング在庫上限");
		}else if(order <= 1) {
			order = 1;
			System.out.println("商品下限");
		}else {*/
			//オーダーの商品数の計算
			order = order + n;
			//toppingQuantity = toppingQuantity * order;k
			subTotal = orderPrice * order;
			
			ol.setSubTotal(subTotal);
			ol.setOrderQuantity(order);
			//ol.setToppingQuantity(toppingQuantity);
			

			try {
				olDAO.updateOrderDetails(n, oid);
				olDAO.updateStock(oid, n);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			System.out.println("商品" + order +"分追加されました。");
		//}
	}


	public void calcSubTotal() {
		int orderPrice = ol.getOrderPrice();
		int subTotal = orderPrice * order;
		ol.setSubTotal(subTotal);
		calcAllOrderPrice(subTotal);
	}
	
	public void calcAllOrderPrice(int subTotal) {
		int num = ol.getAllOrderPrice();
		int aop = num + subTotal;
		ol.setAllOrderPrice(aop);
	}


}
