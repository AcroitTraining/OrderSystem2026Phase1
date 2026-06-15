<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="model.TableInfo"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>注文完了画面</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet" href="./css/common.css">
</head>
<body>
	<header class="header-area">
		<img src="./image/木目3.jpg" alt="背景" class="bg-img"> <img
			src="./image/biglogo.png" alt="ロゴ" class="logo-img">
	</header>

	<div class="container">
		<div class="content">
			<div class="yellow-area">


				<font size="4">ご注文いただきありがとうございます。</font> <br>
				<font size="4"> お料理を準備いたしますので </font><br>
				<font size="4">しばらくお待ちください</font>
			</div>
		</div>
	</div>

	<footer>
		<table class="footer-table">
			<tr>
				<td width="33%">
					<button type="button" class="btn-footer btn-green-style"
						onclick="location.href='ShowMenuServlet'">
						<img src="./image/menu.png" alt="メニュー"><br> <span>メニュー</span>
					</button>
				</td>
				<td width="34%">
					<div class="table-num">${tableNumber}卓</div>
				</td>
				<td width="34%"></td>
			</tr>
		</table>
	</footer>

</body>

</html>