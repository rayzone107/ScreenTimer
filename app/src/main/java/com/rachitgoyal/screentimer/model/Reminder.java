package com.rachitgoyal.screentimer.model;

import com.orm.SugarRecord;

/**
 * Created by Rachit Goyal on 15/03/18
 */

public class Reminder extends SugarRecord {

    public static final String millisField = "MILLIS";
    public static final String isRecurringField = "IS_RECURRING";
    public static final String isEnabledField = "IS_ENABLED";
    public static final String isAllowedTimeReminderField = "IS_ALLOWED_TIME_REMINDER";
    public static final String isDeleteCheckedField = "IS_DELETE_CHECKED";

    private int millis;
    private boolean isRecurring;
    private boolean isEnabled;
    private boolean isAllowedTimeReminder;
    private boolean isDeleteChecked;

    public Reminder() {
    }

    public Reminder(int millis, boolean isRecurring) {
        this.millis = millis;
        this.isRecurring = isRecurring;
        this.isEnabled = true;
        this.isAllowedTimeReminder = false;
    }

    public Reminder(int millis, boolean isRecurring, boolean isEnabled) {
        this.millis = millis;
        this.isRecurring = isRecurring;
        this.isEnabled = isEnabled;
        this.isAllowedTimeReminder = false;
    }

    public Reminder(int millis, boolean isRecurring, boolean isEnabled, boolean isAllowedTimeReminder) {
        this.millis = millis;
        this.isRecurring = isRecurring;
        this.isEnabled = isEnabled;
        this.isAllowedTimeReminder = isAllowedTimeReminder;
    }

    public int getMillis() {
        return millis;
    }

    public void setMillis(int millis) {
        this.millis = millis;
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
