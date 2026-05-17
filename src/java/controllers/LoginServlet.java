package controllers;
import dao.UserDAO;
import models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String account = req.getParameter("account"); // username or email
        String pass = req.getParameter("password");
        String remember = req.getParameter("remember");

        User user = new UserDAO().login(account, pass);
        if (user != null) {
            HttpSession session = req.getSession();
            session.setAttribute("LOGIN_USER", user);
            
            if ("on".equals(remember)) {
                Cookie c = new Cookie("rem_user", user.getUsername());
                c.setMaxAge(60 * 60 * 24 * 7); // 7 days
                resp.addCookie(c);
            }
            
            if (user.getRoleId() == 1) resp.sendRedirect("admin.jsp");
            else resp.sendRedirect("home.jsp");
        } else {
            req.setAttribute("error", "Invalid credentials!");
            req.getRequestDispatcher("login.jsp").forward(req, resp);
        }
    }
}