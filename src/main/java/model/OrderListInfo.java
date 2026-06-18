package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



public class OrderListInfo implements Serializable{
	//private int[] productToppingList;
	private String productName, toppingName, categoryName;
	

	private int orderId, toppingQuantity, toppingPrice, subTotal, productPrice,orderQuantity, toppingStock, productStock, allOrderPrice, sessionId, orderPrice, orderFlag;
	
	private int productQuantity;
	
	private List<ToppingList> toppings = new ArrayList<>();


	    // トッピング情報の内部クラスdes
	    public static class ToppingList implements Serializable {
	        private String name;
	        private int quantity;
	        private int price;
	        public ToppingList(String name, int quantity, int price) {
	            this.name = name;
	            this.quantity = quantity;
	            this.price = price;
	        }
	        public String getName() { return name; }
	        public int getQuantity() { return quantity; }
	        public int getprice() { return price; }
	    }
	
	public OrderListInfo(int allOrderPrice) {this.allOrderPrice = allOrderPrice;}	
		
	public OrderListInfo() {}
	
	public int getSessionId() {
		return sessionId;
	}
	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public int getAllOrderPrice() {
		return allOrderPrice;
	}
	public void setAllOrderPrice(int allOrderPrice) {
		this.allOrderPrice = allOrderPrice;
	}
	public int getToppingStock() {
		return toppingStock;
	}
	public void setToppingStock(int toppingStock) {
		this.toppingStock = toppingStock;
	}
	public int getProductStock() {
		return productStock;
	}
	public void setProductStock(int productStock) {
		this.productStock = productStock;
	}
	public int getOrderQuantity() {
		return orderQuantity;
	}
	public void setOrderQuantity(int orderQuantity) {
		this.orderQuantity = orderQuantity;
	}	
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getToppingName() {
		return toppingName;
	}
	public void setToppingName(String toppingName) {
		this.toppingName = toppingName;
	}
	public int getSubTotal() {
		return subTotal;
	}
	public void setSubTotal(int subTotal) {
		this.subTotal = subTotal;
	}
	public int getProductPrice() {
		return productPrice;
	}
	public void setProductPrice(int productPrice) {
		this.productPrice = productPrice;
	}
	public int getToppingPrice() {
		return toppingPrice;
	}
	public void setToppingPrice(int toppingPrice) {
		this.toppingPrice = toppingPrice;
	}
	public int getToppingQuantity() {
		return toppingQuantity;
	}
	public void setToppingQuantity(int toppingQuantity) {
		this.toppingQuantity = toppingQuantity;
	}

	public int getProductQuantity() {
		return productQuantity;
	}

	public void setProductQuantity(int productQuantity) {
		this.productQuantity = productQuantity;
	}
	public int getOrderPrice() {
		return orderPrice;
	}
	public void setOrderPrice(int orderPrice) {
		this.orderPrice = orderPrice;
	}
	public List<ToppingList> getToppings() { return toppings; }
	public void addTopping(String name, int quantity, int price) {
		if (name != null) {
			this.toppings.add(new ToppingList(name, quantity, price));
		}	
	}

	public int getOrderFlag() {
		return orderFlag;
	}

	public void setOrderFlag(int orderFlag) {
		this.orderFlag = orderFlag;
	}
	
	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
		
	}
	
}