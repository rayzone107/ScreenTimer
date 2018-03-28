package com.rachitgoyal.screentimer.modules.history.day;

import android.app.DatePickerDialog;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.rachitgoyal.screentimer.R;
import com.rachitgoyal.screentimer.model.ScreenUsage;
import com.rachitgoyal.screentimer.modules.base.BaseFragment;
import com.rachitgoyal.screentimer.service.TimeChangeBroadcastReceiver;
import com.rachitgoyal.screentimer.util.Constants;
import com.rachitgoyal.screentimer.util.TimeUtil;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DayHistoryFragment extends BaseFragment implements DayHistoryContract.View,
        TimeChangeBroadcastReceiver.TimeChangeListener, DiscreteScrollView.OnItemChangedListener, DatePickerDialog.OnDateSetListener {

    private TimeChangeBroadcastReceiver mTimeChangeReceiver;
    private DayHistoryContract.Presenter mPresenter;
    private List<ScreenUsage> mScreenUsageList = new ArrayList<>();

    private DayHistoryPagerAdapter mAdapter;

    @BindView(R.id.date_tv)
    TextView mDateTV;

    @BindView(R.id.emoticon_iv)
    ImageView mEmoticonIV;

    @BindView(R.id.view_pager)
    DiscreteScrollView mViewPager;

    public DayHistoryFragment() {
    }

    public static DayHistoryFragment newInstance() {
        return new DayHistoryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day_history, container, false);
        ButterKnife.bind(this, view);

        mTimeChangeReceiver = new TimeChangeBroadcastReceiver(this);
        mPresenter = new DayHistoryPresenter(this);

        mViewPager.addOnItemChangedListener(this);
        mAdapter = new DayHistoryPagerAdapter(mScreenUsageList);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setItemTransitionTimeMillis(200);
        mViewPager.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());

        mPresenter.fetchData();
        return view;
    }

    @OnClick(R.id.date_tv)
    public void onDateClicked(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, this, 23, 3, 2018);
    }

    @Override
    public void setData(List<ScreenUsage> screenUsageList) {
        mScreenUsageList.clear();
        mScreenUsageList.addAll(screenUsageList);
        mAdapter.setScreenUsageList(screenUsageList);
        mAdapter.notifyDataSetChanged();
        mViewPager.scrollToPosition(mScreenUsageList.size() - 1);
        onItemChanged(mScreenUsageList.get(mScreenUsageList.size() - 1));
    }

    @Override
    public void updateTodayData(ScreenUsage screenUsage) {
        /*mScreenUsageList.set(mScreenUsageList.size() - 1, screenUsage);
        mAdapter.notifyItemChanged(mScreenUsageList.size() - 1);*/
    }

    @Override
    public void setEmoticon(int emoticon) {
        mEmoticonIV.setImageResource(emoticon);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

    }

    private void onItemChanged(ScreenUsage screenUsage) {
        mDateTV.setText(TimeUtil.convertStringDateToFormattedString(screenUsage.getDate()));
        mPresenter.calculateForEmoticon(screenUsage);
    }

    @Override
    public void setData() {
//        mPresenter.updateData();
    }

    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {
        onItemChanged(mScreenUsageList.get(adapterPosition));
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
}
