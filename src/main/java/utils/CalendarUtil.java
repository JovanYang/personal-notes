package utils;

import java.util.Calendar;
import java.util.Date;

public class CalendarUtil {
    public static Calendar getCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        return calendar;
    }

    public static int getWeeksInMonth(Calendar calendar) {
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        return (int) Math.ceil((firstDayOfWeek + daysInMonth - 1) / 7.0);
    }
} 