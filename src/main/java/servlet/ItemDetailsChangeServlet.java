package servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import dao.ToppingDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.ItemDetailsChangeLogic;
import model.ItemDetailsInfo;
import model.OrderListInfo;

@WebServlet("/ItemDetailsChangeServlet")
public class ItemDetailsChangeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private ToppingDAO toppingDAO;
	private ItemDetailsChangeLogic itemDetailsChangeLogic;

	/**
	 * ① 通常運用時のデフォルトコンストラクタ
	 */
	public ItemDetailsChangeServlet() {
		this.toppingDAO = new ToppingDAO();
		this.itemDetailsChangeLogic = new ItemDetailsChangeLogic();
	}

	/**
	 * ② テスト（DI）用のコンストラクタ
	 */
	public ItemDetailsChangeServlet(ToppingDAO toppingDAO, ItemDetailsChangeLogic itemDetailsChangeLogic) {
		this.toppingDAO = toppingDAO;
		this.itemDetailsChangeLogic = itemDetailsChangeLogic;
	}

	// Mockito 等で個別に注入するためのセッター
	public void setToppingDAO(ToppingDAO toppingDAO) {
		this.toppingDAO = toppingDAO;
	}

	public void setItemDetailsChangeLogic(ItemDetailsChangeLogic itemDetailsChangeLogic) {
		this.itemDetailsChangeLogic = itemDetailsChangeLogic;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("tableNumber") == null) {
			response.sendRedirect("error.jsp");
			return;
		}

		request.setCharacterEncoding("UTF-8");
		String orderIdStr = request.getParameter("orderId");
		if (orderIdStr == null || orderIdStr.isEmpty()) {
			orderIdStr = request.getParameter("oid");
		}
		if (orderIdStr == null || orderIdStr.isEmpty()) {
			response.sendRedirect("OrderListServlet");
			return;
		}

		int orderId = Integer.parseInt(orderIdStr);

		try {
			OrderListInfo ol = toppingDAO.findOrderInfo(orderId);

			if (ol == null) {
				response.sendRedirect("OrderListServlet");
				return;
			}

			// DAOから直接 productId を取得
			int productId = toppingDAO.getProductIdByOrderId(orderId);

			// その商品のトッピング一覧のみを取得
			List<ItemDetailsInfo> toppingList = toppingDAO.findToppingListByProductId(productId, orderId);

			int subTotal = itemDetailsChangeLogic.calcSubTotal(ol.getProductPrice(), toppingList);

			request.setAttribute("ol", ol);
			request.setAttribute("toppingList", toppingList);
			request.setAttribute("subTotal", subTotal);
			request.getRequestDispatcher("/WEB-INF/jsp/itemDetailsChange.jsp").forward(request, response);

		} catch (SQLException e) {
			e.printStackTrace();
			response.sendRedirect("OrderListServlet");
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		String orderIdStr = request.getParameter("orderId");
		if (orderIdStr == null || orderIdStr.isEmpty()) {
			orderIdStr = request.getParameter("oid");
		}
		if (orderIdStr == null || orderIdStr.isEmpty()) {
			response.sendRedirect("OrderListServlet");
			return;
		}
		int orderId = Integer.parseInt(orderIdStr);
		String button = request.getParameter("Button");
		String mode = request.getParameter("mode");

		try {
			OrderListInfo ol = toppingDAO.findOrderInfo(orderId);

			if (ol == null) {
				response.sendRedirect("OrderListServlet");
				return;
			}

			// POST時もDAOから直接 productId を取得
			int productId = toppingDAO.getProductIdByOrderId(orderId);

			// その商品のトッピング一覧のみを取得
			List<ItemDetailsInfo> toppingList = toppingDAO.findToppingListByProductId(productId, orderId);

			// 1. 画面の数量を先に一括復元
			for (int i = 0; i < toppingList.size(); i++) {
				String qty = request.getParameter("oldQty_" + i);
				if (qty != null && !qty.isEmpty()) {
					toppingList.get(i).setToppingQuantity(Integer.parseInt(qty));
				}
			}

			// 2. ＋ / －ボタンが押された場合の処理
			if (button != null && (button.startsWith("+") || button.startsWith("-"))) {
				int index = Integer.parseInt(button.substring(1));
				String action = button.startsWith("+") ? "plus" : "minus";

				itemDetailsChangeLogic.calcToppingQuantity(toppingList, index, action);
				int subTotal = itemDetailsChangeLogic.calcSubTotal(ol.getProductPrice(), toppingList);

				request.setAttribute("ol", ol);
				request.setAttribute("toppingList", toppingList);
				request.setAttribute("subTotal", subTotal);
				request.getRequestDispatcher("/WEB-INF/jsp/itemDetailsChange.jsp").forward(request, response);
				return;
			}

			// 3. 変更ボタンでDBを更新
			if ("update".equals(mode)) {
				int subTotal = itemDetailsChangeLogic.calcSubTotal(ol.getProductPrice(), toppingList);
				List<ItemDetailsInfo> dbList = toppingDAO.findToppingListByProductId(productId, orderId);

				for (int i = 0; i < toppingList.size(); i++) {
					ItemDetailsInfo screen = toppingList.get(i);
					ItemDetailsInfo db = dbList.get(i);
					int screenQty = screen.getToppingQuantity();
					int dbQty = db.getToppingQuantity();

					if (dbQty == 0 && screenQty > 0) {
						toppingDAO.insertTopping(orderId, screen.getToppingId(), screenQty);
						toppingDAO.updateToppingStock(screen.getToppingId(), screenQty);
					} else if (dbQty > 0 && screenQty == 0) {
						toppingDAO.deleteTopping(orderId, screen.getToppingId());
						toppingDAO.updateToppingStock(screen.getToppingId(), -dbQty);
					} else if (dbQty > 0 && screenQty > 0 && dbQty != screenQty) {
						toppingDAO.updateToppingQuantity(orderId, screen.getToppingId(), screenQty);
						int diff = screenQty - dbQty;
						toppingDAO.updateToppingStock(screen.getToppingId(), diff);
					}
				}
				toppingDAO.updateOrderPrice(orderId, subTotal);

				response.sendRedirect("OrderListServlet");
				return;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		response.sendRedirect("OrderListServlet");
	}
}