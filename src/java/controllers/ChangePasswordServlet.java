package controllers;

import dao.UserDAO;
import models.User;
import utils.BCryptUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/change-password")
public class ChangePasswordServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User currentUser = (User) session.getAttribute("LOGIN_USER");

        // 1. Check if user is logged in
        if (currentUser == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        String currentPassword = req.getParameter("currentPassword");
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        // 2. Verify current password
        if (!BCryptUtil.checkPassword(currentPassword, currentUser.getPasswordHash())) {
            req.setAttribute("error", "Current password is incorrect!");
            req.getRequestDispatcher("change-password.jsp").forward(req, resp);
            return;
        }

        // 3. Validate new password match
        if (!newPassword.equals(confirmPassword)) {
            req.setAttribute("error", "New passwords do not match!");
            req.getRequestDispatcher("change-password.jsp").forward(req, resp);
            return;
        }

        // 4. Update to new password
        UserDAO dao = new UserDAO();
        String hashedNewPassword = BCryptUtil.hashPassword(newPassword);
        dao.updatePassword(currentUser.getEmail(), hashedNewPassword);
        
        // 5. Update session info (so next time they edit profile, the hash is updated in session)
        currentUser.setPasswordHash(hashedNewPassword);
        session.setAttribute("LOGIN_USER", currentUser);

        // 6. Redirect back to profile with success message
        resp.sendRedirect("profile.jsp?msg=password_changed");
    }
}