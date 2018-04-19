package com.rachitgoyal.screentimer.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

import com.orm.query.Condition;
import com.orm.query.Select;
import com.pixplicity.easyprefs.library.Prefs;
import com.rachitgoyal.screentimer.R;
import com.rachitgoyal.screentimer.model.Reminder;
import com.rachitgoyal.screentimer.model.ScreenUsage;
import com.rachitgoyal.screentimer.util.Constants;
import com.rachitgoyal.screentimer.util.TimeUtil;

import java.util.Date;
import java.util.List;

public class ScreenTimerService extends Service implements StopServiceReceiver.StopServiceListener {

    private BroadcastReceiver mScreenStateReceiver;
    private StopServiceReceiver mStopServiceReceiver;
    private Handler mHandler;
    private Runnable mRunnable;
    private Notification.Builder mNotificationBuilder;
    private NotificationManager mNotificationManager;
    private String mNotificationTime = "";

    public ScreenTimerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.getAction() != null && intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
                PackageManager pm = getPackageManager();
                Intent launchIntent = pm.getLaunchIntentForPackage("com.rachitgoyal.screentimer");
                PendingIntent contentIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);

                mNotificationBuilder = new Notification.Builder(this)
                        .setContentTitle("Today's Usage: ")
                        .setContentIntent(contentIntent)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setPriority(Notification.PRIORITY_MIN)
                        .setOngoing(true);

                mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID,
                            Constants.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_MIN);
                    if (mNotificationManager != null) {
                        mNotificationManager.createNotificationChannel(channel);
                    }

                    mNotificationBuilder.setChannelId(Constants.NOTIFICATION_CHANNEL_ID);
                }

                startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                        mNotificationBuilder.build());

                mScreenStateReceiver = new ScreenStateReceiver();
                IntentFilter screenStateFilter = new IntentFilter();
                screenStateFilter.addAction(Intent.ACTION_SCREEN_ON);
                screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
                registerReceiver(mScreenStateReceiver, screenStateFilter);

                mStopServiceReceiver = new StopServiceReceiver(this);
                registerReceiver(mStopServiceReceiver, new IntentFilter(Constants.ACTION.STOP_SERVICE));

                restartTimer();
            } else if (intent.hasExtra(Constants.EXTRAS.START_TIMER)) {
                restartTimer();
            } else if (intent.hasExtra(Constants.EXTRAS.STOP_TIMER)) {
                pauseTimer();
            }
        }
        return START_STICKY;
    }

    @Override
    public void stopService() {
        this.stopSelf();
    }

    private void restartTimer() {
        if (mHandler == null) {
            mHandler = new Handler();
        } else {
            mHandler.removeCallbacks(mRunnable);
        }
        mRunnable = new Runnable() {
            @Override
            public void run() {
                mHandler.postDelayed(this, 1000);
                updateDatabase();
                sendBroadcastToActivity();
                updateNotification();
            }
        };
        mHandler.postDelayed(mRunnable, 1000);
    }

    private void pauseTimer() {
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }

    private void updateDatabase() {

        String selectedTime = Prefs.getString(Constants.PREFERENCES.MAX_TIME_OPTION, "");

        List<ScreenUsage> screenUsageList = Select.from(ScreenUsage.class)
                .where(Condition.prop(ScreenUsage.dateField).eq(TimeUtil.getDateAsFormattedString(new Date()))).
                        limit("1").list();
        ScreenUsage screenUsage;
        if (screenUsageList.isEmpty()) {
            screenUsage = new ScreenUsage(TimeUtil.getDateAsFormattedString(new Date()), 0, TimeUtil.convertTimeOptionToSeconds(selectedTime));
        } else {
            screenUsage = screenUsageList.get(0);
            screenUsage.setSecondsUsed(screenUsage.getSecondsUsed() + 1);
            screenUsage.setSecondsAllowed(TimeUtil.convertTimeOptionToSeconds(selectedTime));
        }
        screenUsage.save();

        checkIfReminderNeeded(screenUsage);
    }

    private void checkIfReminderNeeded(ScreenUsage screenUsage) {
        List<Reminder> reminderList = Select.from(Reminder.class).list();

        for (Reminder reminder : reminderList) {
            if (reminder.isEnabled()) {
                if (!reminder.isRecurring() && reminder.getSeconds() == screenUsage.getSecondsUsed()) {
                    sendReminder(screenUsage);
                    break;
                } else if (reminder.isRecurring() && (float) screenUsage.getSecondsUsed() % (float) reminder.getSeconds() == 0) {
                    sendReminder(screenUsage);
                }
            }
        }
    }

    private void sendReminder(ScreenUsage screenUsage) {
        if (Prefs.getBoolean(Constants.PREFERENCES.PREFS_SHOW_NOTIFICATIONS, true)) {
            Uri soundUri = Prefs.contains(Constants.PREFERENCES.PREFS_RINGTONE) ?
                    Uri.parse(Prefs.getString(Constants.PREFERENCES.PREFS_RINGTONE, "")) :
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            PackageManager pm = getPackageManager();
            Intent launchIntent = pm.getLaunchIntentForPackage("com.rachitgoyal.screentimer");
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);

            String title, message;
            boolean exceededLimit = screenUsage.getSecondsUsed() > screenUsage.getSecondsAllowed();
            if (exceededLimit) {
                title = "You've exceeded your daily limit";
                message = "Looking at screens for too long is really harmful for your eyes. " +
                        "You've already exceeded the maximum time limit. You really should stop now.";
            } else {
                title = "Protect your eyes";
                message = "You've used your device over " +
                        TimeUtil.convertSecondsToApproximateTimeString(screenUsage.getSecondsUsed()) +
                        ". You should rest your eyes.";
            }

            Notification.Builder notificationBuilder = new Notification.Builder(this)
                    .setContentTitle(title)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentIntent(contentIntent)
                    .setSound(soundUri)
                    .setAutoCancel(true)
                    .setOngoing(false);

            if (!Prefs.getBoolean(Constants.PREFERENCES.PREFS_VIBRATE, true)) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID_REMINDER,
                            Constants.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                    if (mNotificationManager != null) {
                        mNotificationManager.createNotificationChannel(channel);
                    }
                    channel.enableVibration(false);
                    notificationBuilder.setChannelId(Constants.NOTIFICATION_CHANNEL_ID_REMINDER);
                } else {
                    notificationBuilder.setVibrate(null);
                }
            } else {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID_REMINDER,
                            Constants.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                    if (mNotificationManager != null) {
                        mNotificationManager.createNotificationChannel(channel);
                    }
                    channel.enableVibration(true);
                    channel.setVibrationPattern(new long[]{1000, 1000});
                    notificationBuilder.setChannelId(Constants.NOTIFICATION_CHANNEL_ID_REMINDER);
                } else {
                    notificationBuilder.setVibrate(new long[]{1000, 1000});
                }
            }

            Notification notification = new Notification.BigTextStyle(notificationBuilder).bigText(message).build();
            if (mNotificationManager != null) {
                mNotificationManager.notify(Constants.NOTIFICATION_ID.REMINDER, notification);
            }
        }
    }

    private void sendBroadcastToActivity() {
        sendBroadcast(new Intent(Constants.ACTION.UPDATE_TIMER));
    }

    private void updateNotification() {

        List<ScreenUsage> screenUsageList = Select.from(ScreenUsage.class)
                .where(Condition.prop(ScreenUsage.dateField).eq(TimeUtil.getDateAsFormattedString(new Date()))).
                        limit("1").list();
        ScreenUsage screenUsage = screenUsageList.get(0);
        String notificationTime = TimeUtil.convertSecondsToNotificationTimeString(screenUsage.getSecondsUsed());
        if (!notificationTime.equals(mNotificationTime)) {
            mNotificationTime = notificationTime;
            mNotificationBuilder.setContentTitle("Today's Usage: " + mNotificationTime);
            if (screenUsage.getSecondsUsed() > screenUsage.getSecondsAllowed()) {
                long timeExceeded = screenUsage.getSecondsUsed() - screenUsage.getSecondsAllowed();
                String exceededTime = TimeUtil.convertSecondsToApproximateTimeString(timeExceeded);
                String message = "You've exceeded usage by " + exceededTime + ".";
                mNotificationBuilder.setContentText(message);
            } else {
                mNotificationBuilder.setContentText(null);
            }
            mNotificationManager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, mNotificationBuilder.build());
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mScreenStateReceiver != null) {
            unregisterReceiver(mScreenStateReceiver);
        }
        if (mStopServiceReceiver != null) {
            unregisterReceiver(mStopServiceReceiver);
        }
    }
}
