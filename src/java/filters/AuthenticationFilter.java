package filters;
import dao.UserDAO;
import models.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebFilter(urlPatterns = {"/profile.jsp", "/edit-profile.jsp", "/admin.jsp", "/edit-profile"})
public class AuthenticationFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);
        
        User user = (session != null) ? (User) session.getAttribute("LOGIN_USER") : null;

        // Auto login from cookie (Remember Me)
        if (user == null) {
            Cookie[] cookies = req.getCookies();
            if (cookies != null) {
                for (Cookie c : cookies) {
                    if (c.getName().equals("rem_user")) {
                        user = new UserDAO().findByUsername(c.getValue());
                        req.getSession().setAttribute("LOGIN_USER", user);
                        break;
                    }
                }
            }
        }

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
        } else {
            chain.doFilter(request, response);
        }
    }
}