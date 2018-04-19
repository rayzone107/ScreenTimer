package com.rachitgoyal.screentimer.modules.tears;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pixplicity.easyprefs.library.Prefs;
import com.rachitgoyal.screentimer.R;
import com.rachitgoyal.screentimer.libraries.reside_menu.ResideMenu;
import com.rachitgoyal.screentimer.libraries.reside_menu.ResideMenuItem;
import com.rachitgoyal.screentimer.libraries.wave.WaveView;
import com.rachitgoyal.screentimer.modules.base.BaseActivity;
import com.rachitgoyal.screentimer.modules.history.HistoryActivity;
import com.rachitgoyal.screentimer.modules.reminder.ReminderActivity;
import com.rachitgoyal.screentimer.modules.settings.SettingsActivity;
import com.rachitgoyal.screentimer.modules.settings.SettingsFragment;
import com.rachitgoyal.screentimer.service.ScreenTimerService;
import com.rachitgoyal.screentimer.service.TimeChangeBroadcastReceiver;
import com.rachitgoyal.screentimer.util.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TearsActivity extends BaseActivity implements TearsContract.View,
        TimeChangeBroadcastReceiver.TimeChangeListener, View.OnClickListener {

    private TearsContract.Presenter mPresenter;
    private TimeChangeBroadcastReceiver mTimeChangeReceiver;
    private ResideMenuItem mItemReminders;
    private ResideMenuItem mItemHistory;
    private ResideMenuItem mItemSettings;
    private ResideMenu mResideMenu;

    @BindView(R.id.hamburger_iv)
    ImageView mHamburgerIV;

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
        setResideMenu();

        if (Prefs.getBoolean(Constants.PREFERENCES.PREFS_ALLOW_TRACKING, true)) {
            Intent startIntent = new Intent(this, ScreenTimerService.class);
            startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
            startService(startIntent);
        }

        mTimeChangeReceiver = new TimeChangeBroadcastReceiver(this);
        mPresenter = new TearsPresenter(this);
        mPresenter.setData(mContext);

        mWave.setCenterTitleSize(30);
    }

    private void setResideMenu() {
        mResideMenu = new ResideMenu(this);
        mResideMenu.attachToActivity(this);
        mResideMenu.setBackground(R.drawable.tree_bg);
        mResideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
        mResideMenu.setShadowVisible(true);
        mResideMenu.setScaleValue(0.6f);

        mItemReminders = new ResideMenuItem(this, R.drawable.ic_reminder, "Reminders", 20);
        mItemHistory = new ResideMenuItem(this, R.drawable.ic_history, "History", 20);
        mItemSettings = new ResideMenuItem(this, R.drawable.ic_settings, "Settings", 20);

        mItemReminders.setOnClickListener(this);
        mResideMenu.addMenuItem(mItemReminders, ResideMenu.DIRECTION_LEFT);
        mItemHistory.setOnClickListener(this);
        mResideMenu.addMenuItem(mItemHistory, ResideMenu.DIRECTION_LEFT);
        mItemSettings.setOnClickListener(this);
        mResideMenu.addMenuItem(mItemSettings, ResideMenu.DIRECTION_LEFT);

        mHamburgerIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mResideMenu != null && !mResideMenu.isOpened()) {
                    mResideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == mItemReminders) {
            startActivity(new Intent(mContext, ReminderActivity.class));
        } else if (view == mItemHistory) {
            startActivity(new Intent(mContext, HistoryActivity.class));
        } else if (view == mItemSettings) {
            Intent intent = new Intent(mContext, SettingsActivity.class);
            intent.putExtra(SettingsActivity.EXTRA_SHOW_FRAGMENT, SettingsFragment.class.getName());
            intent.putExtra(SettingsActivity.EXTRA_NO_HEADERS, true);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mTimeChangeReceiver, new IntentFilter(Constants.ACTION.UPDATE_TIMER));

        if (mResideMenu != null && mResideMenu.isOpened()) {
            mResideMenu.closeMenu();
        }
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

    @Override
    public void setData(float progress, int waveColor, String timeValue, float eyeOverlayOpacity) {
        mWave.setProgressValue(progress);
        mWave.setWaveColor(waveColor);
        mWave.setCenterTitle(timeValue);
        mWave.setCenterTitleSize(20f);
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
    public void setData() {
        mPresenter.setData(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return mResideMenu.dispatchTouchEvent(ev);
    }
}
