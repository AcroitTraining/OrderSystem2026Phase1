package model;

public class ItemDetailsInfo {

    private int productId;
    private String productName;
    private int productPrice;

    private int toppingId;
    private String toppingName;
    private int toppingPrice;
    private int toppingStock;
    private int toppingQuantity;

    private int orderId;
    private int sessionId;

    public ItemDetailsInfo(int orderId2, String toppingName2, String productName2, int orderPrice, int productPrice2,
			int toppingPrice2, int toppingQuantity2, int productQuantity, int sessionId2, int subTotal) {
		// TODO 自動生成されたコンストラクター・スタブ
	}
	public ItemDetailsInfo() {
		// TODO 自動生成されたコンストラクター・スタブ
	}
	public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public int getProductPrice() { return productPrice; }
    public void setProductPrice(int productPrice) { this.productPrice = productPrice; }
    public int getToppingId() { return toppingId; }
    public void setToppingId(int toppingId) { this.toppingId = toppingId; }
    public String getToppingName() { return toppingName; }
    public void setToppingName(String toppingName) { this.toppingName = toppingName; }
    public int getToppingPrice() { return toppingPrice; }
    public void setToppingPrice(int toppingPrice) { this.toppingPrice = toppingPrice; }
    public int getToppingStock() { return toppingStock; }
    public void setToppingStock(int toppingStock) { this.toppingStock = toppingStock; }
    public int getToppingQuantity() { return toppingQuantity; }
    public void setToppingQuantity(int toppingQuantity) { this.toppingQuantity = toppingQuantity; }
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getSessionId() { return sessionId; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }
	public void setOrderPrice(int orderPrice) {
		// TODO 自動生成されたメソッド・スタブ
		
	}
}