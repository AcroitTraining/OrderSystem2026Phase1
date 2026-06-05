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

@WebServlet("/OrderCompleteServlet")
public class OrderCompleteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//注文フラグをtrueに
		OrderCompleteDAO ocDAO = new OrderCompleteDAO();
		try {
			ocDAO.updateOrderDetails();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//わホー(セイキン）
		RequestDispatcher rd = request.getRequestDispatcher("WEB-INF/jsp/orderComplete.jsp");
		rd.forward(request, response);
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

}
