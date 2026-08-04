package servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import dao.OrderHistoryDAO;
import dao.OrderListDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.OrderHistoryInfo;
import model.OrderHistoryLogic;
import model.OrderListInfo;

@WebServlet("/OrderHistoryServlet")
public class OrderHistoryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("tableNumber") == null) {
            response.sendRedirect("error.jsp");
            return;
        } else {
            doPost(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        String tableNumber = (String) session.getAttribute("tableNumber");
        int sessionId = 0;
        try {
            if (tableNumber != null) {
                sessionId = Integer.parseInt(tableNumber);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        String action = request.getParameter("action");
        OrderHistoryDAO dao = new OrderHistoryDAO();
        OrderListDAO olDAO = new OrderListDAO();

        try {
            List<OrderHistoryInfo> orderHistoryList = dao.findOrderDetails(sessionId);
            List<OrderListInfo> olList = olDAO.findorderDetails(sessionId);

            OrderHistoryLogic logic = new OrderHistoryLogic();
            int totalOrderPrice = logic.calcTotalOrderPrice(orderHistoryList);
            int totalOrderQuantity = logic.calcTotalOrderQuantity(orderHistoryList);
            int popupStatus = logic.showPopUp(olList, orderHistoryList, action);

            // =========================================================
            // お会計確定処理 (「はい」が押された場合)
            // =========================================================
            if ("yes".equals(action) && popupStatus != 1) {
                
                // 旧URL切断 ➔ 新UUID生成 ➔ update_flag=1（QRコード作成要求）を一括実行
                dao.processCheckoutAndGenerateNewToken(sessionId);

                request.setAttribute("tableNumber", tableNumber);
                request.setAttribute("totalOrderPrice", totalOrderPrice);

                // 旧スマホ接続を安全に破棄
                session.invalidate();

                RequestDispatcher dispatcher = request.getRequestDispatcher("CheckOutServlet");
                dispatcher.forward(request, response);
                return;
            }

            // 通常表示処理
            request.setAttribute("orderHistoryList", orderHistoryList);
            request.setAttribute("tableNumber", tableNumber);
            request.setAttribute("totalOrderPrice", totalOrderPrice);
            request.setAttribute("totalOrderQuantity", totalOrderQuantity);
            request.setAttribute("popupStatus", popupStatus);

            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/orderHistory.jsp");
            dispatcher.forward(request, response);
            return;

        } catch (SQLException e) {
            e.printStackTrace();
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/error.jsp");
            if (!response.isCommitted()) {
                dispatcher.forward(request, response);
            }
        }
    }
}