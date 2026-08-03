package servlet;

import java.io.IOException;

import dao.TableDAO;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.TableInfo;

@WebFilter("/*")
public class MultiScreenStatusFilter implements Filter {

    private TableDAO tableDAO = new TableDAO(); 

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();

        // 除外判定（サーブレットへのパス /CheckOutServlet も除外に加えることで無限ループを防ぎます）
        boolean isIndexPage = requestURI.equals(contextPath + "/") || requestURI.equals(contextPath + "/index.jsp");
        boolean isCheckOutPage = requestURI.equals(contextPath + "/checkOut.jsp");
        boolean isCheckOutServlet = requestURI.equals(contextPath + "/CheckOutServlet");
        boolean isStaticResource = requestURI.contains("/css/") || requestURI.contains("/js/") || requestURI.contains("/images/");

        if (isIndexPage || isCheckOutPage || isCheckOutServlet || isStaticResource) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);

        // セッションと tableInfo の安全な null チェック
        if (session != null) {
            TableInfo tableInfo = (TableInfo) session.getAttribute("tableInfo");
            
            if (tableInfo != null) {
                try {
                    int sessionId = tableInfo.getSessionId();
                    String status = tableDAO.getStatus(sessionId);

                    if ("closed".equalsIgnoreCase(status)) {
                        // ★URLパラメータとして fromFilter=true を付与してリダイレクト（.javaは削除）
                        httpResponse.sendRedirect(contextPath + "/CheckOutServlet?fromFilter=true");
                        return; // ここで処理を完全に遮断
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}