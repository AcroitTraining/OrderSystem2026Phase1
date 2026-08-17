package servlet;

import java.io.IOException;
import java.sql.Connection;

import javax.sql.DataSource;

import dao.DBConnection;
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

        // DB接続テストを実行（共通の DataSource を使用）
        try {
            DataSource dataSource = DBConnection.getDataSource();
            try (Connection conn = dataSource.getConnection()) {
                chain.doFilter(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = e.getMessage() != null ? e.getMessage() : e.toString();
            httpRequest.getSession().setAttribute("errorMessage", errorMsg);
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/error.jsp");
        }
    }
}