<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Forgot Password - Learning Path</title>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
    <link rel="stylesheet" href="assets/css/style.css">
</head>
<body>

    <jsp:include page="header.jsp" />

    <div class="main-container">
        
        <div class="left-content fade-in-left">
            <h1 class="hero-title">
                Don't worry,<br>
                we've got<br>
                <span class="highlight">your back</span>
            </h1>
            <ul class="feature-list">
                <li class="feature-item">+ Fast and secure recovery</li>
                <li class="feature-item">+ 6-digit OTP verification</li>
                <li class="feature-item">+ Get back to learning quickly</li>
            </ul>
        </div>

        <div class="right-content fade-in-up">
            <div class="panel-backdrop"></div>
            
            <div class="login-panel">
                <h2 class="panel-title">Forgot Password</h2>
                
                <p style="text-align: center; color: #666; font-size: 14px; margin-bottom: 25px; line-height: 1.5;">
                    Enter your registered email address below to receive a 6-digit One-Time Password (OTP).
                </p>

                <c:if test="${not empty error}">
                    <p class="error"><i class="fas fa-exclamation-circle"></i> &nbsp; ${error}</p>
                </c:if>

                <form action="forgot-password" method="POST">
                    <div class="form-group">
                        <input type="email" name="email" class="form-input" placeholder="Email Address *" required>
                    </div>
                    
                    <button type="submit" class="btn-login">Send OTP</button>
                </form>

                <div class="separator">Or</div>

                <p class="register-row">
                    Remembered your password? <a href="login.jsp" class="link-register">Back to Login</a>
                </p>
            </div>
        </div>

    </div>

</body>
</html>