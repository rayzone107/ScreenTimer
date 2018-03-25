package com.rachitgoyal.screentimer.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Rachit Goyal on 25/03/18
 */

public class TimeChangeBroadcastReceiver extends BroadcastReceiver {

    private TimeChangeListener mListener;

    public TimeChangeBroadcastReceiver(TimeChangeListener listener) {
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mListener.setData();
    }

    public interface TimeChangeListener {
        void setData();
    }
}
