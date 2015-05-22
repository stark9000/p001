/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xu;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author xBerWise
 */
public class dateandtime {

    public static dateandtime getInstance() {
        return dateandtimeHolder.INSTANCE;
    }

    private static class dateandtimeHolder {

        private static final dateandtime INSTANCE = new dateandtime();
    }

    public String getTimeDate() {
        Calendar TimeStop = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String TimeStop_Str = sdf.format(TimeStop.getTime());
        return TimeStop_Str;

    }

    public String getDate() {
        Calendar TimeStop = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String TimeStop_Str = sdf.format(TimeStop.getTime());
        return TimeStop_Str;

    }

}
