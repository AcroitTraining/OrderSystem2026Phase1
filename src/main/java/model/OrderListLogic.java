package model;

import java.sql.SQLException;

import dao.OrderListDAO;

public class OrderListLogic {
	OrderListInfo ol = new OrderListInfo();
	OrderListDAO olDAO = new OrderListDAO();
	int toppingStock = ol.getToppingStock();
	int productStock = ol.getProductStock();
	int order = ol.getOrderQuantity();
	int orderPrice = ol.getOrderPrice();
	int productPrice = ol.getProductPrice();
	int subTotal = ol.getSubTotal();
	int allOrderPrice = ol.getAllOrderPrice();
	
	public void calcOrderQuantity(int n, int oid) {
			order = order + n;
			subTotal = orderPrice * order;
			
			ol.setSubTotal(subTotal);
			ol.setOrderQuantity(order);		

			try {
				olDAO.updateOrderDetails(n, oid);
				olDAO.updateStock(oid, n);
				olDAO.updateToppingStock(oid, n);
			} catch (SQLException e) {
				e.printStackTrace();
			}
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
