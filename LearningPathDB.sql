CREATE DATABASE LearningPathDB;
GO
USE LearningPathDB;
GO

CREATE TABLE Roles (
    role_id INT IDENTITY(1,1) PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE Users (
    user_id INT IDENTITY(1,1) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role_id INT NOT NULL,
    full_name NVARCHAR(100),
    phone VARCHAR(20),
    bio NVARCHAR(MAX),
    avatar VARCHAR(255) DEFAULT 'default-avatar.png',
    github_link VARCHAR(255),
    facebook_link VARCHAR(255),
    linkedin_link VARCHAR(255),
    website_link VARCHAR(255),
    address NVARCHAR(255),
    dob DATE,
    created_at DATETIME DEFAULT GETDATE(),
    status BIT DEFAULT 1, -- 1: Active, 0: Banned
    FOREIGN KEY (role_id) REFERENCES Roles(role_id)
);

CREATE TABLE PasswordResetTokens (
    token_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    otp_code VARCHAR(6) NOT NULL,
    expiry_time DATETIME NOT NULL,
    is_used BIT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

-- Insert Roles & Admin Account (Password: admin123)
INSERT INTO Roles (role_name) VALUES ('Admin'), ('User');
INSERT INTO Users (username, email, password_hash, role_id, full_name) 
VALUES ('admin', 'admin@learningpath.com', '$2a$10$wN9Q7b7T5O5V./wA5M9o.O8uD2bXn./oV3t7L.Z5b5kP.o5q5o5m', 1, 'System Administrator');
GO