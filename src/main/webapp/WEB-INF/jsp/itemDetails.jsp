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
<meta http-equiv="refresh" content="10">
<title>商品詳細</title>
<link rel="stylesheet" href="./css/common.css?v=3">
<link rel="stylesheet" href="./css/itemDetails.css?v=3">
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
		<%-- ★①商品価格に3桁カンマを追加 --%>
		<div class="product-main-price"><%= String.format("%,d", pPrice) %>円(税込)</div>
	</div>

	<% if (showTopping && toppingList != null && !toppingList.isEmpty()) { 
		// 現在のトッピング全体の合計選択数を算出する
		int totalToppingQty = 0;
		for (ItemDetailsInfo t : toppingList) {
			totalToppingQty += t.getToppingQuantity();
		}
	%>
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
				<%-- ★②トッピング価格に3桁カンマを追加 --%>
				<span class="topping-price"><%= String.format("%,d", t.getToppingPrice()) %>円</span>
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

					<button type="submit" name="Button" value="-<%= i %>" class="btn-qty" <%= (t.getToppingQuantity() <= 0) ? "disabled" : "" %>>－</button>
					<span class="qty-text"><%= t.getToppingQuantity() %></span>
					
					<%-- 全体が4個に達した、または個別在庫の上限に達したら disabled --%>
					<button type="submit" name="Button" value="+<%= i %>" class="btn-qty" <%= (totalToppingQty >= 4 || t.getToppingQuantity() >= t.getToppingStock()) ? "disabled" : "" %>>＋</button>
				</form>
			<% } else { %>
				<%-- 売り切れ時はボタンを非表示にし、売切テキストのみ表示 --%>
				<div class="quantity-form">
					<span class="sold-out-text">売切</span>
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
	<%-- ★③最下部の小計に3桁カンマを追加 --%>
	<div class="subtotal-text">小計:<%= String.format("%,d", subTotal) %>円(税込)</div>
</div>

<footer>
	<table class="footer-table">
		<tr>
			<td>
				<form action="ShowMenuServlet" method="post">
					<button type="submit" class="btn-footer btn-green-style"><img src="./image/menu.png" alt="メニューアイコン"><span>メニュー</span></button>
				</form>
			</td>
			<td>
				<div class="table-num"><%= tableNum %>卓</div>
			</td>
			<td>
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
					<button type="submit" name="Button" value="追加" class="btn-footer btn-orange-style"><img src="./image/addCart.png" alt="追加アイコン"><span>追加</span></button>
				</form>
			</td>
		</tr>
	</table>
</footer>

</body>
</html>