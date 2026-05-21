<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page isELIgnored="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Change Password - Learning Path</title>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css?v=3">
</head>
<body>

    <jsp:include page="header.jsp" />

    <div class="main-container" style="justify-content: center; align-items: center; padding: 60px 0;">
        
        <div class="login-panel fade-in-up" style="max-width: 500px; width: 100%;">
            <h2 class="panel-title">Change Password</h2>
            
            <p style="text-align: center; color: #666; font-size: 14px; margin-bottom: 25px;">
                Secure your account by updating your password.
            </p>
            
            <c:if test="${not empty error}">
                <p class="error"><i class="fas fa-exclamation-circle"></i> &nbsp; ${error}</p>
            </c:if>
            
            <form action="change-password" method="POST">
                
                <div class="form-group">
                    <label style="display: block; font-size: 13px; font-weight: 600; color: #475569; margin-bottom: 8px;">Current Password</label>
                    <input type="password" name="currentPassword" class="form-input" placeholder="Enter your current password" required>
                </div>
                
                <div class="form-group">
                    <label style="display: block; font-size: 13px; font-weight: 600; color: #475569; margin-bottom: 8px;">New Password</label>
                    <input type="password" name="newPassword" class="form-input" placeholder="Min. 6 characters" required minlength="6">
                </div>
                
                <div class="form-group">
                    <label style="display: block; font-size: 13px; font-weight: 600; color: #475569; margin-bottom: 8px;">Confirm New Password</label>
                    <input type="password" name="confirmPassword" class="form-input" placeholder="Retype new password" required minlength="6">
                </div>
                
                <hr style="border: 0; border-top: 1px solid #e2e8f0; margin: 25px 0 20px 0;">

                <div style="display: flex; gap: 15px;">
                    <a href="profile.jsp" class="btn-login" style="background: #94a3b8; text-align: center; text-decoration: none; flex: 1; margin-top: 0; line-height: 22px;">Cancel</a>
                    <button type="submit" class="btn-login" style="flex: 1.5; margin-top: 0;"><i class="fas fa-lock"></i> Update Password</button>
                </div>

            </form>
        </div>
    </div>

</body>
</html>