package servlet;

import java.io.IOException;
import java.util.List;

import dao.ToppingDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.ItemDetailsChangeLogic;
import model.ItemDetailsInfo;
import model.OrderListInfo;

@WebServlet("/ItemDetailsChangeServlet")
public class ItemDetailsChangeServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("tableNumber") == null) {
            response.sendRedirect("error.jsp");
            return;
        }
        
        request.setCharacterEncoding("UTF-8");
        String orderIdStr = request.getParameter("orderId");
        if (orderIdStr == null || orderIdStr.isEmpty()) {
            orderIdStr = request.getParameter("oid");
        }
        if (orderIdStr == null || orderIdStr.isEmpty()) {
            response.sendRedirect("OrderListServlet");
            return;
        }
        
        int orderId = Integer.parseInt(orderIdStr);
        ToppingDAO dao = new ToppingDAO();
        OrderListInfo ol = dao.findOrderInfo(orderId);
        List<ItemDetailsInfo> toppingList = dao.findToppingListByOrderId(orderId);
        ItemDetailsChangeLogic logic = new ItemDetailsChangeLogic();
        int subTotal = logic.calcSubTotal(
                ol.getProductPrice(),
                toppingList
        );
        request.setAttribute("ol", ol);
        request.setAttribute("toppingList", toppingList);
        request.setAttribute("subTotal", subTotal);
        request.getRequestDispatcher(
                "/WEB-INF/jsp/itemDetailsChange.jsp"
        ).forward(request, response);
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String orderIdStr = request.getParameter("orderId");
        if (orderIdStr == null || orderIdStr.isEmpty()) {
            orderIdStr = request.getParameter("oid");
        }
        if (orderIdStr == null || orderIdStr.isEmpty()) {
            response.sendRedirect("OrderListServlet");
            return;
        }
        int orderId = Integer.parseInt(orderIdStr);
        String button = request.getParameter("Button");
        String mode = request.getParameter("mode");
        ToppingDAO dao = new ToppingDAO();
        OrderListInfo ol = dao.findOrderInfo(orderId);
        List<ItemDetailsInfo> toppingList = dao.findToppingListByOrderId(orderId);

        // 1. 画面の数量を先に一括復元
        for (int i = 0; i < toppingList.size(); i++) {
            String qty = request.getParameter("oldQty_" + i);
            if (qty != null && !qty.isEmpty()) {
                toppingList.get(i).setToppingQuantity(Integer.parseInt(qty));
            }
        }
        
        ItemDetailsChangeLogic logic = new ItemDetailsChangeLogic();

        // 2. ＋ / －ボタンが押された場合の処理
        if (button != null && (button.startsWith("+") || button.startsWith("-"))) {
            int index = Integer.parseInt(button.substring(1));
            String action = button.startsWith("+") ? "plus" : "minus";
            
            logic.calcToppingQuantity(toppingList, index, action);
            
            int subTotal = logic.calcSubTotal(
                    ol.getProductPrice(),
                    toppingList);
            
            request.setAttribute("ol", ol);
            request.setAttribute("toppingList", toppingList);
            request.setAttribute("subTotal", subTotal);
            request.getRequestDispatcher(
                    "/WEB-INF/jsp/itemDetailsChange.jsp"
            ).forward(request, response);

            return;
        }

        // 3. ★変更ボタンでDBを更新
        if ("update".equals(mode)) {
            // 最新のトッピング状態から小計(subTotal)を算出する
            int subTotal = logic.calcSubTotal(
                    ol.getProductPrice(),
                    toppingList);

            List<ItemDetailsInfo> dbList = dao.findToppingListByOrderId(orderId);
            for (int i = 0; i < toppingList.size(); i++) {
                ItemDetailsInfo screen = toppingList.get(i);
                ItemDetailsInfo db = dbList.get(i);
                int screenQty = screen.getToppingQuantity();
                int dbQty = db.getToppingQuantity();

                // 0 → 1以上（新規追加：INSERT）
                if (dbQty == 0 && screenQty > 0) {
                    dao.insertTopping(orderId, screen.getToppingId(), screenQty);
                    dao.updateToppingStock(screen.getToppingId(), screenQty);
                }
                // 1以上 → 0（完全削除：DELETE）
                else if (dbQty > 0 && screenQty == 0) {
                    dao.deleteTopping(orderId, screen.getToppingId());
                    dao.updateToppingStock(screen.getToppingId(), -dbQty);
                }
                // 1以上 → 別の数量（数量変更：UPDATE）
                else if (dbQty > 0 && screenQty > 0 && dbQty != screenQty) {
                    dao.updateToppingQuantity(orderId, screen.getToppingId(), screenQty);
                    
                    int diff = screenQty - dbQty;
                    dao.updateToppingStock(screen.getToppingId(), diff);
                }
            }

            // ★ここで計算された小計(subTotal)を使って、order_detailsテーブルのorder_priceを一括更新します
            dao.updateOrderPrice(orderId, subTotal);

            response.sendRedirect("OrderListServlet");
            return;
        }

        response.sendRedirect("OrderListServlet");
    }
}