package dao;

import models.User;
import utils.DBContext;
import java.sql.*;

public class UserDAO {
    
    public User login(String account, String password) {
        String sql = "SELECT * FROM Users WHERE username = ? OR email = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, account);
            ps.setString(2, account);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if(utils.BCryptUtil.checkPassword(password, rs.getString("password_hash"))) {
                    return extractUser(rs);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public boolean register(User user) {
        String sql = "INSERT INTO Users (username, email, password_hash, role_id) VALUES (?, ?, ?, 2)"; // 2 is User role
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean checkExists(String username, String email) {
        String sql = "SELECT 1 FROM Users WHERE username = ? OR email = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, email);
            return ps.executeQuery().next();
        } catch (Exception e) { return false; }
    }
    
    public User findByUsername(String username) {
        String sql = "SELECT * FROM Users WHERE username = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return extractUser(rs);
        } catch (Exception e) {}
        return null;
    }

    // ĐÃ CẬP NHẬT: Lưu đầy đủ thông tin, hỗ trợ tiếng Việt (NString) và trả về boolean
    public boolean updateProfile(User user) {
        String sql = "UPDATE Users SET full_name=?, dob=?, phone=?, address=?, bio=?, github_link=?, facebook_link=?, linkedin_link=?, website_link=?, avatar=? WHERE user_id=?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setNString(1, user.getFullName()); // Dùng NString cho dữ liệu Tiếng Việt
            
            // Xử lý an toàn cho Ngày sinh (Date)
            if (user.getDob() != null) {
                ps.setDate(2, new java.sql.Date(user.getDob().getTime()));
            } else {
                ps.setNull(2, java.sql.Types.DATE);
            }
            
            ps.setString(3, user.getPhone());
            ps.setNString(4, user.getAddress());  // Tiếng Việt
            ps.setNString(5, user.getBio());      // Tiếng Việt
            
            ps.setString(6, user.getGithubLink());
            ps.setString(7, user.getFacebookLink());
            ps.setString(8, user.getLinkedinLink());
            ps.setString(9, user.getWebsiteLink());
            ps.setString(10, user.getAvatar());
            
            ps.setInt(11, user.getUserId());
            
            return ps.executeUpdate() > 0;
            
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return false;
    }

    // Giữ nguyên hàm này phòng trường hợp bạn chỉ muốn update riêng ảnh ở tính năng khác
    public void updateAvatar(int userId, String filename) {
        String sql = "UPDATE Users SET avatar = ? WHERE user_id = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, filename); ps.setInt(2, userId); ps.executeUpdate();
        } catch (Exception e) {}
    }

    // OTP Methods
    public void saveOTP(int userId, String otp) {
        String sql = "INSERT INTO PasswordResetTokens (user_id, otp_code, expiry_time) VALUES (?, ?, DATEADD(minute, 5, GETDATE()))";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId); ps.setString(2, otp); ps.executeUpdate();
        } catch (Exception e) {}
    }

    public boolean validateOTP(String email, String otp) {
        String sql = "SELECT p.token_id FROM PasswordResetTokens p JOIN Users u ON p.user_id = u.user_id WHERE u.email = ? AND p.otp_code = ? AND p.is_used = 0 AND p.expiry_time > GETDATE()";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email); ps.setString(2, otp);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                markOTPUsed(rs.getInt("token_id"));
                return true;
            }
        } catch (Exception e) {}
        return false;
    }
    
    private void markOTPUsed(int tokenId) {
        String sql = "UPDATE PasswordResetTokens SET is_used = 1 WHERE token_id = ?";
        try(Connection c = DBContext.getConnection(); PreparedStatement ps = c.prepareStatement(sql)){ ps.setInt(1, tokenId); ps.executeUpdate();} catch(Exception e){}
    }

    public void updatePassword(String email, String hash) {
        String sql = "UPDATE Users SET password_hash = ? WHERE email = ?";
         try(Connection c = DBContext.getConnection(); PreparedStatement ps = c.prepareStatement(sql)){ ps.setString(1, hash); ps.setString(2, email); ps.executeUpdate();} catch(Exception e){}
    }

    private User extractUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id")); 
        u.setUsername(rs.getString("username")); 
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash")); 
        u.setRoleId(rs.getInt("role_id")); 
        u.setFullName(rs.getString("full_name")); 
        u.setPhone(rs.getString("phone"));
        u.setBio(rs.getString("bio")); 
        u.setAvatar(rs.getString("avatar"));
        
        u.setGithubLink(rs.getString("github_link"));
        u.setFacebookLink(rs.getString("facebook_link"));
        u.setLinkedinLink(rs.getString("linkedin_link"));
        u.setWebsiteLink(rs.getString("website_link"));
        u.setAddress(rs.getString("address"));
        u.setDob(rs.getDate("dob")); // Lấy chính xác Date
        
        return u;
    }
    
    public User findByEmail(String email) {
        String sql = "SELECT * FROM Users WHERE email = ?";
        try (Connection conn = DBContext.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractUser(rs); 
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        return null;
    }
}