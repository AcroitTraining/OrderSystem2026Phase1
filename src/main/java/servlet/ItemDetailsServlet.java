package servlet;

import java.io.IOException;
import java.util.List;

import dao.ToppingListDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.ItemDetailsInfo;
import model.ItemDetailsLogic;

@WebServlet("/ItemDetailsServlet")
public class ItemDetailsServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        request.setCharacterEncoding("UTF-8");

        String productIdStr = request.getParameter("productId");
        String productName = request.getParameter("productName");
        String category = request.getParameter("productCategory");
        String priceStr = request.getParameter("productPrice");
        String tableStr = request.getParameter("tableNumber");
        
        if (tableStr != null && !tableStr.isEmpty()) {
            session.setAttribute("tableNumber", tableStr);
        }
        int productId = (productIdStr != null) ? Integer.parseInt(productIdStr) : 0;
        int price = (priceStr != null) ? Integer.parseInt(priceStr) : 0;

        ToppingListDAO dao = new ToppingListDAO();
        // 商品ID(productId)を渡し、その商品に紐づくトッピング一覧のみを取得
        List<ItemDetailsInfo> tList = dao.findToppingListByProductId(productId, 0);
        
        request.setAttribute("productId", productId);
        request.setAttribute("selectedPName", productName);
        request.setAttribute("selectedPPrice", price);
        request.setAttribute("currentCategory", category);
        request.setAttribute("subTotal", price);
        request.setAttribute("toppingList", tList);
        request.getRequestDispatcher("WEB-INF/jsp/itemDetails.jsp")
                .forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        String button = request.getParameter("Button");
        String mode = request.getParameter("mode");
        String productIdStr = request.getParameter("productId");
        String productName = request.getParameter("productName");
        String priceStr = request.getParameter("productPrice");
        String subTotalStr = request.getParameter("subTotal");
        String category = request.getParameter("productCategory");

        if (productIdStr == null || priceStr == null || subTotalStr == null) {
            response.sendRedirect("ShowMenuServlet");
            return;
        }

        int productId = Integer.parseInt(productIdStr);
        int price = Integer.parseInt(priceStr);
        int subTotal = Integer.parseInt(subTotalStr);
        
        HttpSession session = request.getSession();
        int sessionId = 0;
        Object tableObj = session.getAttribute("tableNumber");
        if (tableObj != null) {
            sessionId = Integer.parseInt(tableObj.toString());
        }

        ToppingListDAO dao = new ToppingListDAO();
        // 商品ID(productId)に紐づくトッピング一覧を取得
        List<ItemDetailsInfo> tList = dao.findToppingListByProductId(productId, 0);

        // 数量復元（画面から送られてきた各トッピングの数量をtListに同期させる）
        for (int i = 0; i < tList.size(); i++) {
            String qty = request.getParameter("oldQty_" + i);
            if (qty != null) {
                tList.get(i).setToppingQuantity(Integer.parseInt(qty));
            }
        }

        // ＋ / − ボタンが押された時の処理
        if (button != null && (button.startsWith("+") || button.startsWith("-"))) {
            ItemDetailsLogic logic = new ItemDetailsLogic();
            String action = button.startsWith("+") ? "plus" : "minus";
            int index = Integer.parseInt(button.substring(1));
            
            logic.calcToppingQuantity(tList, index, action);
            subTotal = logic.calcSubTotal(price, tList);
            
            request.setAttribute("productId", productId);
            request.setAttribute("selectedPName", productName);
            request.setAttribute("selectedPPrice", price);
            request.setAttribute("currentCategory", category);
            request.setAttribute("subTotal", subTotal);
            request.setAttribute("toppingList", tList);
            request.getRequestDispatcher("WEB-INF/jsp/itemDetails.jsp")
                    .forward(request, response);
            return;
        }

        // 追加ボタン（確定）が押された時の処理
        if ("add".equals(mode)) {
            boolean ok = dao.insertProductDetail(productId);
            if (ok) {
                int orderId = dao.getLastOrderId();
                
                // 【連動完了】DAO側の引数とぴったり一致する6個で呼び出します
                dao.insertOrderDetail(
                        orderId, 1, subTotal, sessionId,
                        0, 0
                );
                
                // 商品在庫を減算
                dao.updateProductStock(productId);
                
                // 各トッピングの登録処理
                for (ItemDetailsInfo t : tList) {
                    int qty = t.getToppingQuantity();
                    int toppingId = t.getToppingId();
                    if (qty > 0) {
                        dao.insertMutipleToppings(toppingId, qty, orderId);
                        dao.updateToppingStock(toppingId, qty);
                    }
                }
                response.sendRedirect("OrderListServlet");
                return;
            }
        }
        response.sendRedirect("ShowMenuServlet");
    }
}