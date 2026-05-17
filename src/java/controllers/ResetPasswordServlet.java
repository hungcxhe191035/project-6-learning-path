package controllers;

import dao.UserDAO;
import utils.BCryptUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/reset-password")
public class ResetPasswordServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");
        
        HttpSession session = req.getSession();
        String email = (String) session.getAttribute("reset_email");
        Boolean isVerified = (Boolean) session.getAttribute("otp_verified");

        if (email == null || isVerified == null || !isVerified) {
            resp.sendRedirect("forgot-password.jsp");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            req.setAttribute("error", "Password confirmation does not match!");
            req.getRequestDispatcher("reset-password.jsp").forward(req, resp);
            return;
        }

        UserDAO dao = new UserDAO();
        String hashedPassword = BCryptUtil.hashPassword(newPassword);
        dao.updatePassword(email, hashedPassword);

        session.removeAttribute("reset_email");
        session.removeAttribute("otp_verified");

        resp.sendRedirect("login.jsp?msg=password_reset_success");
    }
}