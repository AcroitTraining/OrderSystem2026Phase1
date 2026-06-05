<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
String tableNum = (String) session.getAttribute("tableNumber");
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>注文リスト画面</title>
<link rel="stylesheet" href="./css/orderList.css">
<script src="./js/windowScaler.js"></script>
</head>
<body>
	<div class="container">
		<div class="content">
			<c:choose>
				<c:when test="${empty olList}">
					<h1>リストはからです。</h1>
					<div = footer1>
						<div="center"><%=tableNum%>卓
						</div>
						<div="left">
							<form action="ShowMenuServlet" method="get">
								<button type="submit" name="Button" value="メニュー">メニュー</button>
							</form>
						</div>
				</c:when>
				<c:otherwise>

					<c:forEach var="item" items="${olList}">

						<input type="hidden" name="oid" value="${item.orderId}">
						<input type="hidden" name="subTotal" value="${item.subTotal}">
						<table class="ol-table">
							<tr>
								<td>${item.productName}
								</th>
								<td>${item.productPrice}円
								</th>
							</tr>
							<c:if test="${!empty item.toppings}">
								<c:forEach var="t" items="${item.toppings }">
									<tr>
										<td>・${t.name}✕${t.quantity}</td>
									</tr>
								</c:forEach>
							</c:if>
							<tr>
								<c:if
									test="${item.categoryName == 'お好み焼き' or item.categoryName == 'もんじゃ焼き'}">
									<form action="ItemDetailsChangeServlet" method="get">
										<input type="hidden" name="oid" value="${item.orderId}">
										<td><button type="submit" name="Button" value="変更">変更</button>
										</td>
									</form>
								</c:if>
								<c:if test="${item.orderQuantity == 1}">

									<td style="text-align: right;">
										<form action="OrderRemoveServlet" method="post">
											<input type="hidden" name="oid" value="${item.orderId}">
											<button type="submit" name="Button" value="削除">削除</button>${item.orderQuantity}
										</form>
									</td>
									<td><form action="OrderListServlet" method="post">
											<input type="hidden" name="oid" value="${item.orderId}">
											<button type="submit" name="Button" value="+">+</button>
										</form></td>

								</c:if>
								<c:if
									test="${item.orderQuantity < 4 and item.orderQuantity > 1}">

									<td style="text-align: right;">
										<form action="OrderListServlet" method="post">
											<input type="hidden" name="oid" value="${item.orderId}">
											<button type="submit" name="Button" value="-">-</button>
											${item.orderQuantity}
										</form>
									</td>
									<td>
										<form action="OrderListServlet" method="post">
											<input type="hidden" name="oid" value="${item.orderId}">
											<button type="submit" name="Button" value="+">+</button>
										</form>
									</td>
									</form>
								</c:if>
								<c:if test="${item.orderQuantity == 4 or item.productStock == 0}">

									<td style="text-align: right;">
										<form action="OrderListServlet" method="post">
											<input type="hidden" name="oid" value="${item.orderId}">
											<button type="submit" name="Button" value="-">-</button>
											${item.orderQuantity}
										</form>
									</td>
									<td>上限</td>

								</c:if>
								<c:if test="${item.toppingStock <= item.toppingQuantity and item.toppingQuantity !=null}">

									<td style="text-align: right;">
										<form action="OrderListServlet" method="post">
											<input type="hidden" name="oid" value="${item.orderId}">
											<button type="submit" name="Button" value="-">-</button>
											${item.orderQuantity}
										</form>
									</td>
									<td>トッピング上限</td>

								</c:if>							
							</tr>
							<tr>
								<td>小計：${item.subTotal}円</td>
							</tr>



						</table>
						<br>
					</c:forEach>
					<div class="total-area">
						<span class="total-Price">合計：${aop.allOrderPrice}円（税込み）</span>


					</div>
		</div>

		<div class="footer">
			<div class="footer-btn btn-menu"
				onclick="location.href='ShowMenuServlet'">
				<span style="font-size: 2rem;">↩</span> <strong>メニュー</strong>
			</div>
			<div class="table-num"><%=tableNum%>卓
			</div>
			<div class="footer-btn btn-order"
				onclick="location.href='OrderCompleteServlet'">
				<span style="font-size: 2rem;">↩</span> <strong>注文する</strong>
			</div>
		</div>
		</c:otherwise>
		</c:choose>
</body>


</html>