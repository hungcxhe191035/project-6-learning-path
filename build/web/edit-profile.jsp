<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Profile - Learning Path</title>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css?v=3">
</head>
<body>

    <jsp:include page="header.jsp" />

    <div class="main-container" style="justify-content: center; padding-top: 40px; padding-bottom: 40px;">
        
        <div class="login-panel fade-in-up" style="max-width: 800px; width: 100%;">
            <h2 class="panel-title">Edit Your Profile</h2>
            
            <form action="edit-profile" method="POST" enctype="multipart/form-data">
                
                <div class="avatar-upload-container">
                    <img src="${not empty LOGIN_USER.avatar ? 'uploads/avatar/'.concat(LOGIN_USER.avatar) : 'assets/images/default-avatar.png'}" 
                         style="width: 120px; height: 120px; border-radius: 50%; object-fit: cover; border: 3px solid #e2e8f0;">
                    <br>
                    <div class="file-input-wrapper">
                        <span class="btn-upload"><i class="fas fa-camera"></i> Choose New Avatar</span>
                        <input type="file" name="avatar" accept="image/png, image/jpeg">
                    </div>
                </div>

                <h3 class="profile-section-title" style="margin-bottom: 15px; font-size: 16px;">Personal Information</h3>

                <div class="form-row">
                    <div class="form-col">
                        <label>Full Name</label>
                        <input type="text" name="fullName" class="form-input" value="${LOGIN_USER.fullName}" placeholder="e.g. John Doe">
                    </div>
                    <div class="form-col">
                        <label>Date of Birth</label>
                        <input type="date" name="dob" class="form-input" value="${LOGIN_USER.dob}">
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-col">
                        <label>Email Address</label>
                        <input type="email" name="email" class="form-input" value="${LOGIN_USER.email}" required>
                    </div>
                    <div class="form-col">
                        <label>Phone Number</label>
                        <input type="text" name="phone" class="form-input" value="${LOGIN_USER.phone}">
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-col">
                        <label>Location / Address</label>
                        <input type="text" name="address" class="form-input" value="${LOGIN_USER.address}">
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-col">
                        <label>Bio / Description</label>
                        <textarea name="bio" class="form-input">${LOGIN_USER.bio}</textarea>
                    </div>
                </div>

                <h3 class="profile-section-title" style="margin-bottom: 15px; margin-top: 30px; font-size: 16px;">Social Links</h3>

                <div class="form-row">
                    <div class="form-col">
                        <label><i class="fab fa-github" style="color:#333;"></i> GitHub</label>
                        <input type="url" name="githubLink" class="form-input" value="${LOGIN_USER.githubLink}">
                    </div>
                    <div class="form-col">
                        <label><i class="fab fa-linkedin" style="color:#0077b5;"></i> LinkedIn</label>
                        <input type="url" name="linkedinLink" class="form-input" value="${LOGIN_USER.linkedinLink}">
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-col">
                        <label><i class="fab fa-facebook" style="color:#1877F2;"></i> Facebook</label>
                        <input type="url" name="facebookLink" class="form-input" value="${LOGIN_USER.facebookLink}">
                    </div>
                    <div class="form-col">
                        <label><i class="fas fa-globe" style="color:#0ea5e9;"></i> Personal Website</label>
                        <input type="url" name="websiteLink" class="form-input" value="${LOGIN_USER.websiteLink}">
                    </div>
                </div>

                <hr style="border: 0; border-top: 1px solid #e2e8f0; margin: 30px 0;">

                <div style="display: flex; gap: 15px; justify-content: flex-end;">
                    <a href="profile.jsp" class="btn-login" style="background: #94a3b8; text-align: center; text-decoration: none; width: 150px; margin-top: 0;">Cancel</a>
                    <button type="submit" class="btn-login" style="width: 200px; margin-top: 0;"><i class="fas fa-save"></i> Save Changes</button>
                </div>

            </form>
        </div>
    </div>

</body>
</html>