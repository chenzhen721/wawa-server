package com.wawa.common.util;

import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 日期工具类
 */
public final class DateUtil {

    /**
     * 获得第几周
     * @param timestamp 传入同步的时间戳
     * @return
     */
    public static Integer getWeekOfYear(Long timestamp){
        Calendar cal =  Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        if(timestamp != null){
            cal.setTimeInMillis(timestamp);
        }
        return cal.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 获得本周第几天
     * @param timestamp 传入同步的时间戳
     * @return
     */
    public static Integer getDayOfWeek(Long timestamp){
        Calendar cal = Calendar.getInstance();
        if(timestamp != null){
            cal.setTimeInMillis(timestamp);
        }
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek == 0)
            dayOfWeek = 7;

        return dayOfWeek;
    }

    /**
     * 获得当月第一天的时间戳
     * @return
     */
    public static Long firstDayOfMonthTimestamp() {
        Calendar cal = Calendar.getInstance();//获取当前日期
        cal.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        //设置当日时间0点0分0秒
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * 获取每月最后一天日期
     * @return
     */
    public static Date LastDayOfMonthDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, 1);//把日期设置为当月第一天
        cal.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    public static Integer getMinutes() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.MINUTE);
    }

    /**
     * 获得当月天数
     * @return
     */
    public static Integer getCurrentMonthLastDay(){
        Calendar a = Calendar.getInstance();
        a.set(Calendar.DATE, 1);//把日期设置为当月第一天
        a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
        return a.get(Calendar.DATE);
    }

    public static final String DFMT = "yyyy-MM-dd HH:mm:ss";

    public static Date getTime(String dateStr)  {
        if(StringUtils.isNotBlank(dateStr)){
            try {
                return new SimpleDateFormat(DFMT).parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获得格式化数据
     * @param df 例如  "yyyy-MM-dd HH:mm:ss"
     * @return
     */
    public static String getFormatDate(String df, Long timestamp){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        return new SimpleDateFormat(df).format(cal.getTime());
    }

    /**
     * 计算当前时间到明天0点的时间戳
     * @return
     */
    public static Long tomorrowMinus(){
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DATE,1);

        return calendar.getTimeInMillis() - date.getTime();
    }

    /**
     * 获取今天开始时间
     * @return
     */
    public static Map<String,Long> todayBeginAndEnd(){
        Map<String,Long> map = new HashMap<String,Long>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        map.put("begin",calendar.getTimeInMillis());
        calendar.add(Calendar.DATE,1);
        map.put("end",calendar.getTimeInMillis());
        return map;
    }

    public static void main(String[] args){
        System.out.println(DateUtil.tomorrowMinus());
    }

}
