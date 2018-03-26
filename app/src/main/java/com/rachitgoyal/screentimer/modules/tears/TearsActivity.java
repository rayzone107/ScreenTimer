package com.rachitgoyal.screentimer.modules.tears;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rachitgoyal.screentimer.R;
import com.rachitgoyal.screentimer.modules.base.BaseActivity;
import com.rachitgoyal.screentimer.modules.history.HistoryActivity;
import com.rachitgoyal.screentimer.modules.reminder.ReminderActivity;
import com.rachitgoyal.screentimer.modules.settings.SettingsActivity;
import com.rachitgoyal.screentimer.service.TimeChangeBroadcastReceiver;
import com.rachitgoyal.screentimer.util.Constants;
import com.rachitgoyal.screentimer.libraries.wave.WaveView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TearsActivity extends BaseActivity implements TearsContract.View, TimeChangeBroadcastReceiver.TimeChangeListener {

    private TearsContract.Presenter mPresenter;
    private TimeChangeBroadcastReceiver mTimeChangeReceiver;

    @BindView(R.id.logo_iv)
    ImageView mLogoIV;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.parent_rl)
    RelativeLayout mParentRL;

    @BindView(R.id.eye_iv)
    ImageView mEye;

    @BindView(R.id.eye_overlay_iv)
    ImageView mEyeOverlay;

    @BindView(R.id.wave_view)
    WaveView mWave;

    @BindView(R.id.bar_container_ll)
    LinearLayout mBarContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tears);
        ButterKnife.bind(this);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);

        mTimeChangeReceiver = new TimeChangeBroadcastReceiver(this);
        mPresenter = new TearsPresenter(this, getSharedPreferences(Constants.PREFERENCES.PREFS_NAME, MODE_PRIVATE));
        mPresenter.setData(mContext);
        mWave.setCenterTitleSize(30);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mTimeChangeReceiver, new IntentFilter(Constants.ACTION.UPDATE_TIMER));
    }

    @Override
    public void createAllowedTimeBar(View allowedTimeBar) {
        View view = mParentRL.findViewById(Constants.TIME_BAR_ID);
        if (view != null) {
            mParentRL.removeView(view);
        }
        mParentRL.addView(allowedTimeBar);
    }

    @Override
    public void addTearDrop(ImageView teardropIV) {
        mParentRL.addView(teardropIV);
    }

    @Override
    public void removeTearDrop(ImageView teardropIV) {
        mParentRL.removeView(teardropIV);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mTimeChangeReceiver != null) {
            unregisterReceiver(mTimeChangeReceiver);
        }
    }

    public void setData(float progress, int waveColor, String timeValue, float eyeOverlayOpacity) {
        mWave.setProgressValue(progress);
        mWave.setWaveColor(waveColor);
        mWave.setCenterTitle(timeValue);
        if (progress > 55) {
            mWave.setCenterTitleColor(Color.WHITE);
        }
        mEyeOverlay.setAlpha(eyeOverlayOpacity);
        if (mWave.getMeasuredHeight() > 0) {
            mPresenter.showAllowedTimeBar(mContext, mWave.getMeasuredHeight(), mParentRL.getMeasuredHeight());
            mPresenter.animateTearDrops(mContext, mEye.getX(), mEye.getY(), mEye.getMeasuredHeight(), mEye.getMeasuredWidth(), mWave.getHeight(),
                    mParentRL.getHeight());
        }
    }

    @Override
    public void setScale(List<TextView> bars) {
        mBarContainer.removeAllViews();
        for (int i = bars.size() - 1; i >= 0; i--) {
            mBarContainer.addView(bars.get(i));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tears, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reminders:
                startActivity(new Intent(mContext, ReminderActivity.class));
                break;
            case R.id.action_history:
                startActivity(new Intent(mContext, HistoryActivity.class));
                break;
            case R.id.action_settings:
                startActivity(new Intent(mContext, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setData() {
        mPresenter.setData(this);
    }
}
