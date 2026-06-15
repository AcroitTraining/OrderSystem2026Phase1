<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>注文履歴</title>
<link rel="stylesheet" href="./css/common.css?v=3">
<link rel="stylesheet" href="./css/orderHistory.css?v=3">
<script src="./js/popupClose.js"></script>
</head>
<body>

	<header class="header-area">
		<img src="./image/木目3.jpg" alt="背景" class="bg-img">
		<img src="./image/biglogo.png" alt="ロゴ" class="logo-img">
	</header>

	<div class="container">
		<c:choose>
			<%-- 注文していない場合表示 --%>
			<c:when test="${empty orderHistoryList}">
				<div class="no-history-box">
					注文履歴がありません
				</div>
			</c:when>
			<%-- 注文している商品がある時表示 --%>
			<c:otherwise>
				<table class="order-table">
					<thead>
						<tr>
							<th>商品名</th>
							<th width="15%">数量</th>
							<th width="30%">金額(税込)</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="item" items="${orderHistoryList}">
							<tr>
								<td>
									<strong>${item.productName}</strong><br>
									<c:forEach var="topping" items="${item.toppings}">
										<span style="color: #666; font-size: 0.85rem;">・${topping.name}✕${topping.quantity}</span><br>
									</c:forEach>
								</td>
								<td align="center">${item.orderQuantity}</td>
								<td align="right">
									<fmt:formatNumber value="${item.subTotal}" pattern="#,###"/>円
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
				
				<div class="total-area">
					<span class="total-qty-text">${totalOrderQuantity}点</span><br>
					<span class="total-price">
						<fmt:formatNumber value="${totalOrderPrice}" pattern="#,###"/>円(税込)
					</span>
				</div>
			</c:otherwise>
		</c:choose>
	</div>

	<footer>
		<table class="footer-table">
			<tr>
				<td>
					<button type="button" class="btn-footer btn-green-style" onclick="location.href='ShowMenuServlet'"><img src="./image/menu.png" alt="メニュー"><span>メニュー</span></button>
				</td>
				<td>
					<div class="table-num">${tableNumber}卓</div>
				</td>
				<td>
					<c:choose>
						<c:when test="${not empty orderHistoryList}">
							<form action="OrderHistoryServlet" method="post" style="margin: 0;">
								<input type="hidden" name="tableNumber" value="${tableNumber}">
								<input type="hidden" name="totalOrderPrice" value="${totalOrderPrice}">
								<button type="submit" name="action" value="checkOut" class="btn-footer btn-orange-style"><img src="./image/history.png" alt="お会計"><span>お会計</span></button>
							</form>
						</c:when>
						<c:otherwise>
							<button type="button" class="btn-footer btn-orange-style" style="opacity: 0.5; cursor: not-allowed;" disabled><img src="./image/history.png" alt="お会計"><span>お会計</span></button>
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
		</table>
	</footer>

	<c:if test="${popupStatus == 1 || popupStatus == 2}">
		<div id="modalOverlay" class="modal-overlay active">
			<div class="modal-content">
				<c:choose>
					<c:when test="${popupStatus == 1}">
						<div class="modal-title">未提供の注文があります。</div>
						<div class="btn-group">
							<button type="button" class="btn-base btn-close" onclick="closeModal()">閉じる</button>
						</div>
					</c:when>
					<c:when test="${popupStatus == 2}">
						<div class="modal-title">お会計に進みます。<br>よろしいですか？</div>
						<form action="OrderHistoryServlet" method="post" class="btn-group">
							<input type="hidden" name="tableNumber" value="${tableNumber}">
							<input type="hidden" name="totalOrderPrice" value="${totalOrderPrice}">
							<button type="button" class="btn-base btn-no" onclick="closeModal()">いいえ</button>
							<button type="submit" name="action" value="yes" class="btn-base btn-yes">はい</button>
						</form>
					</c:when>
				</c:choose>
			</div>
		</div>
	</c:if>
</body>
</html>