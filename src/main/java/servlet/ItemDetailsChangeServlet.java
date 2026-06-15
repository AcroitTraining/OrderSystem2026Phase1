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
        // セッションがない、または卓番号などの必須データが消えている場合
        if (session == null || session.getAttribute("tableNumber") == null) {
            // 即座にエラー画面へ転送する
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
            
            // ① 数量を変更する
            logic.calcToppingQuantity(toppingList, index, action);
            
            // ② 変更された最新の数量を元に、小計を計算する（★ここがポイントです）
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

        // 3. ★変更ボタンでDBを更新（同時にストックも連動変更）
        if ("update".equals(mode)) {
            // ここでも最新の数量から小計を割り出す
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
                    // 在庫マイナス
                    dao.updateToppingStock(screen.getToppingId(), screenQty);
                }
                // 1以上 → 0（完全削除：DELETE）
                else if (dbQty > 0 && screenQty == 0) {
                    dao.deleteTopping(orderId, screen.getToppingId());
                    // 在庫プラス
                    dao.updateToppingStock(screen.getToppingId(), -dbQty);
                }
                // 1以上 → 別の数量（数量変更：UPDATE）
                else if (dbQty > 0 && screenQty > 0 && dbQty != screenQty) {
                    dao.updateToppingQuantity(orderId, subTotal, screen.getToppingId(), screenQty);
                    
                    // 差分を計算
                    int diff = screenQty - dbQty;
                    dao.updateToppingStock(screen.getToppingId(), diff);
                }
            }

            // 完了後は注文リストへ
            response.sendRedirect("OrderListServlet");
            return;
        }

        response.sendRedirect("OrderListServlet");
    }
}