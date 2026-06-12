<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- エラーページであることを宣言 --%>
<%@ page isErrorPage="true" %>
<%
    // ステータスコードの取得 (デフォルトは500)
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
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        }
        h1 { color: #dc3545; }
        .error-code { font-size: 1.2em; font-weight: bold; color: #6c757d; }
        .message { margin-top: 20px; font-size: 1.1em; }
        .home-link { margin-top: 30px; display: inline-block; color: #007bff; text-decoration: none; }
        .home-link:hover { text-decoration: underline; }
    </style>
</head>
<body>
<div class="error-container">
    <h1>システムエラーが発生しました</h1>
    <div class="error-code">ステータスコード: <%= statusCode %></div>
    
    <div class="message">
        <% if (statusCode == 404) { %>
            <p>お探しのページは見つかりませんでした。</p>
            <p>URLが正しいかご確認いただくか、トップページへお戻りください。</p>
        <% } else if (statusCode == 500) { %>
            <p>サーバー内部でエラーが発生しました</p>
            <p>ご迷惑をおかけしますが、しばらく時間を置いてから再度お試しください。</p>
        <% } else { %>
            <p>予期せぬエラーが発生しました。</p>
        <% } %>
    </div>

    <a href="<%= request.getContextPath() %>/index.jsp" class="home-link">トップページへ戻る</a>
</div>

</body>
</html>
