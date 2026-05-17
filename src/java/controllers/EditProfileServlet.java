package controllers; // Sửa lại package cho đúng với project của bạn (vd: controllers, servlets...)

import dao.UserDAO;
import models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;

@WebServlet("/edit-profile")
@MultipartConfig( 
    fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
    maxFileSize = 1024 * 1024 * 5,       // 5 MB
    maxRequestSize = 1024 * 1024 * 10    // 10 MB
)
public class EditProfileServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("LOGIN_USER");
        
        if (currentUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            // 1. Lấy dữ liệu Text từ form
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String address = request.getParameter("address");
            String bio = request.getParameter("bio");
            String githubLink = request.getParameter("githubLink");
            String linkedinLink = request.getParameter("linkedinLink");
            String facebookLink = request.getParameter("facebookLink");
            String websiteLink = request.getParameter("websiteLink");

            // Xử lý riêng cho Ngày sinh (Ép kiểu từ String sang java.sql.Date)
            String dobStr = request.getParameter("dob");
            java.sql.Date dobDate = null;
            if (dobStr != null && !dobStr.trim().isEmpty()) {
                dobDate = java.sql.Date.valueOf(dobStr); // Chuyển "YYYY-MM-DD" thành Date
            }

            // 2. Xử lý Upload Ảnh Avatar
            Part filePart = request.getPart("avatar");
            String avatarFilename = currentUser.getAvatar(); // Giữ nguyên ảnh cũ mặc định
            
            if (filePart != null && filePart.getSize() > 0) {
                // Lấy tên file gốc
                String fileName = java.nio.file.Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                
                // Tạo tên file ngẫu nhiên để tránh trùng (Dùng getUserId() thay vì getUser_id())
                avatarFilename = "user" + currentUser.getUserId() + "_" + System.currentTimeMillis() + "_" + fileName;
                
                // Lưu file vào thư mục server
                String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads" + File.separator + "avatar";
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) uploadDir.mkdirs(); // Dùng mkdirs() để tạo nhiều thư mục lồng nhau nếu chưa có
                
                filePart.write(uploadPath + File.separator + avatarFilename);
            }

            // 3. Cập nhật dữ liệu vào object User hiện tại
            currentUser.setFullName(fullName);
            currentUser.setDob(dobDate); // Đã truyền vào kiểu Date chuẩn
            currentUser.setEmail(email);
            currentUser.setPhone(phone);
            currentUser.setAddress(address);
            currentUser.setBio(bio);
            currentUser.setGithubLink(githubLink);
            currentUser.setLinkedinLink(linkedinLink);
            currentUser.setFacebookLink(facebookLink);
            currentUser.setWebsiteLink(websiteLink);
            currentUser.setAvatar(avatarFilename);

            // 4. Gọi DAO để update xuống Database
            UserDAO dao = new UserDAO();
            boolean isUpdated = dao.updateProfile(currentUser);

            if (isUpdated) {
                // Cập nhật lại session để giao diện hiện thông tin mới nhất
                session.setAttribute("LOGIN_USER", currentUser);
                response.sendRedirect("profile.jsp?msg=updated");
            } else {
                request.setAttribute("error", "Failed to update profile. Please try again.");
                request.getRequestDispatcher("edit-profile.jsp").forward(request, response);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "System error: " + e.getMessage());
            request.getRequestDispatcher("edit-profile.jsp").forward(request, response);
        }
    }
}