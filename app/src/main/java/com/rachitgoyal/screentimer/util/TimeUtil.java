package com.rachitgoyal.screentimer.util;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;

import com.rachitgoyal.screentimer.model.ScreenUsage;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
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

    public static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private static final int MINUTES_IN_AN_HOUR = 60;
    private static final int SECONDS_IN_A_MINUTE = 60;

    public static String convertSecondsToApproximateTimeString(long totalSeconds) {
        long hours;
        long minutes;
        long seconds;

        String timeString = "";

        hours = totalSeconds / MINUTES_IN_AN_HOUR / SECONDS_IN_A_MINUTE;
        minutes = (totalSeconds - (hoursToSeconds(hours)))
                / SECONDS_IN_A_MINUTE;
        seconds = totalSeconds
                - ((hoursToSeconds(hours)) + (minutesToSeconds(minutes)));

        timeString = hours == 0 ? minutes == 0 ? seconds == 1 ? String.valueOf(seconds).concat(" second") : String.valueOf(seconds).concat(" seconds") :
                minutes == 1 ? String.valueOf(minutes).concat(" minute") : String.valueOf(minutes).concat(" minutes") :
                hours == 1 && minutes == 0 ? String.valueOf(hours).concat(" hour") : minutes == 0 ? String.valueOf(hours).concat(" hours") :
                        String.valueOf(hours).concat(":").concat(String.format(Locale.getDefault(), "%02d", minutes)).concat(" hours");

        /* EXPLANATION FOR ABOVE CODE
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
        }*/

        return timeString;
    }

    public static String convertSecondsToNotificationTimeString(long totalSeconds) {
        long hours;
        long minutes;
        long seconds;

        String timeString = "";

        hours = totalSeconds / MINUTES_IN_AN_HOUR / SECONDS_IN_A_MINUTE;
        minutes = (totalSeconds - (hoursToSeconds(hours)))
                / SECONDS_IN_A_MINUTE;
        seconds = totalSeconds
                - ((hoursToSeconds(hours)) + (minutesToSeconds(minutes)));

        timeString = hours == 0 ? minutes == 0 ? seconds == 1 ? String.valueOf(seconds).concat(" second") : String.valueOf(seconds).concat(" seconds") :
                minutes == 1 ? String.valueOf(minutes).concat(" minute") : String.valueOf(minutes).concat(" minutes") :
                minutes == 0 ? String.valueOf(hours).concat(hours == 1 ? " hour" : " hours") :
                        minutes == 1 ? String.valueOf(hours).concat(hours == 1 ? " hour and " : " hours and ")
                                .concat(String.valueOf(minutes)).concat(" minute") : String.valueOf(hours).concat(hours == 1 ? " hour and " : " hours and ")
                                .concat(String.valueOf(minutes)).concat(" minutes");

        /* EXPLANATION OF ABOVE CODE
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
            if (minutes == 0) {
                timeString = String.valueOf(hours).concat(hours == 1 ? " hour" : " hours");
            } else if (minutes == 1) {
                timeString = String.valueOf(hours).concat(hours == 1 ? " hour and " : " hours and ")
                        .concat(String.valueOf(minutes)).concat(" minute");
            } else {
                timeString = String.valueOf(hours).concat(hours == 1 ? " hour and " : " hours and ")
                        .concat(String.valueOf(minutes)).concat(" minutes");
            }
        }*/

        return timeString;
    }

    public static String convertSecondsToExactTimeString(long totalSeconds) {
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

    public static int convertTimeOptionToSeconds(String timeOption) {
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

    public static String convertSecondsToTimeOption(int seconds) {
        switch (seconds) {
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

    public static int calculateGrayedTimeFromTimeOption(int timeOption) {
        switch (timeOption) {
            case FIFTEEN_MINS:
                return THIRTY_MINS;
            case THIRTY_MINS:
                return FOURTY_FIVE_MINS;
            case FOURTY_FIVE_MINS:
                return ONE_HOUR;
            case ONE_HOUR:
                return ONE_HALF_HOUR;
            case ONE_HALF_HOUR:
                return TWO_HOURS;
            case TWO_HOURS:
                return TWO_HALF_HOURS;
            case TWO_HALF_HOURS:
                return THREE_HOURS;
            case THREE_HOURS:
                return FOUR_HOURS;
            case FOUR_HOURS:
                return FIVE_HOURS;
            case FIVE_HOURS:
                return SIX_HOURS;
            case SIX_HOURS:
                return EIGHT_HOURS;
            case EIGHT_HOURS:
                return TEN_HOURS;
            case TEN_HOURS:
                return TWELVE_HOURS;
            case TWELVE_HOURS:
                return FIFTEEN_HOURS;
            case FIFTEEN_HOURS:
                return EIGHTEEN_HOURS;
            case EIGHTEEN_HOURS:
                return TWENTY_ONE_HOURS;
            case TWENTY_ONE_HOURS:
                return TWENTY_FOUR_HOURS;
            case TWENTY_FOUR_HOURS:
                return TWENTY_FOUR_HOURS;
            default:
                return FOUR_HOURS;
        }
    }

    public static int convertHourMinToSeconds(int hour, int mins) {
        return (hour * 3600) + (mins * 60);
    }

    public static String convertSecondsToHourMins(int seconds) {
        String time = "";
        int hours = seconds / 3600;
        int remainder = seconds - hours * 3600;
        int mins = remainder / 60;

        time = hours == 0 ? mins == 1 ? "1 minute" : mins + " minutes" :
                hours == 1 ? mins == 0 ? "1 hour" : hours + ":" + String.format(Locale.getDefault(), "%02d", mins) :
                        mins == 0 ? hours + " hours" : hours + ":" + String.format(Locale.getDefault(), "%02d", mins);

        /* EXPLANATION FOR ABOVE CODE
        if (hours == 0) {
            if (mins == 1) {
                time = "1 minute";
            } else {
                time = mins + " minutes";
            }
        } else if (hours == 1) {
            if (mins == 0) {
                time = "1 hour";
            } else {
                time = hours + ":" + String.format(Locale.getDefault(), "%02d", mins);
            }
        } else {
            if (mins == 0) {
                time = hours + " hours";
            } else {
                time = hours + ":" + String.format(Locale.getDefault(), "%02d", mins);
            }
        }*/

        return time;
    }

    public static SpannableString convertStringDateToFormattedString(String stringDate) {

        DateTimeFormatter fromFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");
        DateTimeFormatter toFormatter = DateTimeFormat.forPattern("dd MMM, yyyy");

        try {
            DateTime setDate = fromFormatter.parseDateTime(stringDate);
            DateTime today = DateTime.now();
            DateTime yesterday = DateTime.now().minusDays(1);

            if (setDate.toLocalDate().equals(today.toLocalDate())) {
                SpannableString todaySpan = new SpannableString("Today");
                todaySpan.setSpan(new RelativeSizeSpan(1.3f), 0, todaySpan.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                return todaySpan;
            } else if (setDate.toLocalDate().equals(yesterday.toLocalDate())) {
                SpannableString todaySpan = new SpannableString("Yesterday");
                todaySpan.setSpan(new RelativeSizeSpan(1.3f), 0, todaySpan.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                return todaySpan;
            } else {
                String formattedDate = toFormatter.print(setDate);
                formattedDate = formattedDate.substring(0, 2) + getDayOfMonthSuffix(setDate.getDayOfMonth()) +
                        formattedDate.substring(2, formattedDate.length());

                SpannableString spannableString = new SpannableString(formattedDate);
                spannableString.setSpan(new SuperscriptSpan(), 2, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new RelativeSizeSpan(0.6f), 2, 4, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                return spannableString;
            }
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String getDayOfMonthSuffix(final int n) {
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    public static class StringDateComparator implements Comparator<ScreenUsage> {

        @Override
        public int compare(ScreenUsage screenUsage1, ScreenUsage screenUsage2) {
            try {
                return TimeUtil.sdf.parse(screenUsage1.getDate()).compareTo(TimeUtil.sdf.parse(screenUsage2.getDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }
}
