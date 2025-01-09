<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>添加事件 - 个人记事系统</title>
    <meta charset="UTF-8">
    <style>
        .form-container {
            width: 500px;
            margin: 50px auto;
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
        .form-group input[type="text"],
        .form-group input[type="date"],
        .form-group textarea {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        .form-group textarea {
            height: 100px;
            resize: vertical;
        }
        .btn {
            padding: 10px 20px;
            background-color: #4CAF50;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        .btn-cancel {
            background-color: #f44336;
        }
        .file-preview {
            margin-top: 10px;
            max-width: 200px;
        }
        .file-preview img {
            max-width: 100%;
            height: auto;
        }
    </style>
</head>
<body>
    <div class="form-container">
        <h2>添加新事件</h2>
        
        <form action="event" method="post" enctype="multipart/form-data">
            <div class="form-group">
                <label for="title">标题：</label>
                <input type="text" id="title" name="title" required>
            </div>
            
            <div class="form-group">
                <label for="description">描述：</label>
                <textarea id="description" name="description" required></textarea>
            </div>
            
            <div class="form-group">
                <label for="eventDate">日期：</label>
                <input type="date" id="eventDate" name="eventDate" required>
            </div>

            <div class="form-group">
                <label for="files">上传文件：</label>
                <input type="file" id="files" name="files" multiple 
                       accept="image/*,.txt" onchange="previewFiles(this)">
                <div id="filePreview"></div>
            </div>
            
            <div style="text-align: right;">
                <a href="dashboard.jsp" class="btn btn-cancel">取消</a>
                <button type="submit" class="btn">保存</button>
            </div>
        </form>
    </div>

    <script>
    function previewFiles(input) {
        var preview = document.getElementById('filePreview');
        preview.innerHTML = '';
        
        for(var i = 0; i < input.files.length; i++) {
            var file = input.files[i];
            if(file.type.startsWith('image/')) {
                var reader = new FileReader();
                reader.onload = function(e) {
                    var img = document.createElement('img');
                    img.src = e.target.result;
                    img.className = 'file-preview';
                    preview.appendChild(img);
                }
                reader.readAsDataURL(file);
            } else {
                var p = document.createElement('p');
                p.textContent = '文件: ' + file.name;
                preview.appendChild(p);
            }
        }
    }
    </script>
</body>
</html> 