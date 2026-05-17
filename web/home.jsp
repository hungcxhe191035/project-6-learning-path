<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Home - Learning Path</title>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css?v=2">
    
    <style>
        .dashboard-content {
            text-align: center;
            color: #fff;
            max-width: 800px;
            margin: 0 auto;
        }
        .dashboard-content h2 {
            font-size: 46px;
            font-weight: 700;
            margin-bottom: 25px;
            color: #fff;
            text-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
        }
        .dashboard-content p {
            font-size: 18px;
            color: #cbd5e1;
            line-height: 1.6;
        }
    </style>
</head>
<body>

    <jsp:include page="header.jsp" />

    <div class="main-container" style="justify-content: center; align-items: center; min-height: calc(100vh - 70px);">
        
        <div class="dashboard-content fade-in-up">
            <h2>Welcome to the Community!</h2>
            <p>
                This is the main dashboard where you can explore learning paths, 
                share courses, and connect with others. <br>
                <span style="color: #00c6c6; font-weight: 500;">(Future features will be implemented here).</span>
            </p>
        </div>

    </div>

</body>
</html>