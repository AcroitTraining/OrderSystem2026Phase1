<%@ page language="java"
contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>

<%@ page import="java.util.List" %>
<%@ page import="model.ItemDetailsInfo" %>
<%@ page import="model.OrderListInfo" %>

<%
OrderListInfo ol = (OrderListInfo)request.getAttribute("ol");
if (ol == null) {
    response.sendRedirect("OrderListServlet");
    return;
}

int orderId = ol.getOrderId();
String productName = ol.getProductName();
int productPrice = ol.getProductPrice();

String tableNum =
(String)session.getAttribute("tableNumber");

List<ItemDetailsInfo> toppingList =
(List<ItemDetailsInfo>)request.getAttribute("toppingList");

Integer subTotal =
(Integer)request.getAttribute("subTotal");

if (subTotal == null) {
    subTotal = productPrice;
}
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>商品変更画面</title>
<link rel="stylesheet" href="./css/style.css">
<script src="./js/windowScaler.js"></script>
</head>

<body style="margin:0; padding-bottom:120px; font-family:sans-serif;">

<!-- 商品 -->
<div style="padding:15px; border-bottom:1px solid #ccc;">
<table width="100%">
<tr>
<td align="left">
<strong style="font-size:22px;"><%= productName %></strong>
</td>
<td align="right"><%= productPrice %>円</td>
</tr>
</table>
</div>

<!-- トッピング -->
<div style="padding:10px;">
<table width="100%" cellpadding="10">

<%
if (toppingList != null) {
    for (int i = 0; i < toppingList.size(); i++) {

        ItemDetailsInfo t = toppingList.get(i);
%>

<tr>
<td width="60%">
<b><%= t.getToppingName() %></b><br>
<small><%= t.getToppingPrice() %>円</small>
</td>

<td width="40%" align="right">

<%-- ★ここが追加：在庫チェック --%>
<% if (t.getToppingStock() > 0) { %>

<form action="ItemDetailsChangeServlet" method="post" style="display:inline;">

<input type="hidden" name="orderId" value="<%= orderId %>">

<%
for (int j = 0; j < toppingList.size(); j++) {
%>
<input type="hidden"
       name="oldQty_<%= j %>"
       value="<%= toppingList.get(j).getToppingQuantity() %>">
<%
}
%>

<!-- − -->
<button type="submit"
        name="Button"
        value="-<%= i %>"
        style="width:40px; height:40px;"
        <%= (t.getToppingQuantity() <= 0) ? "disabled" : "" %>>
－
</button>

<!-- 数量 -->
<span style="display:inline-block; width:25px; text-align:center; font-weight:bold;">
<%= t.getToppingQuantity() %>
</span>

<!-- ＋ -->
<button type="submit"
        name="Button"
        value="+<%= i %>"
        style="width:40px; height:40px;"
        <%= (t.getToppingQuantity() >= 20) ? "disabled" : "" %>>
＋
</button>

</form>

<% } else { %>

<!-- ★ここ追加：売切表示 -->
<span style="color:black; font-weight:bold;">売切</span>

<% } %>

</td>
</tr>

<%
    }
}
%>

</table>
</div>

<!-- 小計 -->
<div align="right"
     style="padding:20px; border-top:1px solid #ccc; margin-bottom:70px;">
<strong style="font-size:24px;">
小計：<%= subTotal %>円
</strong>
</div>

<!-- 下固定 -->
<div style="position:fixed; bottom:0; left:0; width:100%;
            background:#fff; border-top:2px solid #333; padding:10px 0;">

<table width="100%">
<tr>

<!-- 戻る -->
<td align="center">
<form action="OrderListServlet" method="get">
<input type="submit" value="注文リスト"
       style="width:90%; height:50px;">
</form>
</td>

<!-- 卓番号 -->
<td align="center">
<strong style="font-size:1.5em;"><%= tableNum %>卓</strong>
</td>

<!-- 更新 -->
<td align="center">

<form action="ItemDetailsChangeServlet" method="post">

<input type="hidden" name="mode" value="update">
<input type="hidden" name="orderId" value="<%= orderId %>">

<%
for (int j = 0; j < toppingList.size(); j++) {
%>
<input type="hidden"
       name="oldQty_<%= j %>"
       value="<%= toppingList.get(j).getToppingQuantity() %>">
<%
}
%>

<input type="submit"
       value="更新"
       style="width:90%; height:50px; background:orange; color:white; border:none; font-weight:bold;">

</form>

</td>
</tr>
</table>

</div>

</body>
</html>