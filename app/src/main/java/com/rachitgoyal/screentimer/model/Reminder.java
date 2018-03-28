package com.rachitgoyal.screentimer.model;

import com.orm.SugarRecord;

/**
 * Created by Rachit Goyal on 15/03/18
 */

public class Reminder extends SugarRecord {

    public static final String secondsField = "SECONDS";
    public static final String isRecurringField = "IS_RECURRING";
    public static final String isEnabledField = "IS_ENABLED";
    public static final String isAllowedTimeReminderField = "IS_ALLOWED_TIME_REMINDER";
    public static final String isDeleteCheckedField = "IS_DELETE_CHECKED";

    private int seconds;
    private boolean isRecurring;
    private boolean isEnabled;
    private boolean isAllowedTimeReminder;
    private boolean isDeleteChecked;

    public Reminder() {
    }

    public Reminder(int seconds, boolean isRecurring) {
        this.seconds = seconds;
        this.isRecurring = isRecurring;
        this.isEnabled = true;
        this.isAllowedTimeReminder = false;
    }

    public Reminder(int seconds, boolean isRecurring, boolean isEnabled) {
        this.seconds = seconds;
        this.isRecurring = isRecurring;
        this.isEnabled = isEnabled;
        this.isAllowedTimeReminder = false;
    }

    public Reminder(int seconds, boolean isRecurring, boolean isEnabled, boolean isAllowedTimeReminder) {
        this.seconds = seconds;
        this.isRecurring = isRecurring;
        this.isEnabled = isEnabled;
        this.isAllowedTimeReminder = isAllowedTimeReminder;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public boolean isAllowedTimeReminder() {
        return isAllowedTimeReminder;
    }

    public void setAllowedTimeReminder(boolean allowedTimeReminder) {
        isAllowedTimeReminder = allowedTimeReminder;
    }

    public boolean isDeleteChecked() {
        return isDeleteChecked;
    }

    public void setDeleteChecked(boolean deleteChecked) {
        isDeleteChecked = deleteChecked;
    }
}
