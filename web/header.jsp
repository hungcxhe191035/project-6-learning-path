<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<nav class="navbar">
    <div class="navbar-brand">Learning Path</div>
    
    <ul class="navbar-nav">
        <li><a href="home.jsp" class="nav-link">Home</a></li>
        <li><a href="#" class="nav-link">Learning</a></li>
        <li><a href="#" class="nav-link">Practice</a></li>
        <li><a href="#" class="nav-link">Compete</a></li>
    </ul>

    <div class="navbar-actions">
        <i class="fas fa-search nav-icon" title="Search"></i>
        
        <c:choose>
            <c:when test="${not empty sessionScope.LOGIN_USER}">
                <div class="user-dropdown">
                    <div class="user-avatar" data-tooltip="${sessionScope.LOGIN_USER.email != null ? sessionScope.LOGIN_USER.email : sessionScope.LOGIN_USER.username}">
                        <i class="fas fa-user-circle avatar-icon-light"></i>
                    </div>
                    
                    <div class="dropdown-content-menu">
                        <a href="profile.jsp">My Profile</a>
                        <a href="">My Classes</a>
                        <a href="logout" class="logout-item">Logout</a>
                    </div>
                </div>
            </c:when>
            
            <c:otherwise>
                <a href="register.jsp" class="btn-navbar-register">Register</a>
                <a href="login.jsp" class="btn-navbar-login">Login</a>
            </c:otherwise>
        </c:choose>
    </div>
</nav>