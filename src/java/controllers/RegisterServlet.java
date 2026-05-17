package controllers;
import dao.UserDAO;
import models.User;
import utils.BCryptUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        
        UserDAO dao = new UserDAO();
        if (dao.checkExists(username, email)) {
            req.setAttribute("error", "Username or Email already exists!");
            req.getRequestDispatcher("register.jsp").forward(req, resp);
            return;
        }

        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setPasswordHash(BCryptUtil.hashPassword(password));
        
        if (dao.register(u)) {
            resp.sendRedirect("login?msg=success");
        } else {
            req.setAttribute("error", "Registration failed.");
            req.getRequestDispatcher("register.jsp").forward(req, resp);
        }
    }
}