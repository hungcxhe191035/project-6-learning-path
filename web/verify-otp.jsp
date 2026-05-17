<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Verify OTP - Learning Path</title>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
    <link rel="stylesheet" href="assets/css/style.css">
</head>
<body>

    <jsp:include page="header.jsp" />

    <div class="main-container" style="justify-content: center;">
        
        <div class="right-content fade-in-up">
            <div class="panel-backdrop"></div>
            
            <div class="login-panel">
                <h2 class="panel-title">Enter OTP</h2>
                
                <p style="text-align: center; color: #666; font-size: 14px; margin-bottom: 25px; line-height: 1.6;">
                    We've sent a verification code to <br>
                    <strong>${sessionScope.reset_email}</strong>.<br>
                    <span style="color: #f05123; font-size: 13px;">It expires in 5 minutes.</span>
                </p>

                <c:if test="${not empty error}">
                    <p class="error"><i class="fas fa-exclamation-circle"></i> &nbsp; ${error}</p>
                </c:if>

                <form action="verify-otp" method="POST">
                    <div class="form-group">
                        <input type="text" name="otp" class="form-input" maxlength="6" placeholder="------" 
                               style="text-align: center; letter-spacing: 12px; font-size: 20px; font-weight: 700;" required>
                    </div>
                    
                    <button type="submit" class="btn-login">Verify Code</button>
                </form>

                <div class="separator">Or</div>

                <p class="register-row">
                    Didn't receive the code? <a href="forgot-password.jsp" class="link-register">Try again</a>
                </p>
                <p class="register-row" style="margin-top: 5px;">
                    <a href="login.jsp" class="link-forgot">Back to Login</a>
                </p>
            </div>
        </div>

    </div>

</body>
</html>