package servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebFilter("/*")
public class DbCheckFilter implements Filter {
    
    private final String JDBC_URL = "jdbc:mysql://localhost:3306/order_management";
    private final String DB_USER = "order";
    private final String DB_PASS = "1234";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestURI = httpRequest.getRequestURI();
        
        // エラー画面、インデックス画面、注文開始画面へのアクセス時はチェックをスキップする
        if (requestURI.endsWith("error.jsp") || 
        	requestURI.endsWith("index.jsp") || 
        	requestURI.endsWith("orderStart.jsp") || 
        	requestURI.endsWith("OrderStartServlet.jsp") ||
        	requestURI.endsWith("/")) { // 初期の実行でのチェック防止
            
            chain.doFilter(request, response);
            return;
        }

        // DB接続テストを実行
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS)) {
                chain.doFilter(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace(); 
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/error.jsp");
        }
    }
}
