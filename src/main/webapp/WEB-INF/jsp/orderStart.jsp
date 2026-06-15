<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    Integer tableNumber = (Integer) request.getAttribute("tableNumber");
    Integer guestCount = (Integer) request.getAttribute("guestCount");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
<title>注文開始</title>
<link rel="stylesheet" href="./css/common.css">
<link rel="stylesheet" href="./css/orderStart.css">
<script src="./js/windowScaler.js"></script>
</head>
<body>

	<header class="header-area">
		<img src="./image/木目3.jpg" alt="背景" class="bg-img">
		<img src="./image/biglogo.png" alt="ロゴ" class="logo-img">
	</header>

	<div class="guest-container">
		<div class="yellow-area">
			<%= tableNumber %>卓
		</div>

		<div class="message-area">
			<strong>いらっしゃいませ</strong><br>
			<span style="color: #555; font-size: 1rem;">人数を設定してください</span>
		</div>

		<form action="OrderStartServlet" method="post" style="width: 100%; text-align: center;">
			<input type="hidden" name="tableId" value="<%= tableNumber %>">
			<input type="hidden" name="guestCount" value="<%= guestCount %>">
			
			<table class="counter-table">
				<tr>
					<td>
						<button type="submit" name="action" value="minus" class="btn-counter">-</button>
					</td>
					<td class="guest-display"><%= guestCount %></td>
					<td>
						<button type="submit" name="action" value="plus" class="btn-counter">+</button>
					</td>
				</tr>
			</table>

			<button type="submit" name="action" value="start" class="btn-start-trigger">
				注文開始
			</button>
		</form>
	</div>

</body>
</html>