package com.rachitgoyal.screentimer.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StopServiceReceiver extends BroadcastReceiver {

    private StopServiceListener mListener;

    public StopServiceReceiver(StopServiceListener listener) {
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mListener.stopService();
    }

    public interface StopServiceListener {
        void stopService();
    }
}
