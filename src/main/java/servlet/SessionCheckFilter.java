package servlet;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter("/*")
public class SessionCheckFilter implements Filter {

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


		HttpSession session = httpRequest.getSession(false);

		if (session == null) {
			// セッションがない（または切れている）場合はエラー画面へ強制遷移
			httpResponse.sendRedirect(httpRequest.getContextPath() + "/error.jsp");
		} else {
			// セッションがあれば正常に次の処理（サーブレットやJSP）へ進む
			chain.doFilter(request, response);
		}
	}
}
