<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
    Integer tableNumber = (Integer) request.getAttribute("tableNumber");
    Integer guestCount = (Integer) request.getAttribute("guestCount");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, user-scalable=no">
<!-- 見ている画面の範囲 -->
<title>注文開始</title>
<script src="./js/windowScaler.js"></script>
<!-- ウィンドウサイズの調整用JavaScript -->
</head>
<body bgcolor="#FDF5E6">
	<header class="header-area">
		<img src="./image/木目3.jpg" alt="背景" class="bg-img"> <img
			src="./image/biglogo.png" alt="ロゴ" class="logo-img">
	</header>
	<center>
		<table width="300" border="0" cellpadding="0" cellspacing="0"
			style="background-color: #FDF5E6;">

			<!-- 卓番号と挨拶テキストの表示 -->
			<tr>
				<td align="center">
					<div
						style="background-color: #FFD700; width: 250px; padding: 50px 0; border-radius: 15px;">
						<font size="6"><b><%= tableNumber %>卓</b></font>
					</div>
				</td>
			</tr>
			<tr>
				<td height="30"></td>
			</tr>
			<tr>
				<td align="center"><font size="4">いらっしゃいませ！</font><br>
				<br> <font size="4">人数を設定してください</font></td>
			</tr>
			<tr>
				<td height="20"></td>
			</tr>

			<!-- 人数登録 -->
			<tr>
				<td align="center">
					<form action="OrderStartServlet" method="post">
						<input type="hidden" name="tableId" value="<%= tableNumber %>">
						<input type="hidden" name="guestCount" value="<%= guestCount %>">
						<table border="0" cellpadding="10">
							<tr>

								<!-- マイナスボタン -->
								<td>
									<button type="submit" name="action" value="minus"
										style="background-color: #5cb85c; color: white; border: none; width: 50px; height: 50px; font-size: 25px; font-weight: bold; border-radius: 5px; cursor: pointer;">
										-</button>
								</td>

								<!-- 人数表示 -->
								<td width="60" align="center"><font size="7"><b><%= guestCount %></b></font>
								</td>

								<!-- プラスボタン -->
								<td>
									<button type="submit" name="action" value="plus"
										style="background-color: #5cb85c; color: white; border: none; width: 50px; height: 50px; font-size: 25px; font-weight: bold; border-radius: 5px; cursor: pointer;">
										+</button>
								</td>
							</tr>
						</table>
						<br>
						<br>

						<!-- 注文開始ボタン -->
						<button type="submit" name="action" value="start"
							style="background-color: #008000; color: white; border: none; padding: 10px 30px; font-size: 18px; font-weight: bold; border-radius: 5px; cursor: pointer;">
							注文開始</button>
					</form>
				</td>
			</tr>
			<tr>
				<td height="50"></td>
			</tr>
		</table>
	</center>
</body>
</html>