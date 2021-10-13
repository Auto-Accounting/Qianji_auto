package cn.dreamn.qianji_auto.utils.runUtils;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 格式化时间 工具类
 */
public class DateUtils {


    public static String getTime(String s) {
        return getTime(s, 0);
    }

    public static String getTime(String s, int day) {
        long time = System.currentTimeMillis();
        time = time + (long) day * 24 * 60 * 60 * 1000;
        return (new SimpleDateFormat(s, Locale.getDefault())).format(new Date(time));
    }


    public static String getShortTime(long date, String format) {

        return (new SimpleDateFormat(format, Locale.getDefault())).format(new Date(date));
    }

    public static String getTime(String s, long time) {
        return (new SimpleDateFormat(s, Locale.getDefault())).format(new Date(time));
    }

    /*
     * 将时间转换为时间戳
     */
    public static long dateToStamp(String time, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.getDefault());
        try {
            Date date = simpleDateFormat.parse(time);
            assert date != null;
            return date.getTime();
        } catch (Throwable e) {
            return 0;
        }
    }

    public static String stampToDate(long time, String format) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.getDefault());
        Date date = new Date(time);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static String stampToDate(String time, String format) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.getDefault());
        Date date = new Date(Long.parseLong(time));
        res = simpleDateFormat.format(date);
        return res;
    }

    public static long getAnyTime(String time) {
        long t;
        try {
            if (time.contains("undefined") || time.equals("")) {
                throw new Throwable("not useful date");
            }
            if ((time.length() == 10 || time.length() == 13) && !time.contains(" ")) {
                //数字类型的时间戳
                t = Long.parseLong(time);
                if (time.length() == 10) {
                    t = t * 1000;
                }
            } else {
                String format = "";
                String[] t2;
                boolean needYear = true;
                String yearType = "-";
                if (time.contains("年")) {
                    needYear = false;
                }
                if (time.contains("月")) {
                    format += "yyyy年MM月";
                    yearType = "年";
                }
                if (time.contains("日")) {
                    format += "dd日";
                }
                if (time.contains("-")) {
                    t2 = time.split("-");
                    yearType = "-";
                    if (t2.length == 3) {
                        needYear = false;
                    }
                    format += "yyyy-MM-dd";
                } else if (time.contains("/")) {
                    t2 = time.split("/");
                    yearType = "/";
                    if (t2.length == 3) {
                        needYear = false;
                    }
                    format += "yyyy/MM/dd";
                }

                if (time.contains(" ")) {
                    format += " ";
                }

                if (time.contains(":")) {
                    t2 = time.split(":");
                    if (t2.length == 3) {
                        format += "HH:mm:ss";
                    } else if (t2.length == 2) {
                        format += "HH:mm";
                    }
                }
                if (time.contains("时")) {
                    format += "HH时";
                }
                if (time.contains("分")) {
                    format += "mm分";
                }
                if (time.contains("秒")) {
                    format += "ss秒";
                }

                if (needYear) {
                    time = getTime("yyyy") + yearType + time;
                }

                Log.i("Time原始数据：" + time + "计算格式化数据:" + format);
                if (format.equals("")) throw new Throwable("not useful date");
                t = dateToStamp(time, format);
            }
        } catch (Throwable e) {
            t = dateToStamp(getTime("yyyy-MM-dd HH:mm:ss"), "yyyy-MM-dd HH:mm:ss");
        }

        return t;
    }

}

