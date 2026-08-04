package servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dao.OrderStartDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.OrderStartLogic;
import model.TableInfo;

@WebServlet("/OrderStartServlet")
public class OrderStartServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * ★ QRコード読み取り・URL直接アクセス（GETリクエスト）に対応するメソッド
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
	        throws ServletException, IOException {

	    String token = request.getParameter("tt");
	    int tableId = -1;
	    int sessionId = -1;

	    if (token != null && !token.isEmpty()) {
	        // トークンから table_id と session_id を取得
	        String sql = "SELECT table_id, session_id FROM table_sessions WHERE url_token = ? AND session_status != 'closed'";

	        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/order_management", "order", "1234");
	             PreparedStatement ps = conn.prepareStatement(sql)) {

	            ps.setString(1, token);
	            try (ResultSet rs = ps.executeQuery()) {
	                if (rs.next()) {
	                    tableId = rs.getInt("table_id");
	                    sessionId = rs.getInt("session_id");
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    if (tableId != -1) {
	        HttpSession session = request.getSession();
	        
	        // ★ 単体の table_id だけではなく、システム全域で使われる tableInfo もセッションにセットする
	        TableInfo tableInfo = new TableInfo(tableId, sessionId, "active");
	        session.setAttribute("table_id", tableId);
	        session.setAttribute("tableInfo", tableInfo);

	        System.out.println("✅ 卓番 " + tableId + " (sessionId: " + sessionId + ") のセッションを正常開始しました。");

	        // 人数選択画面（orderStart.jsp）へ遷移
	        request.setAttribute("tableNumber", tableId);
	        request.setAttribute("guestCount", 1);
	        RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/jsp/orderStart.jsp");
	        dispatcher.forward(request, response);

	    } else {
	        // トークンが無効（すでに閉じた会計など）の場合
	        response.setContentType("text/html;charset=UTF-8");
	        response.getWriter().println("<h3>無効または期限切れのQRコードです。店員をお呼びください。</h3>");
	    }
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		request.setCharacterEncoding("UTF-8");

		// パラメータ取得
		String tableIdStr = request.getParameter("tableId");
		String guestCountStr = request.getParameter("guestCount");
		String action = request.getParameter("action");

		// セッションから table_id を優先取得
		int tableId = -1;
		if (tableIdStr != null && !tableIdStr.isEmpty()) {
			tableId = Integer.parseInt(tableIdStr);
		} else if (session.getAttribute("table_id") != null) {
			tableId = (Integer) session.getAttribute("table_id");
		} else {
			tableId = 1;
		}

		int guestCount = (guestCountStr == null || guestCountStr.isEmpty()) ? 1 : Integer.parseInt(guestCountStr);

		OrderStartLogic logic = new OrderStartLogic();
		OrderStartDAO dao = new OrderStartDAO();

		if ("start".equals(action)) {
			// DB更新処理（人数やステータス）
			dao.updateStatus(tableId, guestCount);
			int sessionId = dao.findSessionId(tableId);	
			
			// tableInfo を更新してセッションへ格納
			TableInfo tableInfo = new TableInfo(tableId, sessionId, "active");
			session.setAttribute("tableInfo", tableInfo);
			session.setAttribute("table_id", tableId);

			System.out.println("orderstartservlet [POST] 完了 - 卓番: " + tableId + ", sessionId: " + sessionId);

			// メニュー表示サーブレットへ移動
			RequestDispatcher dispatcher = request.getRequestDispatcher("ShowMenuServlet");
			dispatcher.forward(request, response);

		} else {
			// 人数プラス・マイナスボタン等
			if (action != null) {
				guestCount = logic.updateGuestCount(guestCount, action);
			}

			request.setAttribute("tableNumber", tableId);
			request.setAttribute("guestCount", guestCount);
			
			if ("true".equals(request.getParameter("ajax"))) {
				response.setContentType("text/plain;charset=UTF-8");
				response.getWriter().print(guestCount);
				return;
			}

			RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/jsp/orderStart.jsp");
			dispatcher.forward(request, response);
		}
	}
}