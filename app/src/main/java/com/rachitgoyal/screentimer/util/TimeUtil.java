package com.rachitgoyal.screentimer.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.rachitgoyal.screentimer.util.TimeOptions.EIGHTEEN_HOURS;
import static com.rachitgoyal.screentimer.util.TimeOptions.EIGHT_HOURS;
import static com.rachitgoyal.screentimer.util.TimeOptions.FIFTEEN_HOURS;
import static com.rachitgoyal.screentimer.util.TimeOptions.FIFTEEN_MINS;
import static com.rachitgoyal.screentimer.util.TimeOptions.FIVE_HOURS;
import static com.rachitgoyal.screentimer.util.TimeOptions.FOURTY_FIVE_MINS;
import static com.rachitgoyal.screentimer.util.TimeOptions.FOUR_HOURS;
import static com.rachitgoyal.screentimer.util.TimeOptions.ONE_HALF_HOUR;
import static com.rachitgoyal.screentimer.util.TimeOptions.ONE_HOUR;
import static com.rachitgoyal.screentimer.util.TimeOptions.SIX_HOURS;
import static com.rachitgoyal.screentimer.util.TimeOptions.TEN_HOURS;
import static com.rachitgoyal.screentimer.util.TimeOptions.THIRTY_MINS;
import static com.rachitgoyal.screentimer.util.TimeOptions.THREE_HOURS;
import static com.rachitgoyal.screentimer.util.TimeOptions.TWELVE_HOURS;
import static com.rachitgoyal.screentimer.util.TimeOptions.TWENTY_FOUR_HOURS;
import static com.rachitgoyal.screentimer.util.TimeOptions.TWENTY_ONE_HOURS;
import static com.rachitgoyal.screentimer.util.TimeOptions.TWO_HALF_HOURS;
import static com.rachitgoyal.screentimer.util.TimeOptions.TWO_HOURS;

/**
 * Created by Rachit Goyal on 15/03/18
 */

public class TimeUtil {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private static final int MINUTES_IN_AN_HOUR = 60;
    private static final int SECONDS_IN_A_MINUTE = 60;

    public static String generateTimeFromSeconds(long totalSeconds) {
        long hours;
        long minutes;
        long seconds;

        String timeString = "";

        hours = totalSeconds / MINUTES_IN_AN_HOUR / SECONDS_IN_A_MINUTE;
        minutes = (totalSeconds - (hoursToSeconds(hours)))
                / SECONDS_IN_A_MINUTE;
        seconds = totalSeconds
                - ((hoursToSeconds(hours)) + (minutesToSeconds(minutes)));

        if (hours == 0) {
            if (minutes == 0) {
                if (seconds == 1) {
                    timeString = String.valueOf(seconds).concat(" second");
                } else {
                    timeString = String.valueOf(seconds).concat(" seconds");
                }
            } else if (minutes == 1) {
                timeString = String.valueOf(minutes).concat(" minute");
            } else {
                timeString = String.valueOf(minutes).concat(" minutes");
            }
        } else {
            if (hours == 1 && minutes == 0) {
                timeString = String.valueOf(hours).concat(" hour");
            } else if (minutes == 0) {
                timeString = String.valueOf(hours).concat(" hours");
            } else {
                timeString = String.valueOf(hours).concat(":").concat(String.format(Locale.getDefault(), "%02d", minutes)).concat(" hours");
            }
        }

        return timeString;
    }

    public static String convertMillisToString(long totalSeconds) {
        long hours = totalSeconds / MINUTES_IN_AN_HOUR / SECONDS_IN_A_MINUTE;
        long minutes = (totalSeconds - (hoursToSeconds(hours)))
                / SECONDS_IN_A_MINUTE;
        long seconds = totalSeconds
                - ((hoursToSeconds(hours)) + (minutesToSeconds(minutes)));

        return String.format(Locale.getDefault(), "%02d", hours).concat(":")
                .concat(String.format(Locale.getDefault(), "%02d", minutes)).concat(":")
                .concat(String.format(Locale.getDefault(), "%02d", seconds));
    }

    private static long hoursToSeconds(long hours) {
        return hours * MINUTES_IN_AN_HOUR * SECONDS_IN_A_MINUTE;
    }

    private static long minutesToSeconds(long minutes) {
        return minutes * SECONDS_IN_A_MINUTE;
    }

    public static String getDateAsFormattedString(Date date) {
        return sdf.format(date);
    }

    public static int getMillsFromTimeOption(String timeOption) {
        switch (timeOption) {
            case "15 minutes":
                return TimeOptions.FIFTEEN_MINS;
            case "30 minutes":
                return TimeOptions.THIRTY_MINS;
            case "45 minutes":
                return TimeOptions.FOURTY_FIVE_MINS;
            case "1 hour":
                return TimeOptions.ONE_HOUR;
            case "1.5 hours":
                return TimeOptions.ONE_HALF_HOUR;
            case "2 hours":
                return TimeOptions.TWO_HOURS;
            case "2.5 hours":
                return TimeOptions.TWO_HALF_HOURS;
            case "3 hours":
                return TimeOptions.THREE_HOURS;
            case "4 hours":
                return TimeOptions.FOUR_HOURS;
            case "5 hours":
                return TimeOptions.FIVE_HOURS;
            case "6 hours":
                return TimeOptions.SIX_HOURS;
            case "8 hours":
                return TimeOptions.EIGHT_HOURS;
            case "10 hours":
                return TimeOptions.TEN_HOURS;
            case "12 hours":
                return TimeOptions.TWELVE_HOURS;
            case "15 hours":
                return TimeOptions.FIFTEEN_HOURS;
            case "18 hours":
                return TimeOptions.EIGHTEEN_HOURS;
            case "21 hours":
                return TimeOptions.TWENTY_ONE_HOURS;
            case "24 hours":
                return TimeOptions.TWENTY_FOUR_HOURS;
            default:
                return TimeOptions.THREE_HOURS;
        }
    }

    public static String getTimeOptionFromMillis(int timeOption) {
        switch (timeOption) {
            case TimeOptions.FIFTEEN_MINS:
                return "15 minutes";
            case TimeOptions.THIRTY_MINS:
                return "30 minutes";
            case TimeOptions.FOURTY_FIVE_MINS:
                return "45 minutes";
            case TimeOptions.ONE_HOUR:
                return "1 hour";
            case TimeOptions.ONE_HALF_HOUR:
                return "1.5 hours";
            case TimeOptions.TWO_HOURS:
                return "2 hours";
            case TimeOptions.TWO_HALF_HOURS:
                return "2.5 hours";
            case TimeOptions.THREE_HOURS:
                return "3 hours";
            case TimeOptions.FOUR_HOURS:
                return "4 hours";
            case TimeOptions.FIVE_HOURS:
                return "5 hours";
            case TimeOptions.SIX_HOURS:
                return "6 hours";
            case TimeOptions.EIGHT_HOURS:
                return "8 hours";
            case TimeOptions.TEN_HOURS:
                return "10 hours";
            case TimeOptions.TWELVE_HOURS:
                return "12 hours";
            case TimeOptions.FIFTEEN_HOURS:
                return "15 hours";
            case TimeOptions.EIGHTEEN_HOURS:
                return "18 hours";
            case TimeOptions.TWENTY_ONE_HOURS:
                return "21 hours";
            case TimeOptions.TWENTY_FOUR_HOURS:
                return "24 hours";
            default:
                return "3 hours";
        }
    }

    public static float getAllowedPercentageFromTimeOption(int timeOption) {
        switch (timeOption) {
            case FIFTEEN_MINS:
                return 83.33f;
            case THIRTY_MINS:
                return 83.33f;
            case FOURTY_FIVE_MINS:
                return 83.33f;
            case ONE_HOUR:
                return 83.33f;
            case ONE_HALF_HOUR:
                return 83.33f;
            case TWO_HOURS:
                return 83.33f;
            case TWO_HALF_HOURS:
                return 83.33f;
            case THREE_HOURS:
                return 60.0f;
            case FOUR_HOURS:
                return 80.0f;
            case FIVE_HOURS:
                return 83.33f;
            case SIX_HOURS:
                return 60.0f;
            case EIGHT_HOURS:
                return 80.0f;
            case TEN_HOURS:
                return 83.33f;
            case TWELVE_HOURS:
                return 80.0f;
            case FIFTEEN_HOURS:
                return 83.33f;
            case EIGHTEEN_HOURS:
                return 85.71f;
            case TWENTY_ONE_HOURS:
                return 87.5f;
            case TWENTY_FOUR_HOURS:
                return 100.0f;
            default:
                return 60.0f;
        }
    }

    public static int getScaleMaxTimeFromExceededUsedTime(long usedTime) {
        if (usedTime < THIRTY_MINS) {
            return FOURTY_FIVE_MINS;
        } else if (usedTime < FOURTY_FIVE_MINS) {
            return ONE_HOUR;
        } else if (usedTime < ONE_HOUR) {
            return ONE_HALF_HOUR;
        } else if (usedTime < ONE_HALF_HOUR) {
            return TWO_HOURS;
        } else if (usedTime < TWO_HOURS) {
            return TWO_HALF_HOURS;
        } else if (usedTime < TWO_HALF_HOURS) {
            return THREE_HOURS;
        } else if (usedTime < THREE_HOURS) {
            return FOUR_HOURS;
        } else if (usedTime < FOUR_HOURS) {
            return FIVE_HOURS;
        } else if (usedTime < FIVE_HOURS) {
            return SIX_HOURS;
        } else if (usedTime < SIX_HOURS) {
            return EIGHT_HOURS;
        } else if (usedTime < EIGHT_HOURS) {
            return TEN_HOURS;
        } else if (usedTime < TEN_HOURS) {
            return TWELVE_HOURS;
        } else if (usedTime < TWELVE_HOURS) {
            return FIFTEEN_HOURS;
        } else if (usedTime < FIFTEEN_HOURS) {
            return EIGHTEEN_HOURS;
        } else if (usedTime < EIGHTEEN_HOURS) {
            return TWENTY_ONE_HOURS;
        } else {
            return TWENTY_FOUR_HOURS;
        }
    }
}
