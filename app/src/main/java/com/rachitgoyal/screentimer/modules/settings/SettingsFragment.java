package com.rachitgoyal.screentimer.modules.settings;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import android.text.TextUtils;
import android.view.MenuItem;

import com.pixplicity.easyprefs.library.Prefs;
import com.rachitgoyal.screentimer.R;
import com.rachitgoyal.screentimer.modules.reminder.ReminderActivity;
import com.rachitgoyal.screentimer.modules.webview.WebViewActivity;
import com.rachitgoyal.screentimer.service.ScreenTimerService;
import com.rachitgoyal.screentimer.util.Constants;
import com.rachitgoyal.screentimer.util.TimeOptions;
import com.rachitgoyal.screentimer.util.TimeUtil;

/**
 * Created by Rachit Goyal on 17/04/18
 */

public class SettingsFragment extends PreferenceFragment implements SettingsContract.View {

    private Context mContext;
    private SettingsContract.Presenter mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();

        mPresenter = new SettingsPresenter();

        addPreferencesFromResource(R.xml.pref_general);
        setHasOptionsMenu(true);
        bindPreferenceSummaryToValue(findPreference("notifications_ringtone"));
        bindPreferenceSummaryToValue(findPreference("max_threshold"));
        handleSwitches();
        handleLists();
    }

    private void handleSwitches() {
        handleSwitch("allow_tracking", Constants.PREFERENCES.PREFS_ALLOW_TRACKING);
        handleSwitch("show_tutorial", Constants.PREFERENCES.PREFS_SHOW_TUTORIAL);
        handleSwitch("show_notifications", Constants.PREFERENCES.PREFS_SHOW_NOTIFICATIONS);
        handleSwitch("notifications_vibrate", Constants.PREFERENCES.PREFS_VIBRATE);
    }

    private void handleSwitch(String key, final String pref) {
        final SwitchPreference switchPreference = (SwitchPreference) findPreference(key);
        if (switchPreference != null) {
            switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object isTrueObject) {
                    boolean isTrue = (Boolean) isTrueObject;

                    if (pref.equals(Constants.PREFERENCES.PREFS_ALLOW_TRACKING)) {
                        if (!isTrue) {
                            AlertDialog.Builder builder;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                            } else {
                                builder = new AlertDialog.Builder(getActivity());
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
                                            mContext.sendBroadcast(new Intent(Constants.ACTION.STOP_SERVICE));
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
                            Intent startIntent = new Intent(mContext, ScreenTimerService.class);
                            startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                            mContext.startService(startIntent);
                        }
                    } else if (pref.equals(Constants.PREFERENCES.PREFS_SHOW_TUTORIAL)) {
                        Prefs.putBoolean(pref, isTrue);
                    } else if (pref.equals(Constants.PREFERENCES.PREFS_SHOW_NOTIFICATIONS)) {
                        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
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

    private void handleLists() {
        ListPreference listPreference = (ListPreference) findPreference("max_threshold");
        listPreference.setSummary(Prefs.getString(Constants.PREFERENCES.MAX_TIME_OPTION,
                TimeUtil.convertSecondsToTimeOption(TimeOptions.THREE_HOURS)));
        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object maxThresholdValue) {
                String maxThreshold = (String) maxThresholdValue;
                Prefs.putString(Constants.PREFERENCES.MAX_TIME_OPTION, maxThreshold);
                preference.setSummary(maxThreshold);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

        switch (preference.getKey()) {
            case "notifications_customize_reminders":
                startActivity(new Intent(getActivity(), ReminderActivity.class));
                break;
            case "notifications_ringtone":
                break;
            case "about_copyright":
                break;
            case "about_faqs":
                startWebViewActivity(WebViewActivity.FAQs);
                break;
            case "about_terms_of_service":
                startWebViewActivity(WebViewActivity.TERMS_AND_CONDITIONS);
                break;
            case "about_privacy_policy":
                startWebViewActivity(WebViewActivity.PRIVACY_POLICY);
                break;
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void startWebViewActivity(int type) {
        Intent intent = new Intent(mContext, WebViewActivity.class);
        intent.putExtra(Constants.EXTRAS.TYPE_OF_WEBVIEW, type);
        startActivity(intent);
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

            /*if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else */
            if (preference instanceof RingtonePreference) {
                if (TextUtils.isEmpty(stringValue)) {
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        preference.setSummary(null);
                    } else {
                        String name = ringtone.getTitle(preference.getContext());
                        Prefs.putString(Constants.PREFERENCES.PREFS_RINGTONE, stringValue);
                        preference.setSummary(name);
                    }
                }
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }
    };
}
