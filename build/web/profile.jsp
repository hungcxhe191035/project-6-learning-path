<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>User Profile - Learning Path</title>
        <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css?v=3">
    </head>
    <body>

        <jsp:include page="header.jsp" />

        <div class="main-container" style="justify-content: center; align-items: flex-start; padding-top: 40px;">

            <div class="profile-wrapper fade-in-up">

                <div class="profile-sidebar">
                    <img src="${not empty LOGIN_USER.avatar ? 'uploads/avatar/'.concat(LOGIN_USER.avatar) : 'assets/images/default-avatar.png'}" 
                         class="profile-avatar-lg" alt="Avatar">

                    <h2 class="profile-name">${not empty LOGIN_USER.fullName ? LOGIN_USER.fullName : LOGIN_USER.username}</h2>
                    <p class="profile-username">@${LOGIN_USER.username}</p>

                    <p class="profile-bio">
                        <i class="fas fa-quote-left" style="color:#ccc; margin-right:5px;"></i>
                        ${not empty LOGIN_USER.bio ? LOGIN_USER.bio : "No bio provided yet."}
                        <i class="fas fa-quote-right" style="color:#ccc; margin-left:5px;"></i>
                    </p>

                    <div class="profile-socials">
                        <c:if test="${not empty LOGIN_USER.githubLink}">
                            <a href="${LOGIN_USER.githubLink}" target="_blank" class="social-btn" title="GitHub"><i class="fab fa-github"></i></a>
                            </c:if>
                            <c:if test="${not empty LOGIN_USER.linkedinLink}">
                            <a href="${LOGIN_USER.linkedinLink}" target="_blank" class="social-btn" style="color:#0077b5;" title="LinkedIn"><i class="fab fa-linkedin-in"></i></a>
                            </c:if>
                            <c:if test="${not empty LOGIN_USER.facebookLink}">
                            <a href="${LOGIN_USER.facebookLink}" target="_blank" class="social-btn" style="color:#1877F2;" title="Facebook"><i class="fab fa-facebook-f"></i></a>
                            </c:if>
                            <c:if test="${not empty LOGIN_USER.websiteLink}">
                            <a href="${LOGIN_USER.websiteLink}" target="_blank" class="social-btn" style="color:#0ea5e9;" title="Website"><i class="fas fa-globe"></i></a>
                            </c:if>
                    </div>
                </div>

                <div class="profile-main">
                    <c:if test="${param.msg == 'updated'}">
                        <div class="success"><i class="fas fa-check-circle"></i> Profile updated successfully!</div>
                    </c:if>

                    <c:if test="${param.msg == 'password_changed'}">
                        <div class="success">
                            <i class="fas fa-check-circle"></i> &nbsp; Password changed successfully!
                        </div>
                    </c:if>

                    <h3 class="profile-section-title">Personal Information</h3>

                    <div class="info-grid">
                        <div class="info-item">
                            <label>Email Address</label>
                            <p><i class="fas fa-envelope"></i> ${not empty LOGIN_USER.email ? LOGIN_USER.email : "N/A"}</p>
                        </div>
                        <div class="info-item">
                            <label>Date of Birth</label>
                            <p><i class="fas fa-birthday-cake"></i> ${not empty LOGIN_USER.dob ? LOGIN_USER.dob : "Not provided"}</p>
                        </div>
                        <div class="info-item">
                            <label>Phone Number</label>
                            <p><i class="fas fa-phone-alt"></i> ${not empty LOGIN_USER.phone ? LOGIN_USER.phone : "Not provided"}</p>
                        </div>
                        <div class="info-item">
                            <label>Location / Address</label>
                            <p><i class="fas fa-map-marker-alt"></i> ${not empty LOGIN_USER.address ? LOGIN_USER.address : "Not provided"}</p>
                        </div>
                    </div>

                    <div class="profile-actions">
                        <a href="edit-profile.jsp" class="btn-outline"><i class="fas fa-user-edit"></i> Edit Profile</a>
                        <a href="change-password.jsp" class="btn-outline btn-danger-outline"><i class="fas fa-key"></i> Change Password</a>
                    </div>
                </div>

            </div>
        </div>

    </body>
</html>