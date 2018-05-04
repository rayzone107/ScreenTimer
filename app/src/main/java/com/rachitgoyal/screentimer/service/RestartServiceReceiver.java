package com.rachitgoyal.screentimer.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rachitgoyal.screentimer.util.Constants;

/**
 * Created by Rachit Goyal on 27/04/18
 */

public class RestartServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startIntent = new Intent(context, ScreenTimerService.class);
        startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        context.startService(startIntent);
    }
}
