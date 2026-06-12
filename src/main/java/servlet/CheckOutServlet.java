package servlet;

import java.io.IOException;
import java.sql.SQLException;

import dao.CheckOutDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.CheckOutInfo;

@WebServlet("/CheckOutServlet")
public class CheckOutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, 
    		HttpServletResponse response) 
    				throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        
        // 入力データの取得
        String tableNumber = request.getParameter("tableNumber");
        String totalPriceStr = request.getParameter("totalOrderPrice");
        int totalOrderPrice = (totalPriceStr != null) ? Integer.parseInt(totalPriceStr) : 0;

        // データベースの更新実行
        CheckOutDAO dao = new CheckOutDAO();
        try {
            dao.executeCheckout(tableNumber);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // jspへの出力データのセット
        CheckOutInfo info = new CheckOutInfo(tableNumber, totalOrderPrice);
        request.setAttribute("checkOutInfo", info);
        
        // jspへフォワード
        request.getRequestDispatcher("/WEB-INF/jsp/checkOut.jsp").forward(request, response);
        
        if (session != null) {
            session.invalidate(); // セッションを破棄
        }
    }

    protected void doGet(HttpServletRequest request, 
    		HttpServletResponse response) 
    				throws ServletException, IOException {
        doPost(request, response);
    }
}