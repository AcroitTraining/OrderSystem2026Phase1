package servlet;

import java.io.IOException;

import dao.OrderRemoveDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.OrderRemoveInfo;

@WebServlet("/OrderRemoveServlet")
public class OrderRemoveServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String oid = request.getParameter("oid");
		int num = Integer.parseInt(oid);
		OrderRemoveInfo orInfo = new OrderRemoveInfo();
		OrderRemoveDAO orDAO = new OrderRemoveDAO();
		orDAO.deleteOrderDetails(num);

		RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/jsp/Remove.jsp");
		dispatcher.forward(request, response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String oid = request.getParameter("oid");
		System.out.println("id取得パラメーター" + oid);
		int num = Integer.parseInt(oid);
		OrderRemoveInfo orInfo = new OrderRemoveInfo();
		OrderRemoveDAO orDAO = new OrderRemoveDAO();
		orDAO.deleteOrderDetails(num);

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

		RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/jsp/Remove.jsp");
		dispatcher.forward(request, response);
	}

}
