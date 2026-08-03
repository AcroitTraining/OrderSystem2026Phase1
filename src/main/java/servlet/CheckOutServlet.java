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

    /**
     * 他のユーザーが会計を完了して Filter からリダイレクトされてきた場合の処理（GET）
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Filterからのリダイレクトパラメータを取得
        String fromFilter = request.getParameter("fromFilter");

        if ("true".equals(fromFilter)) {
            // ★ Filter経由（他の人が会計した）であることを示すフラグをスコープにセット
            request.setAttribute("isOtherUserCheckout", true);

            // セッションの破棄（JSP表示前に安全にクリア）
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }

            // checkOut.jsp へフォワード（金額情報などはセットしない）
            request.getRequestDispatcher("/WEB-INF/jsp/checkOut.jsp").forward(request, response);
            return;
        }

        // Filterからではなく、直接GETアクセス（URL直接叩きなど）された場合の安全策
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("tableInfo") == null) {
            // セッションや卓情報がない場合はトップまたはエラー画面へリダイレクト
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        // セッションが正しく存在する場合はトップへ戻す（直叩き防止）
        response.sendRedirect(request.getContextPath() + "/index.jsp");
    }

    /**
     * 自分が「会計ボタン」を押したときの処理（POST）
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        // パラメータ取得
        String tableNumber = request.getParameter("tableNumber");
        String totalPriceStr = request.getParameter("totalOrderPrice");
        int totalOrderPrice = 0;
        
        if (totalPriceStr != null && !totalPriceStr.isEmpty()) {
            try {
                totalOrderPrice = Integer.parseInt(totalPriceStr);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        // データベースの会計処理実行
        CheckOutDAO dao = new CheckOutDAO();
        try {
            dao.executeCheckout(tableNumber);
        } catch (SQLException e) {
            e.printStackTrace();
            // 必要に応じてエラー時の処理を記述
        }
        
        // JSPへの出力データのセット
        CheckOutInfo info = new CheckOutInfo(tableNumber, totalOrderPrice);
        request.setAttribute("checkOutInfo", info);
        
        // 自分が会計したことを示すフラグ
        request.setAttribute("isOtherUserCheckout", false);

        // セッションを破棄（フォワード先のJSPへは request スコープが維持されるため問題ありません）
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); 
        }

        // JSPへフォワード
        request.getRequestDispatcher("/WEB-INF/jsp/checkOut.jsp").forward(request, response);
    }
}