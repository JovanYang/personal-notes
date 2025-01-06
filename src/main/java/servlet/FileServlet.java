package servlet;

import utils.DatabaseUtil;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.UUID;

@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1 MB
    maxFileSize = 1024 * 1024 * 10,  // 10 MB
    maxRequestSize = 1024 * 1024 * 15 // 15 MB
)
public class FileServlet extends HttpServlet {
    private static final String UPLOAD_DIR = "uploads";

    // 文件上传
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // 确保上传目录存在
        String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        try {
            Part filePart = request.getPart("file");
            String fileName = getSubmittedFileName(filePart);
            String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
            String filePath = uploadPath + File.separator + uniqueFileName;
            
            // 保存文件
            filePart.write(filePath);

            // 保存文件信息到数据库
            try (Connection conn = DatabaseUtil.getConnection()) {
                String sql = "INSERT INTO files (user_id, filename, file_path) VALUES (?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, userId);
                stmt.setString(2, fileName);
                stmt.setString(3, uniqueFileName);
                stmt.executeUpdate();
            }

            response.sendRedirect("dashboard.jsp");
        } catch (Exception e) {
            throw new ServletException("文件上传失败", e);
        }
    }

    private String getSubmittedFileName(Part part) {
        String header = part.getHeader("content-disposition");
        for (String token : header.split(";")) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf('=') + 2, token.length() - 1);
            }
        }
        return null;
    }
} 