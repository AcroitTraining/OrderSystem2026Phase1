package model;

public class OrderCompleteInfo {
private String tableNumberk ;
private int orderId, productId, productQuantity, orderPrice, toppingId, toppingPrice, toppingQuantity;
public String getTableNumberk() {
	return tableNumberk;
}
public void setTableNumberk(String tableNumberk) {
	this.tableNumberk = tableNumberk;
}
public int getOrderId() {
	return orderId;
}
public void setOrderId(int orderId) {
	this.orderId = orderId;
}
public int getProductId() {
	return productId;
}
public void setProductId(int productId) {
	this.productId = productId;
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
public int getToppingId() {
	return toppingId;
}
public void setToppingId(int toppingId) {
	this.toppingId = toppingId;
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
}
