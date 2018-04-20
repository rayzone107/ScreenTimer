package com.rachitgoyal.screentimer.modules.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import android.text.TextUtils;

import com.orm.query.Select;
import com.pixplicity.easyprefs.library.Prefs;
import com.rachitgoyal.screentimer.R;
import com.rachitgoyal.screentimer.model.Reminder;
import com.rachitgoyal.screentimer.modules.webview.WebViewActivity;
import com.rachitgoyal.screentimer.util.Constants;
import com.rachitgoyal.screentimer.util.TimeOptions;
import com.rachitgoyal.screentimer.util.TimeUtil;

import java.util.List;

/**
 * Created by Rachit Goyal on 17/04/18
 */

public class SettingsPresenter implements SettingsContract.Presenter {

    private SettingsContract.View mView;

    SettingsPresenter(SettingsContract.View view) {
        mView = view;
    }

    @Override
    public void handlePreferences() {
        handleSwitches();
        handleMaxThresholdChange((ListPreference) mView.findPreferenceFromKey(Constants.PREFERENCE_KEYS.MAX_THRESHOLD));
        bindPreferenceSummaryToValue(mView.findPreferenceFromKey(Constants.PREFERENCE_KEYS.NOTIFICATIONS_RINGTONE));
    }

    private void handleSwitches() {
        handleSwitch(Constants.PREFERENCE_KEYS.ALLOW_TRACKING, Constants.PREFERENCES.PREFS_ALLOW_TRACKING);
        handleSwitch(Constants.PREFERENCE_KEYS.SHOW_TUTORIAL, Constants.PREFERENCES.PREFS_SHOW_TUTORIAL);
        handleSwitch(Constants.PREFERENCE_KEYS.SHOW_NOTIFICATIONS, Constants.PREFERENCES.PREFS_SHOW_NOTIFICATIONS);
        handleSwitch(Constants.PREFERENCE_KEYS.NOTIFICATIONS_VIBRATE, Constants.PREFERENCES.PREFS_VIBRATE);
    }

    private void handleSwitch(String key, String pref) {
        mView.handleSwitch(key, pref);
    }

    @Override
    public void handleMaxThresholdChange(ListPreference listPreference) {
        listPreference.setSummary(Prefs.getString(Constants.PREFERENCES.MAX_TIME_OPTION,
                TimeUtil.convertSecondsToTimeOption(TimeOptions.THREE_HOURS)));
        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object maxThresholdValue) {
                String maxThreshold = (String) maxThresholdValue;
                Prefs.putString(Constants.PREFERENCES.MAX_TIME_OPTION, maxThreshold);
                preference.setSummary(maxThreshold);
                mView.showNewMaxTimeSnackbar(maxThreshold);
                setReminder(maxThreshold);
                return true;
            }
        });
    }

    private void setReminder(String maxThreshold) {
        int reminderTime = TimeUtil.convertTimeOptionToSeconds(maxThreshold);
        boolean alreadyExists = false;
        List<Reminder> reminderList = Select.from(Reminder.class).list();
        for (Reminder reminder : reminderList) {
            if (reminderTime == reminder.getSeconds() && !reminder.isRecurring()) {
                alreadyExists = true;
            }
        }
        if (!alreadyExists) {
            Reminder reminder = new Reminder(reminderTime, false);
            reminder.setAllowedTimeReminder(true);
            reminder.save();
        }
    }

    @Override
    public void handleSwitch(final Activity activity, final SwitchPreference switchPreference, final String pref) {
        if (switchPreference != null) {
            if (pref.equals(Constants.PREFERENCES.PREFS_SHOW_TUTORIAL) && !Prefs.getBoolean(pref, false)) {
                switchPreference.setChecked(false);
            }
            switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object isTrueObject) {
                    boolean isTrue = (Boolean) isTrueObject;

                    if (pref.equals(Constants.PREFERENCES.PREFS_ALLOW_TRACKING)) {
                        if (!isTrue) {
                            AlertDialog.Builder builder;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert);
                            } else {
                                builder = new AlertDialog.Builder(activity);
                            }
                            builder.setTitle("Disable Device Usage Tracking?")
                                    .setMessage("If you disable this setting, the device usage will no longer be tracked. " +
                                            "Without tracking, you could end up looking at your mobile screen for far too long, and thus damaging your eyes." +
                                            "\nAre you sure you want to disable tracking?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            Prefs.putBoolean(pref, false);
                                            activity.sendBroadcast(new Intent(Constants.ACTION.STOP_SERVICE));
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            switchPreference.setChecked(true);
                                            Prefs.putBoolean(pref, true);
                                        }
                                    })
                                    .setCancelable(false)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        } else {
                            mView.startService();

                        }
                    } else if (pref.equals(Constants.PREFERENCES.PREFS_SHOW_TUTORIAL)) {
                        Prefs.putBoolean(pref, isTrue);
                    } else if (pref.equals(Constants.PREFERENCES.PREFS_SHOW_NOTIFICATIONS)) {
                        NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
                        if (notificationManager != null) {
                            notificationManager.cancel(Constants.NOTIFICATION_ID.REMINDER);
                        }
                        Prefs.putBoolean(pref, isTrue);
                    } else if (pref.equals(Constants.PREFERENCES.PREFS_VIBRATE)) {
                        Prefs.putBoolean(pref, isTrue);
                    }
                    return true;
                }
            });
        }
    }

    @Override
    public void handlePreferenceClick(String key) {
        switch (key) {
            case Constants.PREFERENCE_KEYS.NOTIFICATIONS_CUSTOMIZE_REMINDERS:
                mView.startReminderActivity();
                break;
            case Constants.PREFERENCE_KEYS.ABOUT_FAQS:
                mView.startWebViewActivity(WebViewActivity.FAQs);
                break;
            case Constants.PREFERENCE_KEYS.ABOUT_TERMS_OF_SERVICE:
                mView.startWebViewActivity(WebViewActivity.TERMS_AND_CONDITIONS);
                break;
            case Constants.PREFERENCE_KEYS.ABOUT_PRIVACY_POLICY:
                mView.startWebViewActivity(WebViewActivity.PRIVACY_POLICY);
                break;
        }
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    private Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof RingtonePreference) {
                if (TextUtils.isEmpty(stringValue)) {
                    preference.setSummary(R.string.pref_ringtone_silent);
                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(preference.getContext(), Uri.parse(stringValue));
                    if (ringtone == null) {
                        preference.setSummary(null);
                    } else {
                        String name = ringtone.getTitle(preference.getContext());
                        Prefs.putString(Constants.PREFERENCES.PREFS_RINGTONE, stringValue);
                        preference.setSummary(name);
                    }
                }
            }
            return true;
        }
    };

}
