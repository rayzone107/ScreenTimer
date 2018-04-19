package com.rachitgoyal.screentimer.modules.settings;

import android.app.Activity;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.SwitchPreference;

/**
 * Created by Rachit Goyal on 17/04/18
 */

public interface SettingsContract {

    interface View {

        void startService();

        void handleSwitch(String key, String pref);

        Preference findPreferenceFromKey(String key);

        void startReminderActivity();

        void startWebViewActivity(int type);

        void showNewMaxTimeSnackbar(String maxThreshold);
    }

    interface Presenter {

        void handlePreferences();

        void handleMaxThresholdChange(ListPreference listPreference);

        void handleSwitch(Activity activity, SwitchPreference preference, String pref);

        void handlePreferenceClick(String key);
    }
}
