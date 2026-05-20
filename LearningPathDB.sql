CREATE DATABASE LearningPathDB;
GO
USE LearningPathDB;
GO

-- =============================================
-- 1. TÀI KHOẢN & QUYỀN TRUY CẬP (CORE & AUTH)
-- =============================================

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

-- =============================================
-- 2. QUẢN TRỊ LỘ TRÌNH (PATH BUILDER)
-- =============================================

CREATE TABLE Paths (
    path_id INT IDENTITY(1,1) PRIMARY KEY,
    creator_id INT NOT NULL,
    title NVARCHAR(255) NOT NULL,
    description NVARCHAR(MAX),
    cover_image_url VARCHAR(500),
    category NVARCHAR(100),
    status VARCHAR(50) DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'PUBLISHED', 'ARCHIVED')),
    avg_rating DECIMAL(3,2) DEFAULT 0,
    review_count INT DEFAULT 0,
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (creator_id) REFERENCES Users(user_id)
);

CREATE TABLE PathReviews (
    review_id INT IDENTITY(1,1) PRIMARY KEY,
    path_id INT NOT NULL,
    user_id INT NOT NULL,
    rating_stars INT CHECK (rating_stars BETWEEN 1 AND 5),
    comment_text NVARCHAR(MAX),
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (path_id) REFERENCES Paths(path_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

CREATE TABLE Milestones (
    milestone_id INT IDENTITY(1,1) PRIMARY KEY,
    path_id INT NOT NULL,
    title NVARCHAR(255) NOT NULL,
    order_index INT NOT NULL,
    deadline_days INT,
    passing_score INT,
    FOREIGN KEY (path_id) REFERENCES Paths(path_id) ON DELETE CASCADE
);

CREATE TABLE Nodes (
    node_id INT IDENTITY(1,1) PRIMARY KEY,
    milestone_id INT NOT NULL,
    title NVARCHAR(255) NOT NULL,
    node_type VARCHAR(50) CHECK (node_type IN ('THEORY', 'QUIZ_CHOICE', 'QUIZ_FILL', 'QUIZ_ESSAY')),
    content NVARCHAR(MAX),
    video_url VARCHAR(500),
    order_index INT NOT NULL,
    max_score INT DEFAULT 0,
    max_attempts INT DEFAULT 1,
    FOREIGN KEY (milestone_id) REFERENCES Milestones(milestone_id) ON DELETE CASCADE
);

-- =============================================
-- 3. HỆ THỐNG CÂU HỎI & QUIZ
-- =============================================

CREATE TABLE Questions (
    question_id INT IDENTITY(1,1) PRIMARY KEY,
    node_id INT NOT NULL,
    text NVARCHAR(MAX) NOT NULL,
    question_type VARCHAR(50),
    points INT DEFAULT 1,
    FOREIGN KEY (node_id) REFERENCES Nodes(node_id) ON DELETE CASCADE
);

CREATE TABLE QuestionOptions (
    option_id INT IDENTITY(1,1) PRIMARY KEY,
    question_id INT NOT NULL,
    label VARCHAR(10),
    text NVARCHAR(MAX) NOT NULL,
    is_correct BIT DEFAULT 0,
    FOREIGN KEY (question_id) REFERENCES Questions(question_id) ON DELETE CASCADE
);

CREATE TABLE FillBlankData (
    fill_id INT IDENTITY(1,1) PRIMARY KEY,
    question_id INT NOT NULL,
    full_text NVARCHAR(MAX) NOT NULL,
    blank_index INT,
    correct_keyword NVARCHAR(255) NOT NULL,
    FOREIGN KEY (question_id) REFERENCES Questions(question_id) ON DELETE CASCADE
);

CREATE TABLE EssayRubrics (
    rubric_id INT IDENTITY(1,1) PRIMARY KEY,
    question_id INT NOT NULL,
    criteria_name NVARCHAR(255) NOT NULL,
    max_points INT NOT NULL,
    FOREIGN KEY (question_id) REFERENCES Questions(question_id) ON DELETE CASCADE
);

-- =============================================
-- 4. TIẾN ĐỘ HỌC TẬP & ĐÁNH GIÁ (LEARNER WORKSPACE)
-- =============================================

CREATE TABLE Enrollments (
    enrollment_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    path_id INT NOT NULL,
    progress_percent INT DEFAULT 0,
    status VARCHAR(50) DEFAULT 'IN_PROGRESS' CHECK (status IN ('IN_PROGRESS', 'COMPLETED', 'FAILED')),
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES Users(user_id),
    FOREIGN KEY (path_id) REFERENCES Paths(path_id)
);

CREATE TABLE NodeProgress (
    progress_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    node_id INT NOT NULL,
    status VARCHAR(50) DEFAULT 'LOCKED' CHECK (status IN ('LOCKED', 'UNLOCKED', 'PASSED', 'FAILED')),
    best_score INT DEFAULT 0,
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES Users(user_id),
    FOREIGN KEY (node_id) REFERENCES Nodes(node_id)
);

CREATE TABLE UserAnswers (
    answer_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    question_id INT NOT NULL,
    answer_text NVARCHAR(MAX),
    is_correct BIT,
    score INT DEFAULT 0,
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES Users(user_id),
    FOREIGN KEY (question_id) REFERENCES Questions(question_id)
);

-- =============================================
-- 5. THẢO LUẬN & GAMIFICATION
-- =============================================

CREATE TABLE Comments (
    comment_id INT IDENTITY(1,1) PRIMARY KEY,
    node_id INT NOT NULL,
    user_id INT NOT NULL,
    content NVARCHAR(MAX) NOT NULL,
    is_pinned BIT DEFAULT 0,
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (node_id) REFERENCES Nodes(node_id),
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

CREATE TABLE Certificates (
    certificate_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    path_id INT NOT NULL,
    image_url VARCHAR(500),
    avg_score INT,
    issued_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES Users(user_id),
    FOREIGN KEY (path_id) REFERENCES Paths(path_id)
);

CREATE TABLE Notifications (
    notification_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    notification_type VARCHAR(50),
    message NVARCHAR(MAX),
    is_read BIT DEFAULT 0,
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

CREATE TABLE UserStreaks (
    streak_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    current_streak INT DEFAULT 0,
    last_study_date DATE,
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

-- =============================================
-- 6. INSERT DỮ LIỆU MẪU BAN ĐẦU
-- =============================================

INSERT INTO Roles (role_name) VALUES ('Admin'), ('User');
INSERT INTO Users (username, email, password_hash, role_id, full_name) 
VALUES ('admin', 'admin@learningpath.com', '$2a$10$wN9Q7b7T5O5V./wA5M9o.O8uD2bXn./oV3t7L.Z5b5kP.o5q5o5m', 1, 'System Administrator');
GO