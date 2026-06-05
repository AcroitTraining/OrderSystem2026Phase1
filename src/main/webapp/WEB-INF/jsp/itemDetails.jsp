<%@ page language="java"
contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>

<%@ page import="java.util.List, model.ItemDetailsInfo" %>
<%@ page import="java.util.Set" %>

<%
String productId = String.valueOf(request.getAttribute("productId"));
String pName = String.valueOf(request.getAttribute("selectedPName"));
Integer pPrice = (Integer)request.getAttribute("selectedPPrice");
List<ItemDetailsInfo> toppingList =
(List<ItemDetailsInfo>)request.getAttribute("toppingList");
Integer subTotal = (Integer)request.getAttribute("subTotal");
String category = (String)request.getAttribute("currentCategory");
String tableNum = (String)session.getAttribute("tableNumber");
String formAction = (String)request.getAttribute("formAction");
if (category == null) {
    category = "";
}
Set<String> toppingCategories = Set.of("お好み焼き","もんじゃ焼き");
boolean showTopping =
        toppingCategories.contains(category);
if (formAction == null) {
    formAction = "ItemDetailsServlet";
}
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>商品詳細</title>
<link rel="stylesheet" href="./css/style.css">
<script src="./js/windowScaler.js"></script>
</head>
<body style="margin:0;padding-bottom:120px;font-family:sans-serif;">
<!-- 商品名 -->
<table width="100%" style="padding:10px;">
<tr>
<td align="left">
<strong style="font-size:1.3em;"><%= pName %></strong>
</td>
<td align="right">
<%= pPrice %>円(税込)
</td>
</tr>
</table>
<hr>
<!-- トッピング -->
<% if (showTopping && toppingList != null) { %>
<div style="padding:10px;">
<table width="100%" cellpadding="10">
<%
for (int i = 0; i < toppingList.size(); i++) {
    ItemDetailsInfo t = toppingList.get(i);
%>
<tr>
<td width="60%">
<%= t.getToppingName() %><br>
<small><%= t.getToppingPrice() %>円</small>
</td>
<td width="40%" align="right">
<%-- ★在庫あり --%>
<% if (t.getToppingStock() > 0) { %>
<form action="<%= formAction %>" method="post" style="display:inline;">

<!-- 商品情報 -->
<input type="hidden" name="productId" value="<%= productId %>">
<input type="hidden" name="productName" value="<%= pName %>">
<input type="hidden" name="productPrice" value="<%= pPrice %>">
<input type="hidden" name="productCategory" value="<%= category %>">
<input type="hidden" name="subTotal" value="<%= subTotal %>">
<input type="hidden" name="orderId" value="<%= request.getAttribute("orderId") %>">
<input type="hidden" name="mode" value="view">
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
        style="width:40px;height:40px;"
        <%= (t.getToppingQuantity() <= 0) ? "disabled" : "" %>>
－
</button>
<!-- 数量 -->
<span style="display:inline-block;width:25px;text-align:center;font-weight:bold;">
<%= t.getToppingQuantity() %>
</span>
<!-- ＋ -->
<button type="submit"
        name="Button"
        value="+<%= i %>"
        style="width:40px;height:40px;"
        <%= (t.getToppingQuantity() >= 20) ? "disabled" : "" %>>
＋
</button>
</form>
<% } else { %>
<!-- ★在庫なし -->
<span style="color:black;font-weight:bold;font-size:16px;">
売切
</span>
<% } %>
</td>
</tr>
<%
}
%>
</table>
</div>
<% } %>
<!-- 小計 -->
<div align="right"
     style="padding:20px;
            border-top:1px solid #ccc;
            margin-bottom:70px;">
<strong style="font-size:1.3em;">
小計：<%= subTotal %>円(税込)
</strong>
</div>
<!-- 下固定 -->
<div style="position:fixed;
            bottom:0;
            left:0;
            width:100%;
            background:#fff;
            border-top:2px solid #333;
            padding:10px 0;">
<table width="100%" style="table-layout:fixed;">
<tr>
<!-- メニュー -->
<td align="center">
<form action="ShowMenuServlet" method="post">
<input type="submit" value="メニュー"
       style="width:90%;height:50px;">
</form>
</td>
<!-- 卓番号 -->
<td align="center">
<strong style="font-size:1.5em;"><%= tableNum %>卓</strong>
</td>
<!-- 追加 -->
<td align="center">
<form action="<%= formAction %>" method="post">
<!-- 商品情報 -->
<input type="hidden" name="productId" value="<%= productId %>">
<input type="hidden" name="productName" value="<%= pName %>">
<input type="hidden" name="productPrice" value="<%= pPrice %>">
<input type="hidden" name="productCategory" value="<%= category %>">
<input type="hidden" name="subTotal" value="<%= subTotal %>">
<input type="hidden" name="orderId" value="<%= request.getAttribute("orderId") %>">
<input type="hidden" name="mode" value="add">
<%
if (toppingList != null) {
    for (int j = 0; j < toppingList.size(); j++) {
%>
<input type="hidden"
       name="oldQty_<%= j %>"
       value="<%= toppingList.get(j).getToppingQuantity() %>">
<%
    }
}
%>

<input type="submit"
       name="Button"
       value="追加"
       style="width:90%;height:50px;background:orange;color:white;border:none;font-weight:bold;">
</form>
</td>
</tr>
</table>
</div>
</body>
</html>