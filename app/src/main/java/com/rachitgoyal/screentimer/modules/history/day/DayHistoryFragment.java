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

import com.rachitgoyal.screentimer.R;
import com.rachitgoyal.screentimer.model.ScreenUsage;
import com.rachitgoyal.screentimer.modules.base.BaseFragment;
import com.rachitgoyal.screentimer.service.TimeChangeBroadcastReceiver;
import com.rachitgoyal.screentimer.util.Constants;
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

    @Override
    public void setData(List<ScreenUsage> screenUsageList) {
        mScreenUsageList.clear();
        mScreenUsageList.addAll(screenUsageList);
        mAdapter.setScreenUsageList(screenUsageList);
        mAdapter.notifyDataSetChanged();
        mViewPager.scrollToPosition(mScreenUsageList.size() - 1);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
    }

    @Override
    public void setData() {
//        mPresenter.updateData();
    }

    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {
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
