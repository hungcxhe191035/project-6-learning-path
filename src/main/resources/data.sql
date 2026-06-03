-- =========================================
-- TAGS
-- =========================================
INSERT INTO tags (created_at, created_by, delete_flag, updated_at, updated_by, tag_name, description)
VALUES (GETDATE(), N'system', 0, NULL, NULL, N'Java Core', N'Khóa học nền tảng Java dành cho người mới bắt đầu');
INSERT INTO tags (created_at, created_by, delete_flag, updated_at, updated_by, tag_name, description)
VALUES (GETDATE(), N'system', 0, NULL, NULL, N'Spring Boot', N'Xây dựng ứng dụng web backend với Spring Boot');
INSERT INTO tags (created_at, created_by, delete_flag, updated_at, updated_by, tag_name, description)
VALUES (GETDATE(), N'system', 0, NULL, NULL, N'Hibernate', N'Tìm hiểu ORM và thao tác cơ sở dữ liệu với Hibernate');
INSERT INTO tags (created_at, created_by, delete_flag, updated_at, updated_by, tag_name, description)
VALUES (GETDATE(), N'system', 0, NULL, NULL, N'JPA', N'Lập trình truy cập dữ liệu với Java Persistence API');
INSERT INTO tags (created_at, created_by, delete_flag, updated_at, updated_by, tag_name, description)
VALUES (GETDATE(), N'system', 0, NULL, NULL, N'Spring Security', N'Bảo mật và phân quyền người dùng trong ứng dụng Spring');
INSERT INTO tags (created_at, created_by, delete_flag, updated_at, updated_by, tag_name, description)
VALUES (GETDATE(), N'system', 0, NULL, NULL, N'Thymeleaf', N'Template engine dùng để xây dựng giao diện web với Spring MVC');
INSERT INTO tags (created_at, created_by, delete_flag, updated_at, updated_by, tag_name, description)
VALUES (GETDATE(), N'system', 0, NULL, NULL, N'Rest API', N'Thiết kế và phát triển RESTful API');
INSERT INTO tags (created_at, created_by, delete_flag, updated_at, updated_by, tag_name, description)
VALUES (GETDATE(), N'system', 0, NULL, NULL, N'SQL Server', N'Thao tác và quản lý dữ liệu với Microsoft SQL Server');
INSERT INTO tags (created_at, created_by, delete_flag, updated_at, updated_by, tag_name, description)
VALUES (GETDATE(), N'system', 0, NULL, NULL, N'Docker', N'Triển khai ứng dụng bằng container Docker');
INSERT INTO tags (created_at, created_by, delete_flag, updated_at, updated_by, tag_name, description)
VALUES (GETDATE(), N'system', 0, NULL, NULL, N'ReactJS', N'Xây dựng giao diện frontend hiện đại với ReactJS');
-- =========================================
-- USERS
-- Password hash: -- $2a$12$9nYTgwKTwY931.EcLZ7OyuDon1jamWCxJD7kDuapIMMWPSPKeek8q
-- =========================================
INSERT INTO users ( created_at, created_by, delete_flag, updated_at, updated_by, email, password, full_name, phone, role, status, bank_name, bank_code, bank_account_number, bank_account_holder )
VALUES ( GETDATE(), N'system', 0, NULL, NULL, N'admin@fcourse.vn', N'$2a$12$9nYTgwKTwY931.EcLZ7OyuDon1jamWCxJD7kDuapIMMWPSPKeek8q', N'Nguyễn Thành Đạt', N'0901000001', N'ADMIN', N'ACTIVE', N'Vietcombank', N'VCB', N'1029384756', N'NGUYEN THANH DAT' );
INSERT INTO users ( created_at, created_by, delete_flag, updated_at, updated_by, email, password, full_name, phone, role, status, bank_name, bank_code, bank_account_number, bank_account_holder )
VALUES ( GETDATE(), N'system', 0, NULL, NULL, N'giangvien.java@fcourse.vn', N'$2a$12$9nYTgwKTwY931.EcLZ7OyuDon1jamWCxJD7kDuapIMMWPSPKeek8q', N'Trần Minh Khôi', N'0901000002', N'INSTRUCTOR', N'ACTIVE', N'Techcombank', N'TCB', N'8877665544', N'TRAN MINH KHOI' );
INSERT INTO users ( created_at, created_by, delete_flag, updated_at, updated_by, email, password, full_name, phone, role, status, bank_name, bank_code, bank_account_number, bank_account_holder )
VALUES ( GETDATE(), N'system', 0, NULL, NULL, N'giangvien.spring@fcourse.vn', N'$2a$12$9nYTgwKTwY931.EcLZ7OyuDon1jamWCxJD7kDuapIMMWPSPKeek8q', N'Lê Quốc Bảo', N'0901000003', N'INSTRUCTOR', N'ACTIVE', N'MB Bank', N'MBB', N'5566778899', N'LE QUOC BAO' );
INSERT INTO users ( created_at, created_by, delete_flag, updated_at, updated_by, email, password, full_name, phone, role, status, bank_name, bank_code, bank_account_number, bank_account_holder )
VALUES ( GETDATE(), N'system', 0, NULL, NULL, N'hocvien01@gmail.com', N'$2a$12$9nYTgwKTwY931.EcLZ7OyuDon1jamWCxJD7kDuapIMMWPSPKeek8q', N'Phạm Gia Hưng', N'0901000004', N'STUDENT', N'ACTIVE', N'ACB', N'ACB', N'1231231231', N'PHAM GIA HUNG' );
INSERT INTO users ( created_at, created_by, delete_flag, updated_at, updated_by, email, password, full_name, phone, role, status, bank_name, bank_code, bank_account_number, bank_account_holder )
VALUES ( GETDATE(), N'system', 0, NULL, NULL, N'hocvien02@gmail.com', N'$2a$12$9nYTgwKTwY931.EcLZ7OyuDon1jamWCxJD7kDuapIMMWPSPKeek8q', N'Nguyễn Đức Anh', N'0901000005', N'STUDENT', N'ACTIVE', N'Sacombank', N'STB', N'9988776655', N'NGUYEN DUC ANH' );
INSERT INTO users ( created_at, created_by, delete_flag, updated_at, updated_by, email, password, full_name, phone, role, status, bank_name, bank_code, bank_account_number, bank_account_holder )
VALUES ( GETDATE(), N'system', 0, NULL, NULL, N'hocvien03@gmail.com', N'$2a$12$9nYTgwKTwY931.EcLZ7OyuDon1jamWCxJD7kDuapIMMWPSPKeek8q', N'Võ Minh Quân', N'0901000006', N'STUDENT', N'ACTIVE', N'VPBank', N'VPB', N'1122334455', N'VO MINH QUAN' );
INSERT INTO users ( created_at, created_by, delete_flag, updated_at, updated_by, email, password, full_name, phone, role, status, bank_name, bank_code, bank_account_number, bank_account_holder )
VALUES ( GETDATE(), N'system', 0, NULL, NULL, N'hocvien04@gmail.com', N'$2a$12$9nYTgwKTwY931.EcLZ7OyuDon1jamWCxJD7kDuapIMMWPSPKeek8q', N'Đặng Hoàng Nam', N'0901000007', N'STUDENT', N'INACTIVE', N'BIDV', N'BIDV', N'6677889900', N'DANG HOANG NAM' );
-- =========================================
-- FILES (ảnh thumbnail khoá học)
-- =========================================
INSERT INTO files (created_at, created_by, delete_flag, updated_at, updated_by, file_name, file_url, file_type, purpose)
VALUES (GETDATE(), N'system', 0, NULL, NULL, N'spring-boot-thumbnail',
        N'https://images.unsplash.com/photo-1633356122544-f134324a6cee?w=480&h=270&fit=crop',
        N'IMAGE', N'COURSE_THUMBNAIL');

INSERT INTO files (created_at, created_by, delete_flag, updated_at, updated_by, file_name, file_url, file_type, purpose)
VALUES (GETDATE(), N'system', 0, NULL, NULL, N'reactjs-thumbnail',
        N'https://images.unsplash.com/photo-1633356122544-f134324a6cee?w=480&h=270&fit=crop&random=2',
        N'IMAGE', N'COURSE_THUMBNAIL');

INSERT INTO files (created_at, created_by, delete_flag, updated_at, updated_by, file_name, file_url, file_type, purpose)
VALUES (GETDATE(), N'system', 0, NULL, NULL, N'java-fullstack-thumbnail',
        N'https://images.unsplash.com/photo-1587620962725-abab7fe55159?w=480&h=270&fit=crop',
        N'IMAGE', N'COURSE_THUMBNAIL');

INSERT INTO files (created_at, created_by, delete_flag, updated_at, updated_by, file_name, file_url, file_type, purpose)
VALUES (GETDATE(), N'system', 0, NULL, NULL, N'docker-thumbnail',
        N'https://images.unsplash.com/photo-1605745341112-85968b19335b?w=480&h=270&fit=crop',
        N'IMAGE', N'COURSE_THUMBNAIL');

INSERT INTO files (created_at, created_by, delete_flag, updated_at, updated_by, file_name, file_url, file_type, purpose)
VALUES (GETDATE(), N'system', 0, NULL, NULL, N'microservices-thumbnail',
        N'https://images.unsplash.com/photo-1558494949-ef010cbdcc31?w=480&h=270&fit=crop',
        N'IMAGE', N'COURSE_THUMBNAIL');

-- =========================================
-- COURSES (5 khoá học)
-- =========================================

-- COURSE 1: Spring Boot
INSERT INTO courses (instructor_id, average_rating, total_reviews, total_students, delete_flag, created_at, created_by, updated_at, updated_by)
VALUES (
           (SELECT TOP 1 user_id FROM users WHERE email = N'giangvien.java@fcourse.vn'),
           4.8, 1250, 3400, 0, GETDATE(), N'system', NULL, NULL
       );

-- COURSE 2: ReactJS
INSERT INTO courses (instructor_id, average_rating, total_reviews, total_students, delete_flag, created_at, created_by, updated_at, updated_by)
VALUES (
           (SELECT TOP 1 user_id FROM users WHERE email = N'giangvien.java@fcourse.vn'),
           4.6, 890, 2100, 0, GETDATE(), N'system', NULL, NULL
       );

-- COURSE 3: Java Fullstack
INSERT INTO courses (instructor_id, average_rating, total_reviews, total_students, delete_flag, created_at, created_by, updated_at, updated_by)
VALUES (
           (SELECT TOP 1 user_id FROM users WHERE email = N'giangvien.java@fcourse.vn'),
           4.5, 2300, 5600, 0, GETDATE(), N'system', NULL, NULL
       );

-- COURSE 4: Docker
INSERT INTO courses (instructor_id, average_rating, total_reviews, total_students, delete_flag, created_at, created_by, updated_at, updated_by)
VALUES (
           (SELECT TOP 1 user_id FROM users WHERE email = N'giangvien.java@fcourse.vn'),
           4.7, 540, 1200, 0, GETDATE(), N'system', NULL, NULL
       );

-- COURSE 5: Microservices
INSERT INTO courses (instructor_id, average_rating, total_reviews, total_students, delete_flag, created_at, created_by, updated_at, updated_by)
VALUES (
           (SELECT TOP 1 user_id FROM users WHERE email = N'giangvien.java@fcourse.vn'),
           4.4, 320, 780, 0, GETDATE(), N'system', NULL, NULL
       );

-- =========================================
-- COURSE VERSIONS (thông tin chi tiết + gắn ảnh)
-- =========================================

-- VERSION của Course 1: Spring Boot
INSERT INTO course_versions (course_id, version_number, title, subtitle, description, price, status, thumbnail_file_id, delete_flag, created_at, created_by, updated_at, updated_by)
VALUES (
           (SELECT TOP 1 course_id FROM courses WHERE current_published_version_id IS NULL ORDER BY course_id ASC),
           1,
           N'Spring Boot 3 - Từ Cơ Bản Đến Nâng Cao',
           N'Học Spring Boot 3 với JPA, Security, REST API thực tế',
           N'Khoá học Spring Boot đầy đủ nhất bằng tiếng Việt',
           259000.00, N'APPROVED',
           (SELECT TOP 1 file_id FROM files WHERE file_name = N'spring-boot-thumbnail'),
           0, GETDATE(), N'system', NULL, NULL
       );
UPDATE courses SET current_published_version_id =
                       (SELECT TOP 1 course_version_id FROM course_versions WHERE title = N'Spring Boot 3 - Từ Cơ Bản Đến Nâng Cao')
WHERE course_id =
      (SELECT TOP 1 course_id FROM course_versions WHERE title = N'Spring Boot 3 - Từ Cơ Bản Đến Nâng Cao');

-- VERSION của Course 2: ReactJS
INSERT INTO course_versions (course_id, version_number, title, subtitle, description, price, status, thumbnail_file_id, delete_flag, created_at, created_by, updated_at, updated_by)
VALUES (
           (SELECT TOP 1 course_id FROM courses WHERE current_published_version_id IS NULL ORDER BY course_id ASC),
           1,
           N'ReactJS - Xây Dựng Web App Hiện Đại',
           N'Học ReactJS, Hooks, Redux, React Router từ đầu',
           N'Khoá học ReactJS thực chiến với project thực tế',
           299000.00, N'APPROVED',
           (SELECT TOP 1 file_id FROM files WHERE file_name = N'reactjs-thumbnail'),
           0, GETDATE(), N'system', NULL, NULL
       );
UPDATE courses SET current_published_version_id =
                       (SELECT TOP 1 course_version_id FROM course_versions WHERE title = N'ReactJS - Xây Dựng Web App Hiện Đại')
WHERE course_id =
      (SELECT TOP 1 course_id FROM course_versions WHERE title = N'ReactJS - Xây Dựng Web App Hiện Đại');

-- VERSION của Course 3: Java Fullstack
INSERT INTO course_versions (course_id, version_number, title, subtitle, description, price, status, thumbnail_file_id, delete_flag, created_at, created_by, updated_at, updated_by)
VALUES (
           (SELECT TOP 1 course_id FROM courses WHERE current_published_version_id IS NULL ORDER BY course_id ASC),
           1,
           N'Java Fullstack - Spring Boot + React',
           N'Xây dựng hệ thống web hoàn chỉnh với Spring Boot và React',
           N'Khoá học fullstack Java phổ biến nhất Việt Nam',
           349000.00, N'APPROVED',
           (SELECT TOP 1 file_id FROM files WHERE file_name = N'java-fullstack-thumbnail'),
           0, GETDATE(), N'system', NULL, NULL
       );
UPDATE courses SET current_published_version_id =
                       (SELECT TOP 1 course_version_id FROM course_versions WHERE title = N'Java Fullstack - Spring Boot + React')
WHERE course_id =
      (SELECT TOP 1 course_id FROM course_versions WHERE title = N'Java Fullstack - Spring Boot + React');

-- VERSION của Course 4: Docker
INSERT INTO course_versions (course_id, version_number, title, subtitle, description, price, status, thumbnail_file_id, delete_flag, created_at, created_by, updated_at, updated_by)
VALUES (
           (SELECT TOP 1 course_id FROM courses WHERE current_published_version_id IS NULL ORDER BY course_id ASC),
           1,
           N'Docker & Kubernetes Cho Developer',
           N'Containerize ứng dụng và deploy với K8s từ A đến Z',
           N'DevOps cơ bản đến nâng cao cho Java developer',
           199000.00, N'APPROVED',
           (SELECT TOP 1 file_id FROM files WHERE file_name = N'docker-thumbnail'),
           0, GETDATE(), N'system', NULL, NULL
       );
UPDATE courses SET current_published_version_id =
                       (SELECT TOP 1 course_version_id FROM course_versions WHERE title = N'Docker & Kubernetes Cho Developer')
WHERE course_id =
      (SELECT TOP 1 course_id FROM course_versions WHERE title = N'Docker & Kubernetes Cho Developer');

-- VERSION của Course 5: Microservices
INSERT INTO course_versions (course_id, version_number, title, subtitle, description, price, status, thumbnail_file_id, delete_flag, created_at, created_by, updated_at, updated_by)
VALUES (
           (SELECT TOP 1 course_id FROM courses WHERE current_published_version_id IS NULL ORDER BY course_id ASC),
           1,
           N'Microservices Với Spring Cloud',
           N'Thiết kế và triển khai hệ thống microservices thực tế',
           N'Kiến trúc microservices từ cơ bản đến production',
           399000.00, N'APPROVED',
           (SELECT TOP 1 file_id FROM files WHERE file_name = N'microservices-thumbnail'),
           0, GETDATE(), N'system', NULL, NULL
       );
UPDATE courses SET current_published_version_id =
                       (SELECT TOP 1 course_version_id FROM course_versions WHERE title = N'Microservices Với Spring Cloud')
WHERE course_id =
      (SELECT TOP 1 course_id FROM course_versions WHERE title = N'Microservices Với Spring Cloud');