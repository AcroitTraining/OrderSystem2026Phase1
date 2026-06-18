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

/**
 * 注文一覧画面（orderList.jsp）に関するリクエストを制御するサーブレットクラスです。
 * 
 * <p>
 * 画面の初期表示（GETリクエスト）や、数量変更・追加などのイベント処理（POSTリクエスト）
 * に応じて、DAOやロジッククラスを呼び出し、必要なデータをJSPへ転送します。
 * </p>
 * 
 * @author Akishima Ryuta
 * @version 1.0
 */
@WebServlet("/OrderListServlet")
public class OrderListServlet extends HttpServlet {
	
	/** シリアルバージョンUID */
	private static final long serialVersionUID = 1L;

	/**
	 * 注文一覧画面の初期表示処理を行います（GETリクエスト対応）。
	 * 
	 * <p>
	 * セッションから卓番号を取得してセッションID（卓番号の数値）に変換し、
	 * 対象の注文明細一覧と合計金額を取得してリクエストスコープに設定します。
	 * その後、注文一覧のJSP（WEB-INF/jsp/orderList.jsp）へフォワードします。
	 * セッションや卓番号が存在しない場合はエラー画面（error.jsp）へリダイレクトします。
	 * </p>
	 * 
	 * @param request クライアントがサーブレットへ行うリクエスト内容を含む {@link HttpServletRequest} オブジェクト
	 * @param response サーブレットがクライアントへ返すレスポンス内容を含む {@link HttpServletResponse} オブジェクト
	 * @throws ServletException サーブレットがGETリクエストを処理している間に例外が発生した場合
	 * @throws IOException サーブレットが入出力を処理している間にI/Oエラーが発生した場合
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession(false);

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

	/**
	 * 注文一覧画面におけるボタン操作のイベント処理を行います（POSTリクエスト対応）。
	 * 
	 * <p>
	 * 押下されたボタンの種類（"追加", "+", "-"）に応じて以下の処理を分岐します。
	 * </p>
	 * <ul>
	 *   <li><b>「追加」ボタン:</b> 注文詳細と合計金額を再取得し、小計を再計算します。</li>
	 *   <li><b>「+」ボタン:</b> 指定された注文IDの数量を1増やし、最新データを再取得して小計再計算後、JSPへフォワードします。</li>
	 *   <li><b>「-」ボタン:</b> 指定された注文IDの数量を1減らし、最新データを再取得して小計再計算後、JSPへフォワードします。</li>
	 * </ul>
	 * <p>
	 * ※以前存在していた「削除」ボタンの分岐は、JavaScriptから直接OrderRemoveServletを
	 * 呼び出す仕様に変更されたため削除されています。
	 * </p>
	 * 
	 * @param request クライアントがサーブレットへ行うリクエスト内容を含む {@link HttpServletRequest} オブジェクト
	 * @param response サーブレットがクライアントへ返すレスポンス内容を含む {@link HttpServletResponse} オブジェクト
	 * @throws ServletException サーブレットがPOSTリクエストを処理している間に例外が発生した場合
	 * @throws IOException サーブレットが入出力を処理している間にI/Oエラーが発生した場合
	 */
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
			
			
		} 
		response.sendRedirect("OrderListServlet");
		
	}
}
