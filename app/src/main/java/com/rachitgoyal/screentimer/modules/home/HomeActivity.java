package com.rachitgoyal.screentimer.modules.home;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.rachitgoyal.screentimer.R;
import com.rachitgoyal.screentimer.modules.base.BaseActivity;
import com.rachitgoyal.screentimer.modules.tears.TearsActivity;
import com.rachitgoyal.screentimer.service.ScreenTimerService;
import com.rachitgoyal.screentimer.service.TimeChangeBroadcastReceiver;
import com.rachitgoyal.screentimer.util.Constants;

import java.lang.reflect.Field;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends BaseActivity
        implements HomeContract.View, AdapterView.OnItemSelectedListener, TimeChangeBroadcastReceiver.TimeChangeListener {

    private final int DEFAULT_SPINNER_POSITION = 7;

    private HomeContract.Presenter mPresenter;
    private TimeChangeBroadcastReceiver mTimeChangeReceiver;
    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mEditor;

    @BindView(R.id.max_time_spinner)
    Spinner mMaxTimeSpinner;

    @BindView(R.id.timer_chart)
    PieChart mChart;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.fab)
    FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        mPrefs = getSharedPreferences(Constants.PREFERENCES.PREFS_NAME, MODE_PRIVATE);
        mEditor = mPrefs.edit();

        mPresenter = new HomePresenter(this, mPrefs);
        mPresenter.setupChart(mChart);
        mPresenter.setData();

        mTimeChangeReceiver = new TimeChangeBroadcastReceiver(this);
        setSpinner();

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, TearsActivity.class));
            }
        });
    }

    private void setSpinner() {

        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(mMaxTimeSpinner);
            popupWindow.setHeight(500);
        } catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException ignored) {
        }

        DurationSelectorSpinnerAdapter adapter = new DurationSelectorSpinnerAdapter(mContext);
        mMaxTimeSpinner.setAdapter(adapter);
        mMaxTimeSpinner.setOnItemSelectedListener(this);

        String selectedTime = mPrefs.getString(Constants.PREFERENCES.MAX_TIME_OPTION, "");
        if (selectedTime.isEmpty()) {
            mMaxTimeSpinner.setSelection(DEFAULT_SPINNER_POSITION);
            mEditor.putString(Constants.PREFERENCES.MAX_TIME_OPTION, Constants.timeOptions.get(DEFAULT_SPINNER_POSITION));
            mEditor.apply();
        } else {
            mMaxTimeSpinner.setSelection(Constants.timeOptions.indexOf(selectedTime));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mTimeChangeReceiver, new IntentFilter(Constants.ACTION.UPDATE_TIMER));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mTimeChangeReceiver != null) {
            unregisterReceiver(mTimeChangeReceiver);
        }
    }

    @Override
    public void setData(PieData data) {
        mChart.setData(data);
        mChart.highlightValues(null);
        mChart.invalidate();
    }

    @Override
    public void setCenterText(SpannableString centerText) {
        mChart.setCenterText(centerText);
    }

    @Override
    public void setAngle(float angle) {
        mChart.setRotationAngle(angle);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        mPresenter.handleMaxTimeChange();
        mEditor.putString(Constants.PREFERENCES.MAX_TIME_OPTION, Constants.timeOptions.get(position));
        mEditor.apply();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void setData() {
        mPresenter.setData();
    }
}
