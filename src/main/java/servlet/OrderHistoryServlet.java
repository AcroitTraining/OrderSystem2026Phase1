package servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import dao.OrderHistoryDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.OrderHistoryInfo;
import model.OrderHistoryLogic;

@WebServlet("/OrderHistoryServlet")
public class OrderHistoryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
                    throws ServletException, IOException {
    	HttpSession session = request.getSession(false);
		// セッションがない、または卓番号などの必須データが消えている場合
		if (session == null || session.getAttribute("tableNumber") == null) {
			// 即座にエラー画面へ転送する
			response.sendRedirect("error.jsp");
			return;
		}else {
			doPost(request, response);
		}
    }
    
    protected void doPost(HttpServletRequest request, 
            HttpServletResponse response)
                    throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        
        // セッションから卓番号を取得
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

        try {
            // 注文履歴情報の取得
            List<OrderHistoryInfo> orderHistoryList = dao.findOrderDetails(sessionId);

            // ロジック実行 (合計計算、ポップアップ判定)
            OrderHistoryLogic logic = new OrderHistoryLogic();
            int totalOrderPrice = logic.calcTotalOrderPrice(orderHistoryList);
            int totalOrderQuantity = logic.calcTotalOrderQuantity(orderHistoryList);
            int popupStatus = logic.showPopUp(orderHistoryList, action);

            // お会計確定処理 (「はい」が押された場合)
            if ("yes".equals(action)) {
                dao.updateAccountingFlag(sessionId); 
                
                request.setAttribute("tableNumber", tableNumber);
                request.setAttribute("totalOrderPrice", totalOrderPrice);
                
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