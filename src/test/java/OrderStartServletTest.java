import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.TableInfo;
import servlet.OrderStartServlet;

@ExtendWith(MockitoExtension.class)
class OrderStartServletTest2 {

	// dao：mock化しない
	// request, response, dispatcher：mock化する


	//    @Mock private OrderStartDAO dao;
	@Mock private HttpServletRequest request;
	@Mock private HttpServletResponse response;
	@Mock private RequestDispatcher dispatcher;
	@InjectMocks private OrderStartServlet servlet;

	@Test
	void testOrderStartServlet () throws ServletException, IOException, SQLException, ClassNotFoundException {

		// 期待値の作成
		int tableId = 1;
		int sessionId = 1;
		String sessionStatus = "active";
		TableInfo tableInfoTest = new TableInfo(sessionId, tableId, sessionStatus);

		// mockitoの動作定義
		when(request.getParameter("tableId")).thenReturn("1");
		when(request.getParameter("guestCount")).thenReturn("2");
		when(request.getParameter("action")).thenReturn("start");
		doNothing().when(dispatcher).forward(request, response);
		when(request.getRequestDispatcher("ShowMenuServlet")).thenReturn(dispatcher);

		// servletの実行
		servlet.doPost(request, response);

		// 実際値のゲット
		TableInfo tableInfo = servlet.getTableInfo();
		// 期待値と実際値の一致のテスト
		assertEquals(tableInfoTest.getSessionId(), tableInfo.getSessionId());
		assertEquals(tableInfoTest.getSessionStatus(), tableInfo.getSessionStatus());
		assertEquals(tableInfoTest.getTableId(), tableInfo.getTableId());

		// mockitoの実行確認
		verify(request, times(1)).setAttribute("tableInfo", tableInfo);
		verify(request, times(1)).getRequestDispatcher(anyString());
		verify(request, times(1)).getRequestDispatcher(anyString());

	}
	
	@Test
    void testplusButton() throws ServletException, IOException {

		when(request.getParameter("tableId")).thenReturn("1");
        when(request.getParameter("action")).thenReturn("plus");
        when(request.getParameter("guestCount")).thenReturn("2");
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
        doNothing().when(dispatcher).forward(request, response);
        servlet.doPost(request, response);
        verify(request).getParameter("action");
        verify(request).getRequestDispatcher(anyString());
        System.out.println(mockingDetails(request).printInvocations());
    }
}
	
