<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%-- エラーページであることを宣言 --%>
<%@ page isErrorPage="true"%>
<%
    // ステータスコードの取得
    int statusCode = response.getStatus();
%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet" href="./css/common.css">
<title>エラーが発生しました</title>
</head>
<body>
	<header class="header-area">
		<img src="./image/木目3.jpg" alt="背景" class="bg-img"> <img
			src="./image/biglogo.png" alt="ロゴ" class="logo-img">
	</header>


	<div class="yellow-area">
		<h1>システムエラーが発生しました</h1>
		<div class="message">
			<% if (session.getAttribute("errorMessage") != null) { %>
			<div class="detail-message">
				【エラー詳細】<br> ${sessionScope.errorMessage}
			</div>
			<%-- 次回表示時に残らないようセッションから削除 --%>
			<% session.removeAttribute("errorMessage"); %>
			<% } %>

			<% if (session.getAttribute("errorMessage") != null) { %>
			<div class="detail-message">
				【エラー詳細】<br> ${sessionScope.errorMessage}
			</div>
			<%-- 次回表示時に残らないようセッションから削除 --%>
			<% session.removeAttribute("errorMessage"); %>
			<% } %>

			<% if (statusCode == 404) { %>
			<p>お探しのページは見つかりませんでした。</p>
			<p>URLが正しいかご確認いただくか、トップページへお戻りください。</p>
			<% } else if (statusCode == 500) { %>
			<p>サーバー内部でエラーが発生しました</p>
			<p>ご迷惑をおかけしますが、しばらく時間を置いてから再度お試しください。</p>
			<% } else {%>

			<p>お手数ですが店員をお呼びください。</p>
			<% } %>
		</div>
	</div>
	<a href="<%= request.getContextPath() %>/index.jsp" class="home-link">トップページへ戻る</a>

	<footer>
		<table class="footer-table">
			<tr>
				<td width="33%"></td>
				<td width="34%"></td>
				<td width="33%"></td>
			</tr>
		</table>
	</footer>

</body>
</html>
