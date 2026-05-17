<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Learning Path</title>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
    <link rel="stylesheet" href="assets/css/style.css">
</head>
<body>

    <jsp:include page="header.jsp" />

    <div class="main-container">
        
        <div class="left-content fade-in-left">
            <h1 class="hero-title">
                The exclusive online<br>
                coding platform<br>
                <span class="highlight">for students</span>
            </h1>
            <ul class="feature-list">
                <li class="feature-item">+ Start from scratch</li>
                <li class="feature-item">+ Discover your passion</li>
                <li class="feature-item">+ Build a digital future</li>
                <li class="feature-item">+ Connect with opportunities</li>
            </ul>
        </div>

        <div class="right-content fade-in-up">
            <div class="panel-backdrop"></div>
            
            <div class="login-panel">
                <h2 class="panel-title">Sign in to Learning Path</h2>
                
                <% if("success".equals(request.getParameter("msg"))) { %>
                    <p class="success"><i class="fas fa-check-circle"></i> &nbsp; Registration successful! Please login.</p>
                <% } %>
                <% if("password_reset_success".equals(request.getParameter("msg"))) { %>
                    <p class="success"><i class="fas fa-check-circle"></i> &nbsp; Password reset successfully!</p>
                <% } %>
                <c:if test="${not empty error}">
                    <p class="error"><i class="fas fa-exclamation-circle"></i> &nbsp; ${error}</p>
                </c:if>

                <form action="login" method="POST">
                    <div class="form-group">
                        <input type="text" name="account" class="form-input" placeholder="Username or Email" required>
                    </div>
                    <div class="form-group">
                        <input type="password" name="password" class="form-input" placeholder="Password" required>
                    </div>

                    <div class="remember-me-row">
                        <label>
                            <input type="checkbox" name="remember" class="remember-me-checkbox"> Remember me
                        </label>
                        <a href="forgot-password.jsp" class="link-forgot">Forgot Password?</a>
                    </div>

                    <button type="submit" class="btn-login">Login</button>
                </form>

                <p class="register-row">
                    Don't have an account? <a href="register.jsp" class="link-register">Create an Account</a>
                </p>
            </div>
        </div>

    </div>

</body>
</html>