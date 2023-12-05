package com.kse.wmsv2.common.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    /**
     * 現在時刻を指定したフォーマットに文字列として取得する。　ex) "MMddyyyyHHmmss", "yyyy-MM-dd HH:mm:ss"
     * @method : getNow
     * @author : hy-park
     * @create : 2023. 01. 18.
     * @return
     */
    public static String getNow(String fmt) {
        DateFormat df = new SimpleDateFormat(fmt);
        Date today = Calendar.getInstance().getTime();
        String reportDate = df.format(today);
        return reportDate;
    }
    /**
     * 現在時刻のタイムスタンプ文字列を取得する。(YYYY-MM-DD hh:ss:mm)
     * @method : getTimeStampNow
     * @author  : hy-park
     * @create : 2023. 01. 18.
     * @return　String
     */
    public static String getTimeStampNow() {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        Date now = calendar.getTime();
        return fmt.format(now);
    }
    /**
     * 取得した日付情報を指定のフォーマットに変更する。
     * @method : dateFormatChange
     * @author  : th-kim
     * @create : 2023. 05. 24.
     * @return　String
     */
    public static String dateFormatChange(Date date, String fmt) {
        DateFormat df = new SimpleDateFormat(fmt);
        String reportDate = df.format(date);
        return reportDate;
    }


}
