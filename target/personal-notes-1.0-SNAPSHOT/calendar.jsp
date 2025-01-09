<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="utils.CalendarUtil" %>
<%@ page import="java.sql.*" %>
<%@ page import="utils.DatabaseUtil" %>
<%@ page import="java.util.Date" %>
<%
    Calendar calendar = CalendarUtil.getCalendar(new Date());
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    String[] weekDays = {"日", "一", "二", "三", "四", "五", "六"};
    
    // 获取选中的日期（如果有）
    String selectedDate = request.getParameter("date");
    Integer userId = (Integer)session.getAttribute("userId");
    
    // 获取当月所有事件日期
    Map<String, Boolean> eventDates = new HashMap<>();
    try (Connection conn = DatabaseUtil.getConnection()) {
        String sql = "SELECT DISTINCT DATE(event_date) as event_date FROM events WHERE user_id = ? " +
                    "AND YEAR(event_date) = ? AND MONTH(event_date) = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, userId);
        stmt.setInt(2, year);
        stmt.setInt(3, month + 1);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            eventDates.put(rs.getString("event_date"), true);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
%>
<div class="calendar">
    <style>
        .calendar-header {
            text-align: center;
            margin-bottom: 10px;
        }
        .calendar-grid {
            display: grid;
            grid-template-columns: repeat(7, 1fr);
            gap: 5px;
        }
        .calendar-cell {
            text-align: center;
            padding: 5px;
            border: 1px solid #ddd;
        }
        .calendar-weekday {
            font-weight: bold;
            background-color: #f5f5f5;
        }
        .current-day {
            background-color: #4CAF50;
            color: white;
        }
        .has-events {
            background-color: #e8f5e9;
            font-weight: bold;
        }
        .calendar-cell {
            cursor: pointer;
        }
        .calendar-cell:hover {
            background-color: #f5f5f5;
        }
        #eventModal {
            display: none;
            position: fixed;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            background: white;
            padding: 20px;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0,0,0,0.3);
            z-index: 1000;
            max-width: 80%;
            max-height: 80vh;
            overflow-y: auto;
        }
        .modal-backdrop {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0,0,0,0.5);
            z-index: 999;
        }
        .event-image {
            max-width: 200px;
            margin: 10px 0;
        }
    </style>
    
    <div class="calendar-header">
        <h3><%= year %>年 <%= month + 1 %>月</h3>
    </div>
    
    <div class="calendar-grid">
        <% for(String day : weekDays) { %>
            <div class="calendar-cell calendar-weekday"><%= day %></div>
        <% } %>
        
        <%
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
            
            // 填充月初空白日期
            for(int i = 1; i < firstDayOfWeek; i++) {
        %>
            <div class="calendar-cell"></div>
        <%
            }
            
            // 填充日期
            for(int day = 1; day <= daysInMonth; day++) {
                String dateStr = String.format("%d-%02d-%02d", year, month + 1, day);
                String className = "calendar-cell";
                if(eventDates.containsKey(dateStr)) {
                    className += " has-events";
                }
                if(day == currentDay) {
                    className += " current-day";
                }
        %>
            <div class="<%= className %>" onclick="showEvents('<%= dateStr %>')">
                <%= day %>
            </div>
        <%
            }
        %>
    </div>
    
    <div id="eventModal"></div>
    <div class="modal-backdrop" onclick="hideEventModal()"></div>
    
    <script>
    function showEvents(date) {
        fetch('event?action=getEvents&date=' + date)
            .then(response => response.text())
            .then(html => {
                document.getElementById('eventModal').innerHTML = html;
                document.getElementById('eventModal').style.display = 'block';
                document.querySelector('.modal-backdrop').style.display = 'block';
            });
    }
    
    function hideEventModal() {
        document.getElementById('eventModal').style.display = 'none';
        document.querySelector('.modal-backdrop').style.display = 'none';
    }
    </script>
</div> 