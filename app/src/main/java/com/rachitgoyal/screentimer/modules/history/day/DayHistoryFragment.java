package com.rachitgoyal.screentimer.modules.history.day;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.PieData;
import com.rachitgoyal.screentimer.R;
import com.rachitgoyal.screentimer.modules.base.BaseFragment;
import com.rachitgoyal.screentimer.service.TimeChangeBroadcastReceiver;
import com.rachitgoyal.screentimer.util.Constants;
import com.rachitgoyal.screentimer.util.CustomTypefaceSpan;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;

public class DayHistoryFragment extends BaseFragment implements DayHistoryContract.View, TimeChangeBroadcastReceiver.TimeChangeListener {

    private TimeChangeBroadcastReceiver mTimeChangeReceiver;
    private DayHistoryContract.Presenter mPresenter;
    private SharedPreferences mPrefs;

    @BindView(R.id.time_tv)
    TextView mTimeTV;

    @BindView(R.id.timer_chart)
    PieChart mChart;

    public DayHistoryFragment() {
    }

    public static DayHistoryFragment newInstance() {
        return new DayHistoryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day_history, container, false);
        ButterKnife.bind(this, view);
        mTimeChangeReceiver = new TimeChangeBroadcastReceiver(this);

        mPrefs = mContext.getSharedPreferences(Constants.PREFERENCES.PREFS_NAME, MODE_PRIVATE);

        mPresenter = new DayHistoryPresenter(this, mPrefs);
        mPresenter.setupChart(mChart);
        mPresenter.setData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mContext.registerReceiver(mTimeChangeReceiver, new IntentFilter(Constants.ACTION.UPDATE_TIMER));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mTimeChangeReceiver != null) {
            mContext.unregisterReceiver(mTimeChangeReceiver);
        }
    }

    @Override
    public void setData(PieData data) {
        mChart.setData(data);
        mChart.highlightValues(null);
        mChart.invalidate();
    }

    @Override
    public void setCenterText(SpannableString centerText, String timeUsed) {
        centerText.setSpan(new CustomTypefaceSpan(Typeface.createFromAsset(mContext.getAssets(), "fonts/OpenSans-BoldItalic.ttf")),
                0, centerText.length(), 0);
        mTimeTV.setText(timeUsed);
        mChart.setCenterText(centerText);
    }

    @Override
    public void setAngle(float angle) {
        mChart.setRotationAngle(angle);
    }

    @Override
    public void setLegend(List<LegendEntry> legendEntries) {
        mChart.getLegend().setCustom(legendEntries);
    }

    @Override
    public void setLabelColor(int color) {
        mChart.setEntryLabelColor(color);
    }

    @Override
    public void setData() {
        mPresenter.setData();
    }
}
