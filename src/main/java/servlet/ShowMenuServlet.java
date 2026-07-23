package servlet;

import java.io.IOException;
import java.util.List;

import dao.ShowMenuDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.ProductInfo;

@WebServlet("/ShowMenuServlet")
public class ShowMenuServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);

		if (session == null || (session.getAttribute("tableNumber") == null && request.getParameter("tableId") == null)) {
			response.sendRedirect("error.jsp");
			return;
		}

		String tableId = request.getParameter("tableId");
		if (tableId != null && !tableId.isEmpty()) {
			session.setAttribute("tableNumber", tableId);
		}

		String sessionId = (String) session.getAttribute("tableNumber");
		ShowMenuDAO dao = new ShowMenuDAO();
		
		int items = 0;
		if (sessionId != null) {
			items = dao.getOrderItemCount(sessionId);
		}
		session.setAttribute("items", items);
		
		// カテゴリ一覧を取得
		List<ProductInfo> categoryList = dao.findAllCategories();
		request.setAttribute("categoryList", categoryList);

		// 商品一覧を取得
		List<ProductInfo> productList = dao.findProductTable();
		session.setAttribute("productList", productList);

		// 選択された categoryId の取得
		int currentCategoryId = -1;
		String categoryIdParam = request.getParameter("categoryId");

		if (categoryIdParam != null && !categoryIdParam.isEmpty()) {
			try {
				currentCategoryId = Integer.parseInt(categoryIdParam);
			} catch (NumberFormatException e) {
				currentCategoryId = -1;
			}
		}

		// リクエストになければ先頭のカテゴリIDをデフォルトに設定
		if (currentCategoryId == -1 && categoryList != null && !categoryList.isEmpty()) {
			currentCategoryId = categoryList.get(0).getCategoryId();
		}

		request.setAttribute("currentCategoryId", currentCategoryId);
		RequestDispatcher rd = request.getRequestDispatcher("WEB-INF/jsp/showMenu.jsp");
		rd.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}