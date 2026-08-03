<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
<meta charset="UTF-8">
<title>会計完了</title>
<link rel="stylesheet" href="./css/common.css">
</head>
<body>
	<header class="header-area">
		<img src="./image/木目3.jpg" alt="背景" class="bg-img"> 
		<img src="./image/biglogo.png" alt="ロゴ" class="logo-img">
	</header>
	
	<div class="yellow-area">
		<c:choose>
			<%-- 他のユーザーが会計を完了した場合 --%>
			<c:when test="${isOtherUserCheckout}">
				<font size="4">お会計が完了されています</font><br><br>
				<font size="4">ご利用ありがとうございました</font><br>
			</c:when>

			<%-- 自分が会計を行った場合（通常表示） --%>
			<c:otherwise>
				<font size="4">会計が確定されました</font><br><br> 
				<font size="4">ご利用ありがとうございます</font><br><br> 
				<font size="5"><b><c:out value="${checkOutInfo.tableNumber}" default="0" />卓</b></font><br>
				<font size="5">
					<u><b>合計：<fmt:formatNumber value="${checkOutInfo.totalOrderPrice}" pattern="#,##0" />円(税込み)</b></u>
				</font>
			</c:otherwise>
		</c:choose>
	</div>

	<div style="text-align: center;">
		<br><br> 
		<%-- ★ここを celebrated から test に修正しました --%>
		<c:if test="${!isOtherUserCheckout}">
			<font size="4">レジにてお支払いください</font><br><br> 
		</c:if>
		<font size="4">またのご利用をお待ちしております</font><br><br><br>
		
		<!-- トップ画面へ戻るリンク -->
		<a href="index.jsp" style="font-size: 18px; font-weight: bold; color: #333; text-decoration: underline;">トップ画面へ戻る</a>
	</div>
</body>
</html>