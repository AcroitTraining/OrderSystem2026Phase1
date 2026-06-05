package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OrderHistoryInfo implements Serializable {
    private int orderId;
    private int orderFlag; 
    private String productName;
    private int orderQuantity;
    private int subTotal;
    // トッピング情報を保持するリスト
    private List<ToppingDetail> toppings = new ArrayList<>();

    // トッピング情報の内部クラス
    public static class ToppingDetail implements Serializable {
        private String name;
        private int quantity;
        public ToppingDetail(String name, int quantity) {
            this.name = name;
            this.quantity = quantity;
        }
        public String getName() { return name; }
        public int getQuantity() { return quantity; }
    }

    public OrderHistoryInfo() {}

    public void addTopping(String name, int quantity) {
        if (name != null) {
            this.toppings.add(new ToppingDetail(name, quantity));
        }
    }

    // Getter / Setter
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) {
    	this.orderId = orderId;
    	}
    public int getOrderFlag() { return orderFlag; }
    public void setOrderFlag(int orderFlag) {
    	this.orderFlag = orderFlag;
    	}
    public String getProductName() { return productName; }
    public void setProductName(String productName) {
    	this.productName = productName;
    	}
    public int getOrderQuantity() { return orderQuantity; }
    public void setOrderQuantity(int orderQuantity) {
    	this.orderQuantity = orderQuantity;
    	}
    public int getSubTotal() { return subTotal; }
    public void setSubTotal(int subTotal) {
    	this.subTotal = subTotal;
    	}
    public List<ToppingDetail> getToppings() { return toppings; }
}