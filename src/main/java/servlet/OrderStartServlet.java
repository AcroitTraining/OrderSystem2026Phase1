package servlet;

import java.io.IOException;
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

	private OrderStartDAO orderStartDAO;
	private OrderStartLogic orderStartLogic;

	/**
	 * ① 通常運用時のデフォルトコンストラクタ
	 */
	public OrderStartServlet() {
		this.orderStartDAO = new OrderStartDAO();
		this.orderStartLogic = new OrderStartLogic();
	}

	/**
	 * ② テスト（DI）用のコンストラクタ
	 */
	public OrderStartServlet(OrderStartDAO orderStartDAO, OrderStartLogic orderStartLogic) {
		this.orderStartDAO = orderStartDAO;
		this.orderStartLogic = orderStartLogic;
	}

	// Mockito 等で個別に注入するためのセッター
	public void setOrderStartDAO(OrderStartDAO orderStartDAO) {
		this.orderStartDAO = orderStartDAO;
	}

	public void setOrderStartLogic(OrderStartLogic orderStartLogic) {
		this.orderStartLogic = orderStartLogic;
	}

	/**
	 * ★ QRコード読み取り・URL直接アクセス（GETリクエスト）に対応するメソッド
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {

		String token = request.getParameter("tt");

		try {
			TableInfo tableInfo = orderStartDAO.findTableSessionByToken(token);

			if (tableInfo != null) {
				HttpSession session = request.getSession();
				session.setAttribute("table_id", tableInfo.getTableId());
				session.setAttribute("tableInfo", tableInfo);

				System.out.println("✅ 卓番 " + tableInfo.getTableId() 
						+ " (sessionId: " + tableInfo.getSessionId() 
						+ ", status: " + tableInfo.getSessionStatus() + ") のセッションを確認しました。");

				if ("active".equals(tableInfo.getSessionStatus())) {
					// ★ 既に稼働中（後から来た人）→ 人数設定スキップしてメニューへ直行
					// ShowMenuServlet が要求する tableNumber をセッションにセットしとく
					session.setAttribute("tableNumber", String.valueOf(tableInfo.getTableId()));

					RequestDispatcher dispatcher = request.getRequestDispatcher("ShowMenuServlet");
					dispatcher.forward(request, response);

				} else {
					// 空席（inactive）→ 従来通り人数選択画面へ
					request.setAttribute("tableNumber", tableInfo.getTableId());
					request.setAttribute("guestCount", 1);
					RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/jsp/orderStart.jsp");
					dispatcher.forward(request, response);
				}

			} else {
				response.setContentType("text/html;charset=UTF-8");
				response.getWriter().println("<h3>無効または期限切れのQRコードです。店員をお呼びください。</h3>");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().println("<h3>データベースエラーが発生しました。店員をお呼びください。</h3>");
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

		// ★ tableId の解決（不正値・卓不明はエラーとして弾く）
		Integer tableId;
		try {
			if (tableIdStr != null && !tableIdStr.isEmpty()) {
				tableId = Integer.parseInt(tableIdStr);
			} else if (session.getAttribute("table_id") != null) {
				tableId = (Integer) session.getAttribute("table_id");
			} else {
				tableId = null; // 固定値で誤魔化さない
			}
		} catch (NumberFormatException e) {
			tableId = null;
		}

		if (tableId == null) {
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().println("<h3>卓情報が確認できませんでした。店員をお呼びください。</h3>");
			return;
		}

		// ★ guestCount の解決
		int guestCount;
		try {
			guestCount = (guestCountStr == null || guestCountStr.isEmpty()) ? 1 : Integer.parseInt(guestCountStr);
		} catch (NumberFormatException e) {
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().println("<h3>不正なリクエストです。店員をお呼びください。</h3>");
			return;
		}

		if ("start".equals(action)) {
			try {
				orderStartDAO.updateStatus(tableId, guestCount);
				Integer sessionId = orderStartDAO.findSessionId(tableId);

				if (sessionId == null) {
					response.setContentType("text/html;charset=UTF-8");
					response.getWriter().println("<h3>セッション情報の取得に失敗しました。店員をお呼びください。</h3>");
					return;
				}

				TableInfo tableInfo = new TableInfo(tableId, sessionId, "active");
				session.setAttribute("tableInfo", tableInfo);
				session.setAttribute("table_id", tableId);
				session.setAttribute("tableNumber", String.valueOf(tableId)); // ★追加

				System.out.println("orderstartservlet [POST] 完了 - 卓番: " + tableId + ", sessionId: " + sessionId);

				RequestDispatcher dispatcher = request.getRequestDispatcher("ShowMenuServlet");
				dispatcher.forward(request, response);
			} catch (SQLException e) {
				e.printStackTrace();
				response.setContentType("text/html;charset=UTF-8");
				response.getWriter().println("<h3>データベースエラーが発生しました。店員をお呼びください。</h3>");
			}
		} else {
			// 人数プラス・マイナスボタン等
			if (action != null) {
				guestCount = orderStartLogic.updateGuestCount(guestCount, action);
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