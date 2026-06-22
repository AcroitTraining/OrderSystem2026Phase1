<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.ItemDetailsInfo" %>
<%@ page import="model.OrderListInfo" %>

<%
OrderListInfo ol = (OrderListInfo)request.getAttribute("ol");
if (ol == null) {
    response.sendRedirect("OrderListServlet");
    return;
}

int orderId = ol.getOrderId();
String productName = ol.getProductName();
int productPrice = ol.getProductPrice();

String tableNum = (String)session.getAttribute("tableNumber");
if (tableNum == null) {
    tableNum = "-";
}

List<ItemDetailsInfo> toppingList = (List<ItemDetailsInfo>)request.getAttribute("toppingList");
Integer subTotal = (Integer)request.getAttribute("subTotal");

if (subTotal == null) {
    subTotal = productPrice;
}
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>商品変更画面</title>
<link rel="stylesheet" href="./css/common.css?v=3">
<link rel="stylesheet" href="./css/itemDetails.css?v=3">
<script src="./js/windowScaler.js"></script>
</head>
<body>
<script src="./js/itemDetails.js"></script>

<header class="header-area">
  <img src="./image/木目3.jpg" alt="背景" class="bg-img">
  <img src="./image/biglogo.png" alt="ロゴ" class="logo-img">
</header>

<div class="main-content">
	<div class="product-header-box">
		<div class="product-main-name"><%= productName %></div>
		<%-- ★3桁カンマ区切りに変更 --%>
		<div class="product-main-price"><%= String.format("%,d", productPrice) %>円(税込み)</div>
	</div>

	<% if (toppingList != null && !toppingList.isEmpty()) { 
		// 現在のトッピング全体の合計選択数を算出する
		int totalToppingQty = 0;
		for (ItemDetailsInfo t : toppingList) {
			totalToppingQty += t.getToppingQuantity();
		}
	%>
	<div class="topping-area">
		<div class="topping-title">トッピング変更</div>
		<table class="topping-table">
		<%
		for (int i = 0; i < toppingList.size(); i++) {
		    ItemDetailsInfo t = toppingList.get(i);
		%>
		<tr class="topping-item-row">
			<td align="left" valign="middle" class="topping-info-cell">
				<span class="topping-name"><%= t.getToppingName() %> ： </span>
				<%-- 3桁カンマ区切りに変更 --%>
				<span class="topping-price"><%= String.format("%,d", t.getToppingPrice()) %>円</span>
			</td>
			<td align="right" valign="middle" class="topping-action-cell">
			<% if (t.getToppingStock() > 0) { %>
				<form action="ItemDetailsChangeServlet" method="post" class="quantity-form">
					<input type="hidden" name="orderId" value="<%= orderId %>">
					<% for (int j = 0; j < toppingList.size(); j++) { %>
					<input type="hidden" name="oldQty_<%= j %>" value="<%= toppingList.get(j).getToppingQuantity() %>">
					<% } %>

					<button type="submit" name="Button" value="-<%= i %>" class="btn-qty" <%= (t.getToppingQuantity() <= 0) ? "disabled" : "" %>>－</button>
					<span class="qty-text"><%= t.getToppingQuantity() %></span>
					
					<button type="submit" name="Button" value="+<%= i %>" class="btn-qty" <%= (totalToppingQty >= 4 || t.getToppingQuantity() >= t.getToppingStock()) ? "disabled" : "" %>>＋</button>
				</form>
			<% } else { %>

				<div class="quantity-form">
					<img src="./image/soldout.png" alt="売切" class="img-sold-out">
				</div>
			<% } %>
			</td>
		</tr>
		<%
		}
		%>
		</table>
	</div>
	<% } %>
</div>

<div class="subtotal-box">
	<%-- 3桁カンマ区切りに変更 --%>
	<div class="subtotal-text">小計:<%= String.format("%,d", subTotal) %>円(税込み)</div>
</div>

<footer>
	<table class="footer-table">
		<tr>
			<td>
				<form action="OrderListServlet" method="get">
					<button type="submit" class="btn-footer btn-green-style"><img src="./image/menu.png" alt="注文リストアイコン"><span>戻る</span></button>
				</form>
			</td>
			<td>
				<div class="table-num"><%= tableNum %>卓</div>
			</td>
			<td>
				<form action="ItemDetailsChangeServlet" method="post">
					<input type="hidden" name="mode" value="update">
					<input type="hidden" name="orderId" value="<%= orderId %>">
					<% if (toppingList != null) {
					    for (int j = 0; j < toppingList.size(); j++) { %>
					<input type="hidden" name="oldQty_<%= j %>" value="<%= toppingList.get(j).getToppingQuantity() %>">
					<%  }
					} %>
					<button type="submit" name="Button" value="変更" class="btn-footer btn-orange-style"><img src="./image/changeCart.png" alt="更新アイコン"><span>変更</span></button>
				</form>
			</td>
		</tr>
	</table>
</footer>

</body>
</html>