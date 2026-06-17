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
<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
<meta http-equiv="refresh" content="10">
<meta charset="UTF-8">
<title>会計完了</title>
<link rel="stylesheet" href="./css/common.css">
</head>
<body>
	<header class="header-area">
		<img src="./image/木目3.jpg" alt="背景" class="bg-img"> <img
			src="./image/biglogo.png" alt="ロゴ" class="logo-img">
	</header>
	<div class = "yellow-area">
		<font size="4">会計が確定されました</font><br>
		<br> <font size="4">ご利用ありがとうございます</font><br>
		<br> <font size="5"><b><%= tableNum %>卓</b></font><br>
		<font size="5"><u><b>合計：<%= String.format("%,d", totalPrice) %>円(税込)
			</b></u></font>
	</div>
	</tr>
	<tr>
		<td align="center"><br>
		<br> <font size="4">レジにてお支払いください</font><br>
		<br> <font size="4">またのご利用をお待ちしております</font>
</body>
</html>