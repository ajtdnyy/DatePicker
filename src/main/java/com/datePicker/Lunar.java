package com.datePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Lunar {

    public static long year;
    public static long month;
    static long day;
    private static boolean leap;
    static SimpleDateFormat chineseDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
    final static String[] Gan = new String[]{"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
    final static String[] Zhi = new String[]{"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
    public final static String chineseNumber[] = {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"};
    final static String[] Animals = new String[]{"鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪"};
    final static long[] lunarInfo = new long[]{
        0x04bd8, 0x04ae0, 0x0a570, 0x054d5, 0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0, 0x055d2,
        0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540, 0x0d6a0, 0x0ada2, 0x095b0, 0x14977,
        0x04970, 0x0a4b0, 0x0b4b5, 0x06a50, 0x06d40, 0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970,
        0x06566, 0x0d4a0, 0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0, 0x1c8d7, 0x0c950,
        0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4, 0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557,
        0x06ca0, 0x0b550, 0x15355, 0x04da0, 0x0a5d0, 0x14573, 0x052d0, 0x0a9a8, 0x0e950, 0x06aa0,
        0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570, 0x05260, 0x0f263, 0x0d950, 0x05b57, 0x056a0,
        0x096d0, 0x04dd5, 0x04ad0, 0x0a4d0, 0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b5a0, 0x195a6,
        0x095b0, 0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50, 0x06d40, 0x0af46, 0x0ab60, 0x09570,
        0x04af5, 0x04970, 0x064b0, 0x074a3, 0x0ea50, 0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0,
        0x0c960, 0x0d954, 0x0d4a0, 0x0da50, 0x07552, 0x056a0, 0x0abb7, 0x025d0, 0x092d0, 0x0cab5,
        0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9, 0x04ba0, 0x0a5b0, 0x15176, 0x052b0, 0x0a930,
        0x07954, 0x06aa0, 0x0ad50, 0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260, 0x0ea65, 0x0d530,
        0x05aa0, 0x076a3, 0x096d0, 0x04bd7, 0x04ad0, 0x0a4d0, 0x1d0b6, 0x0d250, 0x0d520, 0x0dd45,
        0x0b5a0, 0x056d0, 0x055b2, 0x049b0, 0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0};

    /**
     * 传回农历y年的总天数
     *
     * @param y
     * @return
     */
    private static int yearDays(int y) {
        int i, sum = 348;
        for (i = 0x8000; i > 0x8; i >>= 1) {
            if ((lunarInfo[y - 1900] & i) != 0) {
                sum += 1;
            }
        }
        return (sum + leapDays(y));
    }

    /**
     * 传回农历 y年闰月的天数
     *
     * @param y
     * @return
     */
    private static int leapDays(long y) {
        if (leapMonth((int) y) != 0) {
            if ((lunarInfo[(int) (y - 1900)] & 0x10000) != 0) {
                return 30;
            } else {
                return 29;
            }
        } else {
            return 0;
        }
    }

    /**
     * 传回农历 y年闰哪个月 1-12 , 没闰传回 0
     *
     * @param y
     * @return
     */
    private static int leapMonth(long y) {
        return (int) (lunarInfo[(int) (y - 1900)] & 0xf);
    }

    /**
     * 传回农历 y年m月的总天数
     *
     * @param y
     * @param m
     * @return
     */
    private static int monthDays(long y, long m) {
        if ((lunarInfo[(int) (y - 1900)] & (0x10000 >> m)) == 0) {
            return 29;
        } else {
            return 30;
        }
    }

    /**
     * 传回农历 y年的生肖
     *
     * @param year
     * @return
     */
    public static String animalsYear(int year) {
        return Animals[(year - 4) % 12];
    }

    /**
     * 从已知日期计算干支纪日的公式为：
     * <li>g=4C+[C/4]+[5y]+[y/4]+[3*(m+1)/5]+d-3
     * <li>z=8C+[C/4]+[5y]+[y/4]+[3*(m+1)/5]+d+7+i
     * <p>
     * 其中c是世纪数减1。奇数月 i=0，偶数月 i=6，年份前两位，y 是年份后两位，M 是月份，d 是日数。[ ] 表示取整数。 1月和
     * 2月按上一年的 13月和 14月来算，因此C和y也要按上一年的年份来取值。 g 除以 10 的余数是天干，z 除以 12 的余数是地支。
     *
     * @param year 年
     * @param mon 月
     * @param date 日
     * @return 日的天干地支
     */
    public static String cyclicalDate(int year, int mon, int date) {
        String str = year + "";
        int c = Integer.parseInt(str.substring(0, 2));
        int y = Integer.parseInt(str.substring(2));
        int i = 0;
        //getLunarDate(year, mon, date);//获取农历月份 month的值
        if (mon % 2 == 0) {
            i = 6;
        } else {
            i = 0;
        }
        int g = 4 * c + c / 4 + 5 * y + y / 4 + 3 * (mon + 1) / 5 + date - 3;
        int z = 8 * c + c / 4 + 5 * y + y / 4 + 3 * (mon + 1) / 5 + date + 7 + i;
        g = g % 10 - 1;
        z = z % 12 - 1;
        g = g < 0 ? 10 + g : g;
        z = z < 0 ? 12 + z : z;
        return Gan[g] + Zhi[z];
    }

    public static String cyclicalDate(Calendar c) {
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH) + 1;
        int d = c.get(Calendar.DAY_OF_MONTH);
        return cyclicalDate(y, m, d);
    }

    /**
     * <li>年的天干为甲或已时，则正月的天干为丙；
     * <li>年的天干为乙或庚时，则正月的天干为戊；
     * <li>年的天干为丙或辛时，则正月的天干为庚；
     * <li>年的天干为丁或壬时，则正月的天干为壬；
     * <li>年的天干为戊或癸时，则正月的天干为甲。
     *
     * @param year 年
     * @param mon 月
     * @param date
     * @return 月的天干地支
     */
    public static String cyclicalMonth(int year, int mon, int date) {
        String gan = cyclicalYear(year);
        getLunarDate(year, mon, date);//获取农历月份 month的值
        int first = 0;
        if (gan.startsWith("甲") || gan.startsWith("已")) {
            first = 3;
        } else if (gan.startsWith("乙") || gan.startsWith("庚")) {
            first = 5;
        } else if (gan.startsWith("丙") || gan.startsWith("辛")) {
            first = 7;
        } else if (gan.startsWith("丁") || gan.startsWith("壬")) {
            first = 9;
        } else if (gan.startsWith("戊") || gan.startsWith("癸")) {
            first = 1;
        }
        first += month - 2;
        if (first >= 10) {
            first -= 10;
        } else if (first < 0) {
            first += 10;
        }
        return (Gan[first] + Zhi[mon % 12]);
    }

    public static String cyclicalMonth(Calendar c) {
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH) + 1;
        int d = c.get(Calendar.DAY_OF_MONTH);
        return cyclicalMonth(y, m, d);
    }

    /**
     * @param year 年
     * @return 年的天干地支
     */
    public static String cyclicalYear(int year) {
        int num = year - 1900 + 36;
        return (Gan[num % 10] + Zhi[num % 12]);
    }

    /**
     * 农历中文显示
     *
     * @param day
     * @param leap
     * @return
     */
    public static String getChinaDayString(int day, boolean leap) {
        String chineseTen[] = {"初", "十", "廿", "卅"};
        int n = day % 10 == 0 ? 9 : day % 10 - 1;
        if (day > 30) {
            return "";
        }
        if (day == 10) {
            if (leap) {
                return "初十(闰)";
            }
            return "初十";
        } else {
            if (leap) {
                return chineseTen[day / 10] + chineseNumber[n] + "(闰)";
            }
            return chineseTen[day / 10] + chineseNumber[n];
        }
    }

    /**
     * @param c 公历日期
     * @return 对应农历日期
     */
    public static String getLunarDate(Calendar c) {
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH) + 1;
        int d = c.get(Calendar.DAY_OF_MONTH);
        return getLunarDate(y, m, d);
    }

    /**
     * 传出y年m月d日对应的农历. yearCyl3:农历年与1864的相差数 ? monCyl4:从1900年1月31日以来,闰月数
     * dayCyl5:与1900年1月31日相差的天数,再加40 ?
     *
     * @param year_log
     * @param month_log
     * @param day_log
     * @return
     */
    public static String getLunarDate(int year_log, int month_log, int day_log) {
        long monCyl;
        long leapMonth = 0;
        String nowadays;
        Date baseDate = null;
        Date nowaday = null;
        try {
            baseDate = chineseDateFormat.parse("1900年1月31日");
            nowadays = year_log + "年" + month_log + "月" + day_log + "日";
            nowaday = chineseDateFormat.parse(nowadays);
            //求出和1900年1月31日相差的天数
            long offset = (nowaday.getTime() - baseDate.getTime()) / 86400000L;
//            dayCyl = offset + 40;
            monCyl = 14;
            //用offset减去每农历年的天数
            // 计算当天是农历第几天
            //i最终结果是农历的年份
            //offset是当年的第几天
            int iYear, daysOfYear = 0;
            for (iYear = 1900; iYear < 10000 && offset > 0; iYear++) {
                daysOfYear = yearDays(iYear);
                offset -= daysOfYear;
                monCyl += 12;
            }
            if (offset < 0) {
                offset += daysOfYear;
                iYear--;
                monCyl -= 12;
            }
            //农历年份
            year = iYear;
//            yearCyl = iYear - 1864;
            leapMonth = leapMonth(iYear); //闰哪个月,1-12
            leap = false;
            //用当年的天数offset,逐个减去每月（农历）的天数，求出当天是本月的第几天
            int iMonth, daysOfMonth = 0;
            for (iMonth = 1; iMonth < 13 && offset > 0; iMonth++) {
                //闰月
                if (leapMonth > 0 && iMonth == (leapMonth + 1) && !leap) {
                    --iMonth;
                    leap = true;
                    daysOfMonth = leapDays(year);
                } else {
                    daysOfMonth = monthDays(year, iMonth);
                }
                offset -= daysOfMonth;
                //解除闰月
                if (leap && iMonth == (leapMonth + 1)) {
                    leap = false;
                }
                if (!leap) {
                    monCyl++;
                }
            }
            //offset为0时，并且刚才计算的月份是闰月，要校正
            if (offset == 0 && leapMonth > 0 && iMonth == leapMonth + 1) {
                if (leap) {
                    leap = false;
                } else {
                    leap = true;
                    --iMonth;
                    --monCyl;
                }
            }
            //offset小于0时，也要校正
            if (offset < 0) {
                offset += daysOfMonth;
                --iMonth;
                --monCyl;
            }
            month = iMonth;
            day = offset + 1;
            if (((month) == 1) && day == 1) {
                return "春节";
            } else if (((month) == 1) && day == 15) {
                return "元宵";
            } else if (((month) == 5) && day == 5) {
                return "端午";
            } else if (((month) == 8) && day == 15) {
                return "中秋";
            } else if (((month) == 7) && day == 7) {
                return "七夕";
            } else if (day == 1) {
                if (leap) {
                    return "初一(闰)";
                }
                return "初一";//chineseNumber[(int) month - 1] + "月";
            } else {
                return getChinaDayString((int) day, leap);
            }
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        return null;
    }

    @Override
    public String toString() {
        if ("一".equals(chineseNumber[(int) month - 1]) && "初一".equals(getChinaDayString((int) day, false))) {
            return "农历" + year + "年";
        } else if ("初一".equals(getChinaDayString((int) day, false))) {
            return chineseNumber[(int) month - 1] + "月";
        } else {
            return getChinaDayString((int) day, false);
        }
    }
}
