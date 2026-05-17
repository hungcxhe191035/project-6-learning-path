package controllers;

import dao.UserDAO;
import models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        UserDAO dao = new UserDAO();
        
        User user = dao.findByEmail(email);
        
        if (user == null) {
            req.setAttribute("error", "Email does not exist in the system!");
            req.getRequestDispatcher("forgot-password.jsp").forward(req, resp);
            return;
        }

        String otp = String.format("%06d", new java.util.Random().nextInt(999999));
        dao.saveOTP(user.getUserId(), otp);
        
        utils.EmailUtil.sendOTP(email, otp);
        
        req.getSession().setAttribute("reset_email", email);
        resp.sendRedirect("verify-otp.jsp");
    }
}