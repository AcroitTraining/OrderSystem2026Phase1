<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet" href="./css/style.css">
<script src="./js/popupClose.js"></script>
<script src="./js/windowScaler.js"></script>
<title>注文削除画面</title>
</head>
<body>
	<form action="OrderListServlet" method="get">
		<h1 align="center">削除しました</h1>
		<div class="footer">
			<button type="submit" name="Button" value="戻る" class="btn-menu">戻る</button>
	<div class="table-num">${tableNumber}卓</div>
	</form>
	</div>
</body>
</html>