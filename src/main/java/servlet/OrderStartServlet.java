package servlet;

import java.io.IOException;

import dao.OrderStartDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.OrderStartLogic;
import model.TableInfo;

@WebServlet("/OrderStartServlet")
public class OrderStartServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
	private TableInfo tableInfo;
	public TableInfo getTableInfo() { return tableInfo; }
	
    public void doPost(HttpServletRequest request, 
    		HttpServletResponse response) 
    				throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        // パラメータ取得
        String tableIdStr = request.getParameter("tableId");
        String guestCountStr = request.getParameter("guestCount");
        String action = request.getParameter("action"); // ボタンの識別用
        int tableId = Integer.parseInt(tableIdStr);
        System.out.println(guestCountStr);
        int guestCount = (guestCountStr == null) ? 1 : Integer.parseInt(guestCountStr);

        OrderStartLogic logic = new OrderStartLogic();
        OrderStartDAO dao = new OrderStartDAO();

        if ("start".equals(action)) {
            // DB更新処理
            dao.updateStatus(tableId, guestCount);
            int sessionId = dao.findSessionId(tableId);	
            // 遷移先へ渡すデータ
            tableInfo = new TableInfo(tableId, sessionId, "active");
            request.setAttribute("tableInfo", tableInfo);

            // 次の画面（メニュー画面）へ遷移
            RequestDispatcher dispatcher = request.getRequestDispatcher("ShowMenuServlet");
            dispatcher.forward(request, response);

        } else {
            // プラス・マイナスボタンが押された場合
            if (action != null) {
                guestCount = logic.updateGuestCount(guestCount, action);
            }
            
            // jspへ値を戻す
            request.setAttribute("tableNumber", tableId);
            request.setAttribute("guestCount", guestCount);
            
            RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/jsp/orderStart.jsp");
            dispatcher.forward(request, response);
        }
    }
}