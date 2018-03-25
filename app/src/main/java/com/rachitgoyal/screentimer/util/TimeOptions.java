package com.rachitgoyal.screentimer.util;

import android.support.annotation.IntDef;

/**
 * Created by Rachit Goyal on 17/03/18
 */

public class TimeOptions {
    public static final int FIFTEEN_MINS = 900;
    public static final int THIRTY_MINS = 1800;
    public static final int FOURTY_FIVE_MINS = 2700;
    public static final int ONE_HOUR = 3600;
    public static final int ONE_HALF_HOUR = 5400;
    public static final int TWO_HOURS = 7200;
    public static final int TWO_HALF_HOURS = 9000;
    public static final int THREE_HOURS = 10800;
    public static final int FOUR_HOURS = 14400;
    public static final int FIVE_HOURS = 18000;
    public static final int SIX_HOURS = 21600;
    public static final int EIGHT_HOURS = 28800;
    public static final int TEN_HOURS = 36000;
    public static final int TWELVE_HOURS = 43200;
    public static final int FIFTEEN_HOURS = 54000;
    public static final int EIGHTEEN_HOURS = 64800;
    public static final int TWENTY_ONE_HOURS = 75600;
    public static final int TWENTY_FOUR_HOURS = 86400;

    @IntDef({
                    FIFTEEN_MINS,
                    THIRTY_MINS,
                    FOURTY_FIVE_MINS,
                    ONE_HOUR,
                    ONE_HALF_HOUR,
                    TWO_HOURS,
                    TWO_HALF_HOURS,
                    THREE_HOURS,
                    FOUR_HOURS,
                    FIVE_HOURS,
                    SIX_HOURS,
                    EIGHT_HOURS,
                    TEN_HOURS,
                    TWELVE_HOURS,
                    FIFTEEN_HOURS,
                    EIGHTEEN_HOURS,
                    TWENTY_ONE_HOURS,
                    TWENTY_FOUR_HOURS
            })
    public @interface ItemTypeDef {
    }
}
