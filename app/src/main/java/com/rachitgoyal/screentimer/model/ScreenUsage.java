package com.rachitgoyal.screentimer.model;

import com.orm.SugarRecord;

/**
 * Created by Rachit Goyal on 15/03/18
 */

public class ScreenUsage extends SugarRecord {

    public static final String dateField = "DATE";
    public static final String secondsUsedField = "SECONDS_USED";
    public static final String secondsAllowedField = "SECONDS_ALLOWED";

    private String date;
    private long secondsUsed;
    private long secondsAllowed;

    public ScreenUsage() {
    }

    public ScreenUsage(String date, long secondsUsed, long secondsAllowed) {
        this.date = date;
        this.secondsUsed = secondsUsed;
        this.secondsAllowed = secondsAllowed;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getSecondsUsed() {
        return secondsUsed;
    }

    public void setSecondsUsed(long secondsUsed) {
        this.secondsUsed = secondsUsed;
    }

    public long getSecondsAllowed() {
        return secondsAllowed;
    }

    public void setSecondsAllowed(long secondsAllowed) {
        this.secondsAllowed = secondsAllowed;
    }
}
