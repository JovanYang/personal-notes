package servlet;

import utils.DatabaseUtil;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        try (Connection conn = DatabaseUtil.getConnection()) {
            // 查询用户
            String sql = "SELECT id, username FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password); // 实际应用中应该使用加密密码
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // 登录成功，创建会话
                HttpSession session = request.getSession();
                session.setAttribute("userId", rs.getInt("id"));
                session.setAttribute("username", rs.getString("username"));
                response.sendRedirect("dashboard.jsp");
            } else {
                // 登录失败
                request.setAttribute("error", "用户名或密码错误");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            throw new ServletException("登录失败", e);
        }
    }
} 