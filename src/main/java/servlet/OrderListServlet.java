package servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import dao.OrderListDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.OrderListInfo;
import model.OrderListLogic;

@WebServlet("/OrderListServlet")
public class OrderListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("tableNumber") == null) {
			response.sendRedirect("error.jsp");
			return;
		}

		String tableNumber = (String) session.getAttribute("tableNumber");
		int sessionId = 0;
		try {
			if (tableNumber != null) {
				sessionId = Integer.parseInt(tableNumber);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		
		OrderListDAO olDAO = new OrderListDAO();

		try {
			List<OrderListInfo> olList = olDAO.findorderDetails(sessionId);
			request.setAttribute("olList", olList);
			OrderListInfo allOrderPrice = olDAO.findAllOrderPrice(sessionId);
			request.setAttribute("aop", allOrderPrice);

		} catch (SQLException e) {
			e.printStackTrace();
		}		

		RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/jsp/orderList.jsp");
		dispatcher.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String Button = request.getParameter("Button");
		OrderListDAO olDAO = new OrderListDAO();
		OrderListLogic logic = new OrderListLogic();
		
		String stoid = request.getParameter("oid");
		int oid = 0;
		if (stoid != null) {
			oid = Integer.parseInt(stoid);
		}

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

		// イベント処理の分岐
		if("追加".equals(Button)){
			try {
				List<OrderListInfo> olList = olDAO.findorderDetails(sessionId);
				request.setAttribute("olList", olList);
				OrderListInfo allOrderPrice = olDAO.findAllOrderPrice(sessionId);
				request.setAttribute("aop", allOrderPrice);
			} catch (SQLException e) {
				e.printStackTrace();
			}		
			logic.calcSubTotal();
			
		} else if("+".equals(Button)) {
			logic.calcOrderQuantity(1, oid);
			try {
				List<OrderListInfo> olList = olDAO.findorderDetails(sessionId);
				request.setAttribute("olList", olList);
				OrderListInfo allOrderPrice = olDAO.findAllOrderPrice(sessionId);
				request.setAttribute("aop", allOrderPrice);
			} catch (SQLException e) {
				e.printStackTrace();
			}		
			logic.calcSubTotal();
			RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/jsp/orderList.jsp");
			dispatcher.forward(request, response);

		} else if("-".equals(Button)) {
			logic.calcOrderQuantity(-1, oid);
			try {
				List<OrderListInfo> olList = olDAO.findorderDetails(sessionId);
				request.setAttribute("olList", olList);
				OrderListInfo allOrderPrice = olDAO.findAllOrderPrice(sessionId);
				request.setAttribute("aop", allOrderPrice);
			} catch (SQLException e) {
				e.printStackTrace();
			}		
			logic.calcSubTotal();
			RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/jsp/orderList.jsp");
			dispatcher.forward(request, response);
			
		} 
		// ★JavaScriptから直でOrderRemoveServletを叩くようになったため、
		// ここにあった「削除」の分岐は通らなくなります。コードの記述自体消去しても安全です。
	}
}