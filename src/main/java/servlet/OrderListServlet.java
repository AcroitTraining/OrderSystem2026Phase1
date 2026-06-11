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
		OrderListInfo ol = new OrderListInfo();
		OrderListLogic logic = new OrderListLogic();
		request.setCharacterEncoding("UTF-8");
		OrderListDAO olDAO = new OrderListDAO();
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

		//データ取得処理
		try {
			List<OrderListInfo> olList = olDAO.findorderDetails(sessionId);
			request.setAttribute("olList", olList);
			OrderListInfo allOrderPrice = olDAO.findAllOrderPrice(sessionId);
			request.setAttribute("aop", allOrderPrice);


		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}		
		//logic.calcSubTotal();
		RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/jsp/orderList.jsp");
		dispatcher.forward(request, response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String Button = request.getParameter("Button");
		OrderListDAO olDAO = new OrderListDAO();
		OrderListLogic logic = new OrderListLogic();
		String stoid = request.getParameter("oid");
		int oid = Integer.parseInt(stoid);

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


		//イベント処理
		if("追加".equals(Button)){
			
			//データ取得処理
			try {
				List<OrderListInfo> olList = olDAO.findorderDetails(sessionId);
				request.setAttribute("olList", olList);
				OrderListInfo allOrderPrice = olDAO.findAllOrderPrice(sessionId);
				request.setAttribute("aop", allOrderPrice);
				


			} catch (SQLException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}		
			logic.calcSubTotal();
		}else if("+".equals(Button)) {
			//プラス処理
			logic.calcOrderQuantity(1, oid);
			//データ取得処理
			try {
				List<OrderListInfo> olList = olDAO.findorderDetails(sessionId);
				request.setAttribute("olList", olList);
				OrderListInfo allOrderPrice = olDAO.findAllOrderPrice(sessionId);
				request.setAttribute("aop", allOrderPrice);


			} catch (SQLException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}		
			logic.calcSubTotal();
			RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/jsp/orderList.jsp");
			dispatcher.forward(request, response);

		}else if("-".equals(Button)) {
			//マイナス処理
			logic.calcOrderQuantity(-1, oid);
			//データ取得処理
			try {
				List<OrderListInfo> olList = olDAO.findorderDetails(sessionId);
				request.setAttribute("olList", olList);
				OrderListInfo allOrderPrice = olDAO.findAllOrderPrice(sessionId);
				request.setAttribute("aop", allOrderPrice);


			} catch (SQLException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}		
			logic.calcSubTotal();
			RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/jsp/orderList.jsp");
			dispatcher.forward(request, response);
		}else if("削除".equals(Button)) {
			 // リダイレクト先のサーブレットパス（URL）を指定
	        String targetUrl = "OrderRemoveServlet"; 
	        
	        // リダイレクトの実行
	        response.sendRedirect(targetUrl);
		}

	}

}