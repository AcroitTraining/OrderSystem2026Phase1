<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>注文履歴</title>
  <link rel="stylesheet" href="./css/style.css">
  <script src="./js/popupClose.js"></script>
  <script src="./js/windowScaler.js"></script>
</head>
<body>
  <div class="container">
    <div class="content">
      <c:choose>
        <%-- 注文していない場合表示 --%>
        <c:when test="${empty orderHistoryList}">
          <div style="padding: 10px 10px; text-align: center;">
            <div style="background: #FFD700; padding: 110px; border-radius: 20px; font-size: 0.8rem; font-weight: bold;">
              注文履歴がありません
            </div>
          </div>
        </c:when>
        <%-- 注文している商品がある時表示 --%>
        <c:otherwise>
          <table class="order-table">
            <thead>
              <tr>
                <th>商品名</th>
                <th>数量</th>
                <th>金額(税込)</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="item" items="${orderHistoryList}">
                <tr>
                  <td>
                    <strong>${item.productName}</strong><br>
                    <c:forEach var="topping" items="${item.toppings}">
                      <span style="color: #666; font-size: 0.9rem;">・${topping.name}✕${topping.quantity}</span><br><!-- 各トッピング名と各トッピング個数表示 -->
                    </c:forEach>
                  </td>
                  <td align="center">${item.orderQuantity}</td><%-- 各商品個数表示 --%>
                  <td align="right">${item.subTotal}円</td><%-- 各商品の小計 --%>
                </tr>
              </c:forEach>
            </tbody>
          </table>
          <div class="total-area">
            <u>${totalOrderQuantity}点</u><br><%-- 注文した商品の合計数表示 --%>
            <span class="total-price">${totalOrderPrice}円(税込)</span><%-- 注文した商品の合計金額表示 --%>
          </div>
        </c:otherwise>
      </c:choose>
    </div>

    <!-- フッター -->
    <div class="footer">
      <!-- メニューボタン -->
      <div class="footer-btn btn-menu" onclick="location.href='ShowMenuServlet'">
        <span style="font-size: 2rem;">↩</span>
        <strong>メニュー</strong>
      </div>

      <!-- 卓番号 -->
      <div class="table-num">${tableNumber}卓</div>

      <!-- お会計ボタン -->
      <div style="flex: 1; border-left: 1px solid #ccc;">
        <c:if test="${not empty orderHistoryList}">
          <form action="OrderHistoryServlet" method="post" style="height:100%;">
            <input type="hidden" name="tableNumber" value="${tableNumber}">
            <input type="hidden" name="totalOrderPrice" value="${totalOrderPrice}">
            <button type="submit" name="action" value="checkOut" class="btn-checkout">
              <div style="border: 2px solid black; border-radius: 50%; width: 30px; height: 30px; margin: 0 auto 5px; display: flex; align-items: center; justify-content: center;">￥</div>
              <strong>お会計</strong>
            </button>
          </form>
        </c:if>
      </div>
    </div>
  </div>

  <!-- --- ポップアップ --- -->
  <c:if test="${popupStatus == 1 || popupStatus == 2}">
    <div id="modalOverlay" class="modal-overlay active">
      <div class="modal-content">
        <c:choose>
          <%-- 未提供あり --%>
          <c:when test="${popupStatus == 1}">
            <div class="modal-title">未提供の注文があります。</div>
            <div class="btn-group">
              <button type="button" class="btn-base btn-close" onclick="closeModal()">閉じる</button>
            </div>
          </c:when>

          <%-- お会計確認(提供済) --%>
          <c:when test="${popupStatus == 2}">
            <div class="modal-title">お会計に進みます。<br>よろしいですか？</div>
            <form action="OrderHistoryServlet" method="post" class="btn-group">
              <input type="hidden" name="tableNumber" value="${tableNumber}">
              <input type="hidden" name="totalOrderPrice" value="${totalOrderPrice}">
              <button type="button" class="btn-base btn-no" onclick="closeModal()">いいえ</button>
              <button type="submit" name="action" value="yes" class="btn-base btn-yes">はい</button>
            </form>
          </c:when>
        </c:choose>
      </div>
    </div>
  </c:if>
</body>
</html>