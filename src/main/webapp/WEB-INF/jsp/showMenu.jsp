<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="model.ProductInfo"%>

<%
List<ProductInfo> productList = (List<ProductInfo>)session.getAttribute("productList");
String currentCategory = (String)request.getAttribute("currentCategory");
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
<link rel="stylesheet" href="./css/showMenu.css">
</head>
<body>
<div class="category-area">
	<div class="scroll-text"></div>
	<nav class="category-wrap">
		<form action="ShowMenuServlet" method="post">
			<table class="category-table">
				<tr>
					<td><input type="submit" name="category" value="お好み焼き"></td>
					<td><input type="submit" name="category" value="もんじゃ焼き"></td>
					<td><input type="submit" name="category" value="鉄板焼き"></td>
					<td><input type="submit" name="category" value="サイドメニュー"></td>
					<td><input type="submit" name="category" value="ソフトドリンク"></td>
					<td><input type="submit" name="category" value="お酒"></td>
					<td><input type="submit" name="category" value="ボトル"></td>
				</tr>
			</table>
		</form>
	</nav>
</div>
<div class="product-area">
	<table class="product-table" cellpadding="10">

	<%
	if(productList != null){
		for(ProductInfo p : productList){

			if(p.getCategoryName().trim().equals(currentCategory)
				&& p.getProductDisplayFlag() == 1){
	%>
	<tr>
		<td>
			<div class="product-name">
				<%= p.getProductName() %>
			</div>
			<div class="product-price">
				<%= p.getProductPrice() %>円
			</div>
		</td>
		<td align="right">
		<%
		if(p.getProductStock() > 0){
		%>
			<form action="ItemDetailsServlet" method="get">
				<input type="hidden" name="productId" value="<%= p.getProductId() %>">
				<input type="hidden" name="productName" value="<%= p.getProductName() %>">
				<input type="hidden" name="productPrice" value="<%= p.getProductPrice() %>">
				<input type="hidden" name="productCategory" value="<%= p.getCategoryName() %>">
				<input class="btn-add" type="submit" value="＋">
			</form>
		<%
		}else{
		%>
			<span class="sold-out">売切</span>
		<%
		}
		%>
		</td>
	</tr>
	<tr>
		<td colspan="2" class="product-line"></td>
	</tr>
	<%
			}
		}
	}
	%>
	</table>
</div>
<footer>
	<table class="footer-table" border="1">
		<tr>
			<td width="33%">
				<form action="OrderHistoryServlet" method="get">
					<input type="hidden" name="tableId" value="<%= tableNum %>">
					<input type="submit"
					       value="￥ 履歴・お会計"
					       class="btn-order-history">
				</form>
			</td>
			<td width="34%">
				<div class="table-num">
					<%= tableNum %>卓
				</div>
			</td>
			<td width="33%">
				<form action="OrderListServlet" method="get">
				<%
				if(items > 0){
				%>
					<input type="submit"
					       value="注文リスト🛒<%= items %>"
					       class="btn-order-list">
				<%
				}else{
				%>
					<input type="submit"
					       value="注文リスト🛒"
					       class="btn-order-list">
				<%
				}
				%>
				</form>
			</td>
		</tr>
	</table>
</footer>
</body>
</html>