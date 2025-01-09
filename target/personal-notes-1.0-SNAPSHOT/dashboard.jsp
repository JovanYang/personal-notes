<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="java.sql.*" %>
<%@ page import="utils.DatabaseUtil" %>
<%
    // 检查用户是否已登录
    Integer userId = (Integer) session.getAttribute("userId");
    if (userId == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>主页 - 个人记事系统</title>
    <meta charset="UTF-8">
    <style>
        .dashboard-container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }
        .content {
            display: grid;
            grid-template-columns: 2fr 1fr;
            gap: 20px;
        }
        .events-list {
            background: #fff;
            padding: 20px;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        .calendar-widget {
            background: #fff;
            padding: 20px;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        .btn {
            padding: 8px 15px;
            background-color: #4CAF50;
            color: white;
            text-decoration: none;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            margin-left: 10px;
        }
        .btn-danger {
            background-color: #f44336;
        }
        .event-item {
            padding: 15px;
            border-bottom: 1px solid #eee;
            margin-bottom: 10px;
        }
        .event-item h4 {
            margin: 0 0 10px 0;
        }
        .event-item p {
            margin: 0 0 10px 0;
            color: #666;
        }
        .event-item small {
            color: #999;
        }
        .event-image {
            max-width: 200px;
            margin: 10px 0;
        }
    </style>
</head>
<body>
    <div class="dashboard-container">
        <div class="header">
            <h2>欢迎回来，<%= session.getAttribute("username") %></h2>
            <div>
                <a href="event-form.jsp" class="btn">添加事件</a>
                <a href="logout" class="btn btn-danger">退出</a>
            </div>
        </div>
        
        <div class="content">
            <div class="events-list">
                <h3>最近事件</h3>
                <%
                    try (Connection conn = DatabaseUtil.getConnection()) {
                        String sql = "SELECT * FROM events WHERE user_id = ? ORDER BY event_date DESC LIMIT 10";
                        PreparedStatement stmt = conn.prepareStatement(sql);
                        stmt.setInt(1, userId);
                        ResultSet rs = stmt.executeQuery();
                        
                        while(rs.next()) {
                %>
                    <div class="event-item">
                        <h4><%= rs.getString("title") %></h4>
                        <p><%= rs.getString("description") %></p>
                        <small>日期：<%= rs.getDate("event_date") %></small>
                        
                        <% if (rs.getBoolean("has_files")) { %>
                            <%
                                String filesSql = "SELECT * FROM files WHERE event_id = ?";
                                PreparedStatement filesStmt = conn.prepareStatement(filesSql);
                                filesStmt.setInt(1, rs.getInt("event_id"));
                                ResultSet filesRs = filesStmt.executeQuery();
                                
                                while(filesRs.next()) {
                                    String fileType = filesRs.getString("file_type");
                                    String filePath = filesRs.getString("file_path");
                                    if ("image".equals(fileType)) {
                            %>
                                    <div>
                                        <img src="uploads/<%= filePath %>" 
                                             class="event-image" alt="事件图片">
                                    </div>
                            <%
                                    } else if ("text".equals(fileType)) {
                            %>
                                    <div>
                                        <a href="uploads/<%= filePath %>" 
                                           target="_blank">查看文本文件</a>
                                    </div>
                            <%
                                    }
                                }
                            %>
                        <% } %>
                        
                        <button onclick="deleteEvent(<%= rs.getInt("event_id") %>)" 
                                class="btn btn-danger">删除</button>
                    </div>
                <%
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                %>
            </div>
            
            <div class="calendar-widget">
                <jsp:include page="calendar.jsp" />
            </div>
        </div>
    </div>
    
    <script>
    function deleteEvent(eventId) {
        if (confirm('确定要删除这个事件吗？')) {
            fetch('event/' + eventId, {
                method: 'DELETE'
            }).then(response => {
                if (response.ok) {
                    location.reload();
                }
            });
        }
    }
    </script>
</body>
</html> 