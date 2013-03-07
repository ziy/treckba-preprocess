package edu.cmu.cs.ziy.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class CalendarUtils {

  public static final Calendar BIG_BANG = getInstance(Long.MIN_VALUE);

  public static final Calendar BIG_RIP = getInstance(Long.MAX_VALUE);

  public static final Calendar PRESENT = Calendar.getInstance();

  public static final Calendar getInstance(long millis) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(millis);
    return calendar;
  }

  public static final Calendar getGmtInstance(long millis) {
    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    calendar.setTimeInMillis(millis);
    return calendar;
  }

  public static Calendar getInstance(String timeString, String dateFormatPattern)
          throws ParseException {
    Calendar calendar = Calendar.getInstance();
    DateFormat df = new SimpleDateFormat(dateFormatPattern);
    calendar.setTime(df.parse(timeString));
    return calendar;
  }

  public static Calendar getGmtInstance(String timeString, String dateFormatPattern)
          throws ParseException {
    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    DateFormat df = new SimpleDateFormat(dateFormatPattern);
    calendar.setTime(df.parse(timeString));
    return calendar;
  }

  public static String toString(Calendar calendar, String dateFormatPattern)
          throws ParseException {
    DateFormat df = new SimpleDateFormat(dateFormatPattern);
    return df.format(calendar.getTime());
  }

}
