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