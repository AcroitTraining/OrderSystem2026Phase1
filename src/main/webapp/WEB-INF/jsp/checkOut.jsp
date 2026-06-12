<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="model.CheckOutInfo"%>
<%
  CheckOutInfo info = (CheckOutInfo)request.getAttribute("checkOutInfo");
  String tableNum = (info != null) ? info.getTableNumber() : "0";
  int totalPrice = (info != null) ? info.getTotalOrderPrice() : 0;
%>
<!DOCTYPE html>
<html>
<head>
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, user-scalable=no">
<!-- 見ている画面の範囲 -->
<meta charset="UTF-8">
<meta http-equiv="Cache-Control"
	content="no-store, no-cache, must-revalidate, max-age=0">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Expires" content="0">
<title>会計完了</title>
<script src="./js/windowScaler.js"></script>
<!-- ウィンドウサイズの調整用javaScript -->
</head>
<body bgcolor="#FDF5E6">
	<header class="header-area">
		<img src="./image/木目3.jpg" alt="背景" class="bg-img"> <img
			src="./image/biglogo.png" alt="ロゴ" class="logo-img">
	</header>
	<center>
		<table width="400" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<div
					style="background-color: #FFD700; width: 450px; padding: 20px 0; border-radius: 15px;">
					<font size="4">会計が確定されました</font><br> <br> <font size="4">ご利用ありがとうございます</font><br>
					<br> <font size="5"><b><%= tableNum %>卓</b></font><br>
					<!-- 卓番号表示 -->
					<font size="5"><u><b>合計：<%= totalPrice %>円(税込)
						</b></u></font>
					<!-- 合計金額表示 -->
				</div>
			</tr>
			<tr>
				<td align="center"><br> <br> <font size="4">レジにてお支払いください</font><br>
					<br> <font size="4">またのご利用をお待ちしております</font></td>
			</tr>
		</table>
	</center>
</body>
</html>