package com.rachitgoyal.screentimer.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rachitgoyal.screentimer.util.Constants;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent startIntent = new Intent(context, ScreenTimerService.class);
            startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
            context.startService(startIntent);
        }
    }
}
