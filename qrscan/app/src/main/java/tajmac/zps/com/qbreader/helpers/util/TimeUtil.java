package tajmac.zps.com.qbreader.helpers.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import tajmac.zps.com.qbreader.helpers.constant.AppConstants;

public class TimeUtil {
    public static String getFormattedDateString(long milliseconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);

        return new SimpleDateFormat(AppConstants.APP_COMMON_DATE_FORMAT,
                Locale.ENGLISH).format(calendar.getTime());
    }

    public static String getFormattedDateString(long milliseconds, String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);

        return new SimpleDateFormat(format, Locale.ENGLISH).format(calendar.getTime());
    }
}
