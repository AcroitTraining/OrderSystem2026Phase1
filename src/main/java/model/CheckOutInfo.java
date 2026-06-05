package model;

import java.io.Serializable;

public class CheckOutInfo implements Serializable {
	private String tableNumber;
	private int totalOrderPrice;
	
	public CheckOutInfo() {}
	
	public CheckOutInfo(String tableNumber, int totalOrderPrice) {
		this.tableNumber = tableNumber;
		this.totalOrderPrice = totalOrderPrice;
	}
	
	public String getTableNumber() { return tableNumber; }
	public void setTableNumber(String tableNumber) {
		this.tableNumber = tableNumber;
	}
	
	public int getTotalOrderPrice() { return totalOrderPrice; }
	public void setTotalOrderPrice(int totalOrderPrice) {
		this.totalOrderPrice = totalOrderPrice;
	}
}
