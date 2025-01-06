package servlet;

import utils.DatabaseUtil;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RegisterServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        try (Connection conn = DatabaseUtil.getConnection()) {
            // 检查用户名是否已存在
            String checkSql = "SELECT id FROM users WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                request.setAttribute("error", "用户名已存在");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }
            
            // 插入新用户
            String insertSql = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setString(1, username);
            insertStmt.setString(2, password); // 实际应用中应该使用加密密码
            
            insertStmt.executeUpdate();
            
            // 注册成功，重定向到登录页面
            request.setAttribute("success", "注册成功，请登录");
            response.sendRedirect("login.jsp");
            
        } catch (Exception e) {
            request.setAttribute("error", "注册失败：" + e.getMessage());
            request.getRequestDispatcher("register.jsp").forward(request, response);
        }
    }
} 