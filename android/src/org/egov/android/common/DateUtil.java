package org.egov.android.common;

import android.annotation.SuppressLint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class DateUtil {

	public static Date toDateTime(String datetime, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        try {
            return sdf.parse(datetime);
        } catch (ParseException e) {
        }
        return null;
    }

    public static String getCurrentDateTime(Date datetime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(datetime);
    }

    /*public static String getCurrentDateTime() {
        return DateUtil.getCurrentDateTime(new Date());
    }

    public static long getTimeMills(String dateTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(dateFormat.parse(dateTime));
        } catch (ParseException e) {
            return 0;
        }
        return cal.getTimeInMillis();
    }

    public static String getTimeFromMilliSeconds(long ms) {
        long diffSeconds = ms / 1000 % 60;
        long diffMinutes = ms / (60 * 1000) % 60;
        long diffHours = ms / (60 * 60 * 1000);
        return diffHours + "," + diffMinutes + "," + diffSeconds;
    }

    public static String getTimeZone() {
        String timeZone = new SimpleDateFormat("Z").format(Calendar.getInstance().getTime());
        return timeZone.substring(0, 3) + ":" + timeZone.substring(3, 5);
    }*/

}
