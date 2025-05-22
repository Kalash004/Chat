package cz.cvut.fel.pjv.semwork.peer_to_peer_chat.service_view.format_utils;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    /**
     * Converts a given {@link Date} object into a string representing the time in the format "HH:mm:ss".
     * The method extracts the hour, minute, and second from the given date and formats it accordingly.
     * If the minute or second is less than 10, it will prepend a leading zero.
     *
     * @param date the {@link Date} object to be converted into a time string
     * @return a string representing the time in the "HH:mm:ss" format
     */
    public static String timestampToString(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        String dateStr = calendar.get(Calendar.HOUR_OF_DAY) + ":";
        if (minute < 10) {
            dateStr += "0" + minute;
        } else {
            dateStr += minute;
        }
        dateStr += ":";
        if (second < 10) {
            dateStr += "0" + second;
        } else {
            dateStr += second;
        }
        return dateStr;
    }
}
