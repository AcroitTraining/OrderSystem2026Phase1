package servlet;

import java.io.IOException;

import dao.OrderRemoveDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/OrderRemoveServlet")
public class OrderRemoveServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// JavaScriptのForm送信(method="post")からここに来ます
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		// セッションチェック（安全対策）
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("tableNumber") == null) {
			response.sendRedirect("error.jsp");
			return;
		}

		// 削除する注文IDを取得
		String oid = request.getParameter("oid");
		int num = Integer.parseInt(oid);
		
		// データベースから削除を実行
		OrderRemoveDAO orDAO = new OrderRemoveDAO();
		orDAO.deleteOrderDetails(num);

		// ★【重要】削除が終わったら、すぐに注文リスト（OrderListServlet）へ強制送還
		response.sendRedirect("OrderListServlet");
	}

	// 万が一GETでアクセスされた場合も同じ動きにする
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
}