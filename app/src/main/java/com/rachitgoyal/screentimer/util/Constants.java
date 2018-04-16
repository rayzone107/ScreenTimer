package com.rachitgoyal.screentimer.util;


import android.graphics.Color;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Rachit Goyal on 15/03/18
 */

public class Constants {

    public static final int TIME_BAR_ID = 11111;
    public static final String NOTIFICATION_CHANNEL_ID = "GAZE_AWAY";
    public static final String NOTIFICATION_CHANNEL_NAME = "Gaze Away";

    public static final List<String> timeOptions = Arrays.asList("15 minutes",
            "30 minutes",
            "45 minutes",
            "1 hour",
            "1.5 hours",
            "2 hours",
            "2.5 hours",
            "3 hours",
            "4 hours",
            "5 hours",
            "6 hours",
            "8 hours",
            "10 hours",
            "12 hours",
            "15 hours",
            "18 hours",
            "21 hours",
            "24 hours");

    public interface ACTION {
        String STARTFOREGROUND_ACTION = "com.rachitgoyal.foregroundservice.action.startforeground";
        String UPDATE_TIMER = "com.rachitgoyal.updatetimer";
    }

    public interface NOTIFICATION_ID {
        int FOREGROUND_SERVICE = 101;
        int REMINDER = 102;
    }

    public interface PREFERENCES {
        String PREFS_NAME = "ScreenTimerPreferences";
        String MAX_TIME_OPTION = "MaxTimeOption";
    }

    public interface EXTRAS {
        String START_TIMER = "StartTimer";
        String STOP_TIMER = "StopTimer";
    }
}
