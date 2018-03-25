package com.rachitgoyal.screentimer.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rachitgoyal.screentimer.util.Constants;

public class ScreenStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null) {
            Intent startIntent = new Intent(context, ScreenTimerService.class);
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                startIntent.putExtra(Constants.EXTRAS.STOP_TIMER, true);
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                startIntent.putExtra(Constants.EXTRAS.START_TIMER, true);
            }
            context.startService(startIntent);
        }
    }
}
