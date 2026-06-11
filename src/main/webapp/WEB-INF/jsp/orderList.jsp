<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
					<h1>リストは空です。</h1>
					<!-- 🆕 属性の記述ミス(div = footer1)を修正 -->
					<div class="footer1">
						<div class="center"><%=tableNum%>卓</div>
						<div class="left">
							<form action="ShowMenuServlet" method="get">
								<button type="submit" name="Button" value="メニュー">メニュー</button>
							</form>
						</div>
					</div>
				</c:when>
				<c:otherwise>

					<c:forEach var="item" items="${olList}">
						<div class="order_list">

							<input type="hidden" name="oid" value="${item.orderId}">
							<input type="hidden" name="subTotal" value="${item.subTotal}">
							
							<!-- 基準位置(relative)の直下にあるため、これでabsoluteが効きます -->
							<div class="p_area">${item.productName}</div>
							<div class="p_p_area">${item.productPrice}円</div>

							<c:if test="${!empty item.toppings}">
								<c:forEach var="t" items="${item.toppings}">
									<div class="t_area">・${t.name}✕${t.quantity}</div>
								</c:forEach>
							</c:if>

							<c:if test="${item.categoryName == 'お好み焼き' or item.categoryName == 'もんじゃ焼き'}">
								<div class="cButton">
									<form action="ItemDetailsChangeServlet" method="get">
										<input type="hidden" name="oid" value="${item.orderId}">
										<button type="submit" name="Button" value="変更">変更</button>
									</form>
								</div>
							</c:if>

							<!-- 🆕 数量とトッピングの操作エリアをスッキリ整理 -->
							<div class="quantity_control_area">
								
								<!-- 【減算・削除ボタンエリア】 -->
								<c:choose>
									<%-- 数量が1の場合は削除ボタン --%>
									<c:when test="${item.orderQuantity == 1}">
										<div class="mButton">
											<form action="OrderRemoveServlet" method="post">
												<input type="hidden" name="oid" value="${item.orderId}">
												<button type="submit" name="Button" value="削除">削除</button>
											</form>
										</div>
									</c:when>
									<%-- 数量が2以上ならマイナスボタン --%>
									<c:otherwise>
										<div class="mButton">
											<form action="OrderListServlet" method="post">
												<input type="hidden" name="oid" value="${item.orderId}">
												<button type="submit" name="Button" value="-">-</button>
											</form>
										</div>
									</c:otherwise>
								</c:choose>

								<%-- 現在の数量表示 --%>
								<span class="q_num">${item.orderQuantity}</span>

								<!-- 【加算・上限表示エリア】 -->
								<c:choose>
									<%-- トッピングが上限の場合 --%>
									<c:when test="${item.toppingQuantity != null and item.toppingStock <= item.toppingQuantity}">
										<div class="pButton error-text">トッピング上限</div>
									</c:when>
									<%-- 商品自体が上限、または在庫切れの場合 --%>
									<c:when test="${item.orderQuantity >= 4 or item.productStock == 0}">
										<div class="pButton limit-text">上限</div>
									</c:when>
									<%-- 通常時はプラスボタン --%>
									<c:otherwise>
										<div class="pButton">
											<form action="OrderListServlet" method="post">
												<input type="hidden" name="oid" value="${item.orderId}">
												<button type="submit" name="Button" value="+">+</button>
											</form>
										</div>
									</c:otherwise>
								</c:choose>
							</div>

							<div class="subtotal_area">
								小計：${item.subTotal}円
							</div>
							
						</div><!-- /.order_list -->
					</c:forEach>

					<div class="total-area">
						<!-- 🆕 CSSのクラス名(.total-price)と大文字小文字を合わせました -->
						<span class="total-price">合計：${aop.allOrderPrice}円（税込み）</span>
					</div>

					<div class="footer">
						<div class="footer-btn btn-menu" onclick="location.href='ShowMenuServlet'">
							<span style="font-size: 2rem;">↩</span> <strong>メニュー</strong>
						</div>
						<div class="table-num"><%=tableNum%>卓</div>
						<div class="footer-btn btn-order" onclick="location.href='OrderCompleteServlet'">
							<span style="font-size: 2rem;">↩</span> <strong>注文する</strong>
						</div>
					</div>

				</c:otherwise>
			</c:choose>
		</div><!-- /.content -->
	</div><!-- /.container -->
</body>
</html>