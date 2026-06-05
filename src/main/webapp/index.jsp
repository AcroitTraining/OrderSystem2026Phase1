<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>開店処理画面</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet" href="./css/orderList.css">
<script src="./js/windowScaler.js"></script>
</head>
<body>
	<div class="container">
		<div class="content">

			<form action="OrderStartServlet" method="post">
				卓番：<br> <input type="number" name="tableId" min="1" max="4"
					value="1"><br> <input type="submit" value="登録">
		</div>
	</div>
	</form>
</body>
</html>