<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reset Password - Learning Path</title>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
    <link rel="stylesheet" href="assets/css/style.css">
</head>
<body>

    <jsp:include page="header.jsp" />

    <div class="main-container">
        
        <div class="left-content fade-in-left">
            <h1 class="hero-title">
                Secure your account<br>
                and get back to<br>
                <span class="highlight">learning</span>
            </h1>
            <ul class="feature-list">
                <li class="feature-item">+ Quick account recovery</li>
                <li class="feature-item">+ Secure data encryption</li>
                <li class="feature-item">+ Continue your progress</li>
            </ul>
        </div>

        <div class="right-content fade-in-up">
            <div class="panel-backdrop"></div>
            
            <div class="login-panel">
                <h2 class="panel-title">Set New Password</h2>
                
                <c:if test="${not empty error}">
                    <p class="error"><i class="fas fa-exclamation-circle"></i> &nbsp; ${error}</p>
                </c:if>

                <form action="reset-password" method="POST">
                    
                    <div class="form-group">
                        <input type="password" name="newPassword" class="form-input" placeholder="New Password *" required>
                    </div>
                    
                    <div class="form-group">
                        <input type="password" name="confirmPassword" class="form-input" placeholder="Confirm New Password *" required>
                    </div>
                    
                    <button type="submit" class="btn-login">Update Password</button>
                </form>

                <div class="separator">Or</div>
                
                <p class="register-row">
                    Remember your password? <a href="login.jsp" class="link-register">Back to Login</a>
                </p>
            </div>
        </div>

    </div>

</body>
</html>