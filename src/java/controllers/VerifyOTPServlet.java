package controllers;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/verify-otp")
public class VerifyOTPServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String otp = req.getParameter("otp").trim();
        HttpSession session = req.getSession();
        String email = (String) session.getAttribute("reset_email");

        if (email == null) {
            resp.sendRedirect("forgot-password.jsp");
            return;
        }

        UserDAO dao = new UserDAO();
        if (dao.validateOTP(email, otp)) {
            session.setAttribute("otp_verified", true);
            resp.sendRedirect("reset-password.jsp");
        } else {
            req.setAttribute("error", "Invalid or expired OTP code!");
            req.getRequestDispatcher("verify-otp.jsp").forward(req, resp);
        }
    }
}