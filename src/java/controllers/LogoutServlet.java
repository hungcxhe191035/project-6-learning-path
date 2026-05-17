package controllers;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Lấy phiên làm việc (Session) hiện tại. 
        // Truyền 'false' để không tạo session mới nếu nó không tồn tại.
        HttpSession session = request.getSession(false);
        
        // 2. Nếu session tồn tại (tức là người dùng đang đăng nhập), tiến hành hủy nó
        if (session != null) {
            session.invalidate(); // Xóa toàn bộ dữ liệu (bao gồm LOGIN_USER)
        }
        
        // 3. Chuyển hướng người dùng về trang home.jsp
        response.sendRedirect("home.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Gọi chung hàm doGet để xử lý cả GET và POST
        doGet(request, response);
    }
}