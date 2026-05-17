<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Admin Dashboard</title>
    <link rel="stylesheet" href="assets/css/style.css">
</head>
<body>
    <div class="dashboard-container">
        <div class="nav-bar">
            <span>Admin Control Panel</span>
            <div>
                <span>Admin: ${LOGIN_USER.username}</span>
                <a href="logout">Logout</a>
            </div>
        </div>
        
        <h2>System Management</h2>
        <p>Welcome to the Admin Dashboard. Here you can manage users, roles, and community content.</p>
        <ul>
            <li>Manage Users</li>
            <li>Review Reported Courses</li>
            <li>System Settings</li>
        </ul>
    </div>
</body>
</html>