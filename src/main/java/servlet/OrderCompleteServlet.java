package servlet;

import java.io.IOException;
import java.sql.SQLException;

import dao.OrderCompleteDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/OrderCompleteServlet")
public class OrderCompleteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession(false);
		// セッションがない、または卓番号などの必須データが消えている場合
		if (session == null || session.getAttribute("tableNumber") == null) {
			// 即座にエラー画面へ転送する
			response.sendRedirect("error.jsp");
			return;
		}
		
		//注文フラグをtrueに
		OrderCompleteDAO ocDAO = new OrderCompleteDAO();
		try {
			ocDAO.updateOrderDetails();
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendRedirect("error.jsp"); 
		}
		
		
		RequestDispatcher rd = request.getRequestDispatcher("WEB-INF/jsp/orderComplete.jsp");
		rd.forward(request, response);
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

}
