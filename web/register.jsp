<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register - Learning Path</title>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
    <link rel="stylesheet" href="assets/css/style.css">
    <script src="assets/js/validation.js" defer></script>
</head>
<body>

    <jsp:include page="header.jsp" />

    <div class="main-container">
        
        <div class="left-content fade-in-left">
            <h1 class="hero-title">
                Join the fastest growing<br>
                coding community<br>
                <span class="highlight">for students</span>
            </h1>
            <ul class="feature-list">
                <li class="feature-item">+ Start your journey today</li>
                <li class="feature-item">+ Track your learning progress</li>
                <li class="feature-item">+ Compete with peers</li>
                <li class="feature-item">+ Earn valuable certificates</li>
            </ul>
        </div>

        <div class="right-content fade-in-up">
            <div class="panel-backdrop"></div>
            
            <div class="login-panel">
                <h2 class="panel-title">Create an Account</h2>
                
                <c:if test="${not empty error}">
                    <p class="error"><i class="fas fa-exclamation-circle"></i> &nbsp; ${error}</p>
                </c:if>

                <form action="register" method="POST">
                    <div class="form-group">
                        <input type="text" name="username" class="form-input" placeholder="Username *" required>
                    </div>
                    
                    <div class="form-group">
                        <input type="email" name="email" class="form-input" placeholder="Email Address *" required>
                    </div>
                    
                    <div class="form-group">
                        <input type="password" name="password" class="form-input" placeholder="Password *" required>
                    </div>
                    
                    <div class="form-group">
                        <input type="password" name="confirm" class="form-input" placeholder="Confirm Password *" required>
                    </div>

                    <button type="submit" class="btn-login">Sign Up</button>
                </form>

                <p class="register-row">
                    Already have an account? <a href="login.jsp" class="link-register">Login here</a>
                </p>
            </div>
        </div>

    </div>

</body>
</html>