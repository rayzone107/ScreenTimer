package com.rachitgoyal.screentimer.modules.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.widget.Toast;

import com.rachitgoyal.screentimer.R;
import com.rachitgoyal.screentimer.modules.reminder.ReminderActivity;
import com.rachitgoyal.screentimer.modules.webview.WebViewActivity;
import com.rachitgoyal.screentimer.service.ScreenTimerService;
import com.rachitgoyal.screentimer.util.Constants;

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

        mPresenter = new SettingsPresenter(this);
        addPreferencesFromResource(R.xml.pref_general);
        setHasOptionsMenu(true);
        mPresenter.handlePreferences();
    }

    @Override
    public void handleSwitch(String key, final String pref) {
        mPresenter.handleSwitch(getActivity(), (SwitchPreference) findPreference(key), pref);
    }

    @Override
    public Preference findPreferenceFromKey(String key) {
        return findPreference(key);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        mPresenter.handlePreferenceClick(preference.getKey());
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void startReminderActivity() {
        startActivity(new Intent(getActivity(), ReminderActivity.class));
    }

    @Override
    public void startService() {
        Intent startIntent = new Intent(mContext, ScreenTimerService.class);
        startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        mContext.startService(startIntent);
    }

    @Override
    public void startWebViewActivity(int type) {
        Intent intent = new Intent(mContext, WebViewActivity.class);
        intent.putExtra(Constants.EXTRAS.TYPE_OF_WEBVIEW, type);
        startActivity(intent);
    }

    @Override
    public void showNewMaxTimeSnackbar(String maxThreshold) {
        Toast.makeText(mContext, "A new reminder has been added for " + maxThreshold, Toast.LENGTH_SHORT).show();
    }
}
