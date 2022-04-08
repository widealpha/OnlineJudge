package cn.sdu.oj.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtil {  //yyyy-MM-dd'T'HH：mm：ss.SSS'Z'
    private static SimpleDateFormat sdfLong = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat sdfShort = new SimpleDateFormat("yyyy-MM-dd");

    /*
     * 日期时间格式字符串转换为Date类型
     */
    public static Date stringLongToDate(String string) throws Exception {
        return sdfLong.parse(string);
    }

    /*
     * 日期格式字符串转换为Date类型
     */
    public static Date stringShortToDate(String string) throws Exception {
        return sdfShort.parse(string);
    }

    /*
     * 长整型毫秒(自1970-01-01  00:00:00 GMT过去的毫秒数，又称Unix时间戳)转换为Date类型
     */
    public static Date millisecondToDate(long millisecond) {
        return new Date(millisecond);
    }

    /*
     * 日期时间格式字符串转换为(Unix时间戳)长整型类型
     */
    public static long stringLongToMillisecond(String string) throws Exception {
        return stringLongToDate(string).getTime();
    }

    /*
     * 日期格式字符串转换为(Unix时间戳)长整型类型
     */
    public static long stringShortToMillisecond(String string) throws Exception {
        return stringShortToDate(string).getTime();
    }

    /*
     * Date类型转换为(Unix时间戳)长整型类型
     */
    public static long dateToMillisecond(Date date) {
        return date.getTime();
    }

    /*
     * (Unix时间戳)长整型类型转换为日期时间格式字符串
     */
    public static String millisecondToStringLong(long millisecond) {
        return sdfLong.format(millisecond);
    }

    /*
     * Date类型转换为日期时间格式字符串
     */
    public static String dateToStringLong(Date date) {
        return sdfLong.format(date);
    }

    /*
     * (Unix时间戳)长整型类型转换为日期格式字符串
     */
    public static String millisecondToStringShort(long millisecond) {
        return sdfShort.format(millisecond);
    }

    /*
     * Date类型转换为日期格式字符串
     */
    public static String dateToStringShort(Date date) {
        return sdfShort.format(date);
    }

    public static String CSTprase(String dateTime) throws ParseException {
        Date date = TimeUtil.utcToLocal(dateTime);
     //   SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MM dd HH:mm:ss zzz yyyy", Locale.US);

      //  Date date = simpleDateFormat.parse(dateTime);

        String sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        return sdf;

    }

    public static Date utcToLocal(String utcTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date utcDate = null;
        try {
            utcDate = sdf.parse(utcTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf.setTimeZone(TimeZone.getDefault());
        Date locatlDate = null;
        String localTime = sdf.format(utcDate.getTime());
        try {
            locatlDate = sdf.parse(localTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return locatlDate;
    }

    public static String timeEquation(String bd,String bt,String ed,String et) throws Exception {
        long b = TimeUtil.stringLongToMillisecond(bd + " " + bt);
        long e = TimeUtil.stringLongToMillisecond(ed + " " + et);
        int dis = (int) ((e - b) / 1000);
        int hour = dis / 3600;
        int min = (dis % 3600) / 60;
        String time = hour + "时" + min + "分";
        return time;
    }
}