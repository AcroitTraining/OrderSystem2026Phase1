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
<title>エラーが発生しました</title>
<style>
body {
	font-family: sans-serif;
	background-color: #f8f9fa;
	color: #333;
	text-align: center;
	padding: 50px;
}

.error-container {
	background: #fff;
	max-width: 600px;
	margin: 0 auto;
	padding: 30px;
	border-radius: 8px;
	box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

h1 {
	color: #dc3545;
}

.error-code {
	font-size: 1.2em;
	font-weight: bold;
	color: #6c757d;
}

.message {
	margin-top: 20px;
	font-size: 1.1em;
}

/* 追加：エラー詳細メッセージ用のスタイル */
.detail-message {
	margin-top: 15px;
	padding: 10px;
	background-color: #fff3cd;
	border: 1px solid #ffeeba;
	color: #856404;
	border-radius: 4px;
	text-align: left;
	font-family: monospace;
	font-size: 0.9em;
	word-break: break-all;
}

.home-link {
	margin-top: 30px;
	display: inline-block;
	color: #007bff;
	text-decoration: none;
}

.home-link:hover {
	text-decoration: underline;
}
</style>
</head>
<body>
	<div class="error-container">
		<h1>システムエラーが発生しました</h1>
		

		<div class="message">
		
		<% if (session.getAttribute("errorMessage") != null) { %>
				<div class="detail-message">
					【エラー詳細】<br>
					${sessionScope.errorMessage}
				</div>
				<%-- 次回表示時に残らないようセッションから削除 --%>
				<% session.removeAttribute("errorMessage"); %>
			<% } %>
			
			<% if (session.getAttribute("errorMessage") != null) { %>
				<div class="detail-message">
					【エラー詳細】<br>
					${sessionScope.errorMessage}
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

		<a href="<%= request.getContextPath() %>/index.jsp" class="home-link">トップページへ戻る</a>
	</div>

</body>
</html>
