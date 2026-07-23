<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="model.ProductInfo"%>

<%
List<ProductInfo> productList = (List<ProductInfo>)session.getAttribute("productList");
List<ProductInfo> categoryList = (List<ProductInfo>)request.getAttribute("categoryList");
Integer currentCategoryIdObj = (Integer)request.getAttribute("currentCategoryId");
int currentCategoryId = (currentCategoryIdObj != null) ? currentCategoryIdObj : -1;

Object tableObj = session.getAttribute("tableNumber");
String tableNum = (tableObj != null) ? tableObj.toString() : "-";
Integer items = (Integer)session.getAttribute("items");

if(items == null){
	items = 0;
}
%>

<!DOCTYPE html>
<html>
<head>

<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>メニュー表示</title>
<link rel="stylesheet" href="./css/common.css">
<link rel="stylesheet" href="./css/showMenu.css">
</head>
<body>

<header class="header-area">
  <img src="./image/木目3.jpg" alt="背景" class="bg-img">
  <img src="./image/biglogo.png" alt="ロゴ" class="logo-img">
</header>
<script src="./js/showMenu.js"></script>

<div class="category-area">
	<div class="scroll-text"></div>
	<nav class="category-wrap" id="categoryWrap">
		<table class="category-table">
			<tr>
			<% 
			if (categoryList != null) {
				for (ProductInfo cat : categoryList) { 
			%>
				<td>
					<form action="ShowMenuServlet" method="post" style="margin:0;">
						<input type="hidden" name="categoryId" value="<%= cat.getCategoryId() %>">
						<input type="submit" 
						       value="<%= cat.getCategoryName() %>" 
						       class="<%= cat.getCategoryId() == currentCategoryId ? "active" : "" %>">
					</form>
				</td>
			<% 
				}
			} 
			%>
			</tr>
		</table>
	</nav>
</div>

<div class="product-area">
	<table class="product-table">
	<%
	int productCount = 0; 
	if(productList != null){
		for(ProductInfo p : productList){
			// ★商品の category_id と選択中の category_id が一致するものだけ表示
			if(p.getCategoryId() == currentCategoryId && p.getProductDisplayFlag() == 1){
				productCount++; 
	%>
	<tr class="product-item-row">
		<td align="left" valign="middle" class="product-info-cell">
			<div class="product-name"><%= p.getProductName() %></div>
			<div class="product-price"><%= p.getProductPrice() %>円</div>
		</td>
		<td align="right" valign="middle" class="product-action-cell">
		<% if(p.getProductStock() > 0){ %>
			<form action="ItemDetailsServlet" method="get" style="margin:0;">
				<input type="hidden" name="productId" value="<%= p.getProductId() %>">
				<input type="hidden" name="productName" value="<%= p.getProductName() %>">
				<input type="hidden" name="productPrice" value="<%= p.getProductPrice() %>">
				<input type="hidden" name="productCategory" value="<%= p.getCategoryName() %>">
				<input type="image" src="./image/plusButton.png" alt="追加" class="btn-img-add">
			</form>
		<% }else{ %>
			<img src="./image/soldout.png" alt="売切" class="img-sold-out">
		<% } %>
		</td>
	</tr>
	<%
			}
		}
	}
	%>
	</table>

	<% if(productCount == 0) { %>
		<div class="no-data-message">商品はありません。</div>
	<% } %>
</div>

<footer>
	<table class="footer-table">
		<tr>
			<td width="33%">
				<form action="OrderHistoryServlet" method="get">
					<input type="hidden" name="tableId" value="<%= tableNum %>">
					<button type="submit" class="btn-footer btn-green-style">
						<img src="./image/menuhistory.png" alt="履歴アイコン">
						<span>履歴・お会計</span>
					</button>
				</form>
			</td>
			<td width="34%">
				<div class="table-num"><%= tableNum %>卓</div>
			</td>
			<td width="33%">
				<form action="OrderListServlet" method="get">
					<button type="submit" class="btn-footer btn-orange-style">
						<div class="cart-container">
							<img src="./image/cart.png" alt="カートアイコン">
							<% if(items > 0){ %>
								<span class="badge"><%= items %></span>
							<% } %>
						</div>
						<span>注文リスト</span>
					</button>
				</form>
			</td>
		</tr>
	</table>
</footer>

</body>
</html>