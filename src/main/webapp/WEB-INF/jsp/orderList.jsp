<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
String tableNum = (String) session.getAttribute("tableNumber");
if (tableNum == null) {
	tableNum = "-";
}
%>

<!DOCTYPE html>
<html lang="ja">

<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta http-equiv="refresh" content="10">
<title>注文リスト画面</title>
<link rel="stylesheet" href="./css/orderList.css">
<link rel="stylesheet" href="./css/common.css">
</head>
<body>
	<header class="header-area">
		<img src="./image/木目3.jpg" alt="背景" class="bg-img"> <img
			src="./image/biglogo.png" alt="ロゴ" class="logo-img">
	</header>
	<div class="blank"></div>
	<c:choose>
		<%-- リストが空の場合 --%>
		<c:when test="${empty olList}">
			<div class="yellow-area">
				<font size="4">リストはからです。</font>
			</div>

			<footer>
				<table class="footer-table">
					<tr>
						<td width="33%">
							<form action="ShowMenuServlet" method="get">
								<button type="submit" class="btn-footer btn-menu">
									<img src="./image/menu.png" alt="メニューアイコン"><br> <span>メニュー</span>
								</button>
							</form>
						</td>
						<td width="34%">
							<div class="table-num"><%=tableNum%>卓
							</div>
						</td>
						<td width="33%"></td>
					</tr>
				</table>
			</footer>
		</c:when>

		<%-- リストに商品がある場合 --%>
		<c:otherwise>
			<c:forEach var="item" items="${olList}">
				<div class="order-item-box">

					<table width="100%" style="padding: 5px 10px 0 10px;">
						<tr>
							<td align="left"><strong style="font-size: 1.2em;">${item.productName}</strong>
							</td>
							<td align="right"
								style="font-weight: bold; font-size: 1.1em; white-space: nowrap;">
								${item.productPrice}円</td>
						</tr>
					</table>

					<c:if test="${!empty item.toppings}">
						<c:forEach var="t" items="${item.toppings}">
							<table width="100%" style="padding: 0 10px; color: #555;">
								<tr>
									<td align="left">・${t.name}✕${t.quantity}</td>
									<td align="right"></td>
								</tr>
							</table>
						</c:forEach>
					</c:if>

					<table width="100%" style="padding: 10px;">
						<tr>
							<td align="left" valign="middle"><c:if
									test="${item.categoryName == 'お好み焼き' or item.categoryName == 'もんじゃ焼き'}">
									<form action="ItemDetailsChangeServlet" method="get"
										style="margin: 0;">
										<input type="hidden" name="oid" value="${item.orderId}">
										<button type="submit" name="Button" value="変更"
											class="btn-order-change">変更</button>
									</form>
								</c:if></td>

							<td align="right" valign="middle">
								<div class="quantity-control-group">
									<%-- 削除またはマイナスボタン --%>
									<c:choose>
										<c:when test="${item.orderQuantity == 1}">
											<form action="OrderRemoveServlet" method="post"
												style="margin: 0;" class="delete-form">
												<input type="hidden" name="oid" value="${item.orderId}">
												<button type="submit" name="Button" value="削除"
													class="btn-product-trash">🗑</button>
											</form>
										</c:when>
										<c:otherwise>
											<form action="OrderListServlet" method="post"
												style="margin: 0;">
												<input type="hidden" name="oid" value="${item.orderId}">
												<button type="submit" name="Button" value="-"
													class="btn-product-minus">-</button>
											</form>
										</c:otherwise>
									</c:choose>

									<%-- 現在の数量表示 --%>
									<span class="order-quantity-text">${item.orderQuantity}</span>

									<%-- プラスボタンまたは上限 --%>
									<c:choose>
										<c:when
											test="${item.orderQuantity >= 4 or item.productStock == 0 or (item.toppingStock <= item.toppingQuantity and item.toppingQuantity > 0)}">
											<span class="sold-out-text">上限</span>
										</c:when>
										<c:otherwise>
											<form action="OrderListServlet" method="post"
												style="margin: 0;">
												<input type="hidden" name="oid" value="${item.orderId}">
												<button type="submit" name="Button" value="+"
													class="btn-product-plus">+</button>
											</form>
										</c:otherwise>
									</c:choose>
								</div>
							</td>
						</tr>
					</table>

					<table width="100%" style="padding: 0 10px 10px 10px;">
						<tr>
							<td align="left"></td>
							<td align="right" style="font-weight: bold; font-size: 1.1em;">
								小計：<fmt:formatNumber value="${item.subTotal}" pattern="#,###" />円


							
						</tr>
					</table>

				</div>
			</c:forEach>
			<div class="blank2"></div>
			<div class="total-area">
				<div class="total-text">
					合計：
					<fmt:formatNumber value="${aop.allOrderPrice}" pattern="#,###" />
					円（税込）
				</div>
			</div>

			<footer>
				<table class="footer-table">
					<tr>
						<td width="33%">
							<form action="ShowMenuServlet" method="get">
								<button type="submit" class="btn-footer btn-menu">
									<img src="./image/menu.png" alt="メニューアイコン"><br> <span>メニュー</span>
								</button>
							</form>
						</td>
						<td width="34%">
							<div class="table-num"><%=tableNum%>卓
							</div>
						</td>
						<td width="33%">
							<form action="OrderCompleteServlet" method="get">
								<button type="submit" class="btn-footer btn-order" id="orderBtn">
									<img src="./image/Vector.png" alt="カートアイコン"><br> <span>注文する</span>
								</button>
							</form>
						</td>
					</tr>
				</table>
			</footer>
		</c:otherwise>
	</c:choose>
	<div id="deleteModal" class="modal-overlay">
		<div class="modal-content">
			<div class="modal-title">
				この商品を削除します<br>よろしいですか？
			</div>
			<div class="btn-group">
				<button type="button" id="cancelDelete" class="btn-base btn-no">いいえ</button>
				<button type="button" id="confirmDelete" class="btn-base btn-yes">はい</button>
			</div>
		</div>
	</div>

	<div id="orderModal" class="modal-overlay">
		<div class="modal-content">
			<div class="modal-title">
				注文を確定します<br>よろしいですか？
			</div>
			<div class="btn-group">
				<button type="button" id="cancelOrder" class="btn-base btn-no">いいえ</button>
				<button type="button" id="confirmOrder" class="btn-base btn-yes">はい</button>
			</div>
		</div>
	</div>

	<script src="./js/orderList.js"></script>
</body>
</html>