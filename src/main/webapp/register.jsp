<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>注册 - 个人记事本系统</title>
    <meta charset="UTF-8">
    <style>
        .register-container {
            width: 300px;
            margin: 100px auto;
            padding: 20px;
            border: 1px solid #ddd;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        .form-group {
            margin-bottom: 15px;
        }
        .form-group label {
            display: block;
            margin-bottom: 5px;
        }
        .form-group input {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        .btn {
            width: 100%;
            padding: 10px;
            background-color: #4CAF50;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        .error-message {
            color: red;
            margin-bottom: 10px;
        }
    </style>
</head>
<body>
    <div class="register-container">
        <h2 style="text-align: center;">用户注册</h2>
        
        <% if(request.getAttribute("error") != null) { %>
            <div class="error-message">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
        
        <form action="register" method="post" onsubmit="return validateForm()">
            <div class="form-group">
                <label for="username">用户名：</label>
                <input type="text" id="username" name="username" required>
            </div>
            
            <div class="form-group">
                <label for="password">密码：</label>
                <input type="password" id="password" name="password" required>
            </div>
            
            <div class="form-group">
                <label for="confirmPassword">确认密码：</label>
                <input type="password" id="confirmPassword" name="confirmPassword" required>
            </div>
            
            <button type="submit" class="btn">注册</button>
        </form>
        
        <div style="margin-top: 15px; text-align: center;">
            <a href="login.jsp">已有账号？立即登录</a>
        </div>
    </div>
    
    <script>
    function validateForm() {
        var password = document.getElementById("password").value;
        var confirmPassword = document.getElementById("confirmPassword").value;
        
        if (password !== confirmPassword) {
            alert("两次输入的密码不一致！");
            return false;
        }
        return true;
    }
    </script>
</body>
</html> 