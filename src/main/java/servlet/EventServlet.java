package servlet;

import utils.DatabaseUtil;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.util.Collection;

@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,
    maxFileSize = 1024 * 1024 * 10,
    maxRequestSize = 1024 * 1024 * 50
)
public class EventServlet extends HttpServlet {
    private static final String UPLOAD_DIR = "uploads";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // 创建上传目录
        String appPath = request.getServletContext().getRealPath("");
        String uploadPath = appPath + File.separator + UPLOAD_DIR;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs();
            if (!created) {
                throw new ServletException("无法创建上传目录: " + uploadPath);
            }
        }

        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            // 1. 保存事件
            String sql = "INSERT INTO events (user_id, title, description, event_date, has_files) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, userId);
            stmt.setString(2, request.getParameter("title"));
            stmt.setString(3, request.getParameter("description"));
            stmt.setDate(4, Date.valueOf(request.getParameter("eventDate")));
            
            Collection<Part> fileParts = request.getParts();
            boolean hasFiles = fileParts.stream()
                    .anyMatch(part -> "files".equals(part.getName()) && part.getSize() > 0);
            stmt.setBoolean(5, hasFiles);
            
            stmt.executeUpdate();
            
            // 获取新插入事件的ID
            ResultSet rs = stmt.getGeneratedKeys();
            int eventId = 0;
            if (rs.next()) {
                eventId = rs.getInt(1);
            }

            // 2. 处理文件上传
            if (hasFiles) {
                for (Part part : fileParts) {
                    if ("files".equals(part.getName()) && part.getSize() > 0) {
                        String fileName = getSubmittedFileName(part);
                        String fileType = getFileType(fileName);
                        String uniqueFileName = System.currentTimeMillis() + "_" + fileName;
                        File file = new File(uploadDir, uniqueFileName);
                        
                        // 保存文件
                        part.write(file.getAbsolutePath());
                        
                        // 保存文件信息到数据库
                        String fileSQL = "INSERT INTO files (user_id, event_id, filename, file_path, file_type) VALUES (?, ?, ?, ?, ?)";
                        PreparedStatement fileStmt = conn.prepareStatement(fileSQL);
                        fileStmt.setInt(1, userId);
                        fileStmt.setInt(2, eventId);
                        fileStmt.setString(3, fileName);
                        fileStmt.setString(4, uniqueFileName);
                        fileStmt.setString(5, fileType);
                        fileStmt.executeUpdate();
                    }
                }
            }
            
            conn.commit();
            response.sendRedirect("dashboard.jsp");
            
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new ServletException("添加事件失败", e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
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

    private String getFileType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        if (extension.matches("jpg|jpeg|png|gif")) {
            return "image";
        } else if (extension.equals("txt")) {
            return "text";
        }
        return "other";
    }

    // 删除事件
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        if (userId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int eventId = Integer.parseInt(request.getParameter("eventId"));

        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "DELETE FROM events WHERE event_id = ? AND user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, eventId);
            stmt.setInt(2, userId);
            
            stmt.executeUpdate();
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            throw new ServletException("删除事件失败", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("getEvents".equals(action)) {
            String date = request.getParameter("date");
            Integer userId = (Integer) request.getSession().getAttribute("userId");
            
            try (Connection conn = DatabaseUtil.getConnection()) {
                String sql = "SELECT * FROM events WHERE user_id = ? AND DATE(event_date) = ? " +
                           "ORDER BY created_at DESC";
                           
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, userId);
                stmt.setString(2, date);
                ResultSet rs = stmt.executeQuery();
                
                StringBuilder html = new StringBuilder();
                html.append("<h3>").append(date).append(" 的事件</h3>");
                
                while (rs.next()) {
                    html.append("<div class='event-item'>");
                    html.append("<h4>").append(rs.getString("title")).append("</h4>");
                    html.append("<p>").append(rs.getString("description")).append("</p>");
                    
                    if (rs.getBoolean("has_files")) {
                        String filesSql = "SELECT * FROM files WHERE event_id = ?";
                        PreparedStatement filesStmt = conn.prepareStatement(filesSql);
                        filesStmt.setInt(1, rs.getInt("event_id"));
                        ResultSet filesRs = filesStmt.executeQuery();
                        
                        while(filesRs.next()) {
                            String fileType = filesRs.getString("file_type");
                            String filePath = filesRs.getString("file_path");
                            if ("image".equals(fileType)) {
                                html.append("<img src='uploads/").append(filePath)
                                    .append("' class='event-image' alt='事件图片'>");
                            } else if ("text".equals(fileType)) {
                                html.append("<a href='uploads/").append(filePath)
                                    .append("' target='_blank'>查看文本文件</a>");
                            }
                        }
                    }
                    
                    html.append("</div>");
                }
                
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write(html.toString());
            } catch (Exception e) {
                throw new ServletException("获取事件失败", e);
            }
        }
    }
} 