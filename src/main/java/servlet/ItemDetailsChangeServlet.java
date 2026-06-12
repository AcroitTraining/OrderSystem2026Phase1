package servlet;

import java.io.IOException;
import java.util.List;

import dao.ToppingDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ItemDetailsChangeLogic;
import model.ItemDetailsInfo;
import model.OrderListInfo;

@WebServlet("/ItemDetailsChangeServlet")
public class ItemDetailsChangeServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request,
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

        // 画面の数量復元
        for (int i = 0; i < toppingList.size(); i++) {
            String qty = request.getParameter("oldQty_" + i);
            if (qty != null && !qty.isEmpty()) {
                toppingList.get(i).setToppingQuantity(Integer.parseInt(qty));
            }
        }
        ItemDetailsChangeLogic logic = new ItemDetailsChangeLogic();

        // ＋ / －ボタン
        if (button != null && (button.startsWith("+") || button.startsWith("-"))) {
            int index = Integer.parseInt(button.substring(1));
            String action = button.startsWith("+") ? "plus" : "minus";
            logic.calcToppingQuantity(toppingList, index, action);
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

            return;
        }

        // ★変更ボタンでDBを更新（同時にストックも連動変更）
        if ("update".equals(mode)) {
            List<ItemDetailsInfo> dbList =
                    dao.findToppingListByOrderId(orderId);
            for (int i = 0; i < toppingList.size(); i++) {
                ItemDetailsInfo screen = toppingList.get(i);
                ItemDetailsInfo db = dbList.get(i);
                int screenQty = screen.getToppingQuantity();
                int dbQty = db.getToppingQuantity();

                // 0 → 1以上（新規追加：INSERT）
                if (dbQty == 0 && screenQty > 0) {
                    dao.insertTopping(orderId, screen.getToppingId(), screenQty);
                    
                    // 新しく増えた分（screenQty分）だけ在庫をマイナス
                    dao.updateToppingStock(screen.getToppingId(), screenQty);
                }

                // 1以上 → 0（完全削除：DELETE）
                else if (dbQty > 0 && screenQty == 0) {
                    dao.deleteTopping(orderId, screen.getToppingId());
                    
                    // なくなった分（dbQty分）だけ在庫をプラス（負の数を引くことで加算される）
                    dao.updateToppingStock(screen.getToppingId(), -dbQty);
                }

                // 1以上 → 別の数量（数量変更：UPDATE）
                else if (dbQty > 0 && screenQty > 0 && dbQty != screenQty) {
                    dao.updateToppingQuantity(orderId, screen.getToppingId(), screenQty);
                    
                    // 差分を計算（例：旧2個→新5個なら 5-2 = 3個減算、旧5個→新2個なら 2-5 = -3個で3個加算戻し）
                    int diff = screenQty - dbQty;
                    dao.updateToppingStock(screen.getToppingId(), diff);
                }
            }

            // ※製品(product)のストックは何も変更しないので、このままリダイレクト
            response.sendRedirect("OrderListServlet");
            return;
        }

        response.sendRedirect("OrderListServlet");
    }
}