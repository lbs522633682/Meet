package com.liboshuai.framework.utils;

/**
 * Author:boshuai.li
 * Time:2020/3/16   17:21
 * Description: 时间转换类
 */
public class TimeUtils {

    /**
     * 将当前时间转换成系统时间
     * 28800000  是时区，显示展示时间
     *
     * @param ms
     * @return
     */
    public static String formatDuring(long ms) {
        long hours = ((ms + 28800000) % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
        long minutes = (ms % (60 * 60 * 1000)) / (60 * 1000);
        long sec = (ms % (60 * 1000)) / 1000;

        String hoursStr = hours + "";
        if (hours < 10) {
            hoursStr = "0" + hours;
        }
        String minutesStr = minutes + "";
        if (minutes < 10) {
            minutesStr = "0" + minutes;
        }
        String secStr = sec + "";
        if (sec < 10) {
            secStr = "0" + sec;
        }

        return hoursStr + ":" + minutesStr + ":" + secStr;
    }

    /**
     * 格式化 （倒）计时时间
     * @param ms
     * @return
     */
    public static String formatTimer(long ms) {
        long hours = ((ms) % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
        long minutes = (ms % (60 * 60 * 1000)) / (60 * 1000);
        long sec = (ms % (60 * 1000)) / 1000;

        String hoursStr = hours + "";
        if (hours < 10) {
            hoursStr = "0" + hours;
        }
        String minutesStr = minutes + "";
        if (minutes < 10) {
            minutesStr = "0" + minutes;
        }
        String secStr = sec + "";
        if (sec < 10) {
            secStr = "0" + sec;
        }

        return hoursStr + ":" + minutesStr + ":" + secStr;
    }


}
