<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="model.TableInfo" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>注文完了画面</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="stylesheet" href="./css/style.css">
  <script src="./js/popupClose.js"></script>
  <script src="./js/windowScaler.js"></script>
</head>
<body>

		<h1 align ="center">ご注文いただき<br>ありがとうございます。</h1>
	<h2 align ="center">
		お料理を準備いたしますので<br>しばらくお待ちください
	</h2>
</body>
<div class ="footer">
	<form action="ShowMenuServlet" method="get">
			<button type="submit" name="Button" value="メニュー" class ="btn-menu">メニュー</button>
		<div class="table-num">${tableNumber}卓</div>
	</form>
</footer>
</html>