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
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
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
        
        if (ol == null) {
            response.sendRedirect("OrderListServlet");
            return;
        }

        // 【修正】modelを通さず、DAOから直接 productId を取得
        int productId = dao.getProductIdByOrderId(orderId);

        // その商品のトッピング一覧のみを取得
        List<ItemDetailsInfo> toppingList = dao.findToppingListByProductId(productId, orderId);
        
        ItemDetailsChangeLogic logic = new ItemDetailsChangeLogic();
        int subTotal = logic.calcSubTotal(ol.getProductPrice(), toppingList);
        
        request.setAttribute("ol", ol);
        request.setAttribute("toppingList", toppingList);
        request.setAttribute("subTotal", subTotal);
        request.getRequestDispatcher("/WEB-INF/jsp/itemDetailsChange.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
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
        
        if (ol == null) {
            response.sendRedirect("OrderListServlet");
            return;
        }
        
        // 【修正】POST時もDAOから直接 productId を取得
        int productId = dao.getProductIdByOrderId(orderId);
        
        // その商品のトッピング一覧のみを取得
        List<ItemDetailsInfo> toppingList = dao.findToppingListByProductId(productId, orderId);

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
            int subTotal = logic.calcSubTotal(ol.getProductPrice(), toppingList);
            
            request.setAttribute("ol", ol);
            request.setAttribute("toppingList", toppingList);
            request.setAttribute("subTotal", subTotal);
            request.getRequestDispatcher("/WEB-INF/jsp/itemDetailsChange.jsp").forward(request, response);
            return;
        }

        // 3. 変更ボタンでDBを更新
        if ("update".equals(mode)) {
            int subTotal = logic.calcSubTotal(ol.getProductPrice(), toppingList);
            List<ItemDetailsInfo> dbList = dao.findToppingListByProductId(productId, orderId);
            
            for (int i = 0; i < toppingList.size(); i++) {
                ItemDetailsInfo screen = toppingList.get(i);
                ItemDetailsInfo db = dbList.get(i);
                int screenQty = screen.getToppingQuantity();
                int dbQty = db.getToppingQuantity();

                if (dbQty == 0 && screenQty > 0) {
                    dao.insertTopping(orderId, screen.getToppingId(), screenQty);
                    dao.updateToppingStock(screen.getToppingId(), screenQty);
                }
                else if (dbQty > 0 && screenQty == 0) {
                    dao.deleteTopping(orderId, screen.getToppingId());
                    dao.updateToppingStock(screen.getToppingId(), -dbQty);
                }
                else if (dbQty > 0 && screenQty > 0 && dbQty != screenQty) {
                    dao.updateToppingQuantity(orderId, screen.getToppingId(), screenQty);
                    int diff = screenQty - dbQty;
                    dao.updateToppingStock(screen.getToppingId(), diff);
                }
            }
            dao.updateOrderPrice(orderId, subTotal);

            response.sendRedirect("OrderListServlet");
            return;
        }

        response.sendRedirect("OrderListServlet");
    }
}