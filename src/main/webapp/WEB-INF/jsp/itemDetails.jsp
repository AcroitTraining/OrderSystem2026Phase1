<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, model.ItemDetailsInfo" %>
<%@ page import="java.util.Set" %>

<%
String productId = String.valueOf(request.getAttribute("productId"));
String pName = String.valueOf(request.getAttribute("selectedPName"));
Integer pPrice = (Integer)request.getAttribute("selectedPPrice");
List<ItemDetailsInfo> toppingList = (List<ItemDetailsInfo>)request.getAttribute("toppingList");
Integer subTotal = (Integer)request.getAttribute("subTotal");
String category = (String)request.getAttribute("currentCategory");
String tableNum = (String)session.getAttribute("tableNumber");
String formAction = (String)request.getAttribute("formAction");

if (category == null) {
    category = "";
}
if (tableNum == null) {
    tableNum = "-";
}
Set<String> toppingCategories = Set.of("お好み焼き","もんじゃ焼き");
boolean showTopping = toppingCategories.contains(category);

if (formAction == null) {
    formAction = "ItemDetailsServlet";
}
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>商品詳細</title>
<link rel="stylesheet" href="./css/itemDetails.css">
<script src="./js/windowScaler.js"></script>
</head>
<body>

<header class="header-area">
  <img src="./image/木目3.jpg" alt="背景" class="bg-img">
  <img src="./image/biglogo.png" alt="ロゴ" class="logo-img">
</header>

<div class="main-content">
	<div class="product-header-box">
		<div class="product-main-name"><%= pName %></div>
		<div class="product-main-price"><%= pPrice %>円(税込)</div>
	</div>

	<% if (showTopping && toppingList != null && !toppingList.isEmpty()) { %>
	<div class="topping-area">
		<div class="topping-title">トッピング</div>
		<table class="topping-table">
		<%
		for (int i = 0; i < toppingList.size(); i++) {
		    ItemDetailsInfo t = toppingList.get(i);
		%>
		<tr class="topping-item-row">
			<td align="left" valign="middle" class="topping-info-cell">
				<span class="topping-name"><%= t.getToppingName() %> ： </span>
				<span class="topping-price"><%= t.getToppingPrice() %>円</span>
			</td>
			<td align="right" valign="middle" class="topping-action-cell">
			<% if (t.getToppingStock() > 0) { %>
				<form action="<%= formAction %>" method="post" class="quantity-form">
					<input type="hidden" name="productId" value="<%= productId %>">
					<input type="hidden" name="productName" value="<%= pName %>">
					<input type="hidden" name="productPrice" value="<%= pPrice %>">
					<input type="hidden" name="productCategory" value="<%= category %>">
					<input type="hidden" name="subTotal" value="<%= subTotal %>">
					<input type="hidden" name="orderId" value="<%= request.getAttribute("orderId") %>">
					<input type="hidden" name="mode" value="view">
					<% for (int j = 0; j < toppingList.size(); j++) { %>
					<input type="hidden" name="oldQty_<%= j %>" value="<%= toppingList.get(j).getToppingQuantity() %>">
					<% } %>

					<button type="submit" name="Button" value="-<%= i %>" class="btn-qty" <%= (t.getToppingQuantity() <= 0) ? "disabled" : "" %>>
						－
					</button>
					
					<span class="qty-text"><%= t.getToppingQuantity() %></span>
					
					<button type="submit" name="Button" value="+<%= i %>" class="btn-qty" <%= (t.getToppingQuantity() >= 20) ? "disabled" : "" %>>
						＋
					</button>
				</form>
			<% } else { %>
				<span class="sold-out-text">売切</span>
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

<div class="fixed-footer-container">
	
	<div class="subtotal-box">
		<div class="subtotal-text">小計:<%= subTotal %>円(税込)</div>
	</div>

	<footer>
		<table class="footer-table">
			<tr>
				<td width="33%">
					<form action="ShowMenuServlet" method="post">
						<button type="submit" class="btn-footer btn-menu-back">
							<img src="./image/menu.png" alt="メニューアイコン"><br>
							<span>メニュー</span>
						</button>
					</form>
				</td>
				<td width="34%">
					<div class="table-num"><%= tableNum %>卓</div>
				</td>
				<td width="33%">
					<form action="<%= formAction %>" method="post">
						<input type="hidden" name="productId" value="<%= productId %>">
						<input type="hidden" name="productName" value="<%= pName %>">
						<input type="hidden" name="productPrice" value="<%= pPrice %>">
						<input type="hidden" name="productCategory" value="<%= category %>">
						<input type="hidden" name="subTotal" value="<%= subTotal %>">
						<input type="hidden" name="orderId" value="<%= request.getAttribute("orderId") %>">
						<input type="hidden" name="mode" value="add">
						<%
						if (toppingList != null) {
						    for (int j = 0; j < toppingList.size(); j++) {
						%>
						<input type="hidden" name="oldQty_<%= j %>" value="<%= toppingList.get(j).getToppingQuantity() %>">
						<%
						    }
						}
						%>
						<button type="submit" name="Button" value="追加" class="btn-footer btn-add-cart">
							<img src="./image/addCart.png" alt="追加アイコン"><br>
							<span>追加</span>
						</button>
					</form>
				</td>
			</tr>
		</table>
	</footer>
</div>

</body>
</html>