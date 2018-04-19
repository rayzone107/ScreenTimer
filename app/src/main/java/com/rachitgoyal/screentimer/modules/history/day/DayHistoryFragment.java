package com.rachitgoyal.screentimer.modules.history.day;

import android.animation.Animator;
import android.app.DatePickerDialog;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.florent37.arclayout.ArcLayout;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.rachitgoyal.screentimer.R;
import com.rachitgoyal.screentimer.model.ScreenUsage;
import com.rachitgoyal.screentimer.modules.base.BaseFragment;
import com.rachitgoyal.screentimer.service.TimeChangeBroadcastReceiver;
import com.rachitgoyal.screentimer.util.Constants;
import com.rachitgoyal.screentimer.util.TimeUtil;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DayHistoryFragment extends BaseFragment implements DayHistoryContract.View,
        TimeChangeBroadcastReceiver.TimeChangeListener, DatePickerDialog.OnDateSetListener {

    private TimeChangeBroadcastReceiver mTimeChangeReceiver;
    private DayHistoryContract.Presenter mPresenter;
    private DatePickerDialog mDatePickerDialog;

    @BindView(R.id.arc_top)
    ArcLayout mArcTop;

    @BindView(R.id.arc_bottom)
    ArcLayout mArcBottom;

    @BindView(R.id.date_tv)
    TextView mDateTV;

    @BindView(R.id.date_container)
    LinearLayout mDateContainer;

    @BindView(R.id.emoticon_iv)
    ImageView mEmoticonIV;

    @BindView(R.id.timer_container)
    FrameLayout mTimerContainer;

    @BindView(R.id.timer_chart)
    PieChart mPieChart;

    @BindView(R.id.left_arrow_iv)
    ImageView mLeftArrow;

    @BindView(R.id.right_arrow_iv)
    ImageView mRightArrow;

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

        mDatePickerDialog = new DatePickerDialog(mContext, this, Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));


        long minDate = mPresenter.getMinDateForDatePicker();
        if (minDate != 0) {
            mDatePickerDialog.getDatePicker().setMinDate(minDate);
        }
        mDatePickerDialog.getDatePicker().setMaxDate((new Date()).getTime());

        mDateContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatePickerDialog.show();
            }
        });

        mLeftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.handleLeftClick();
            }
        });

        mRightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.handleRightClick();
            }
        });

        mPresenter.setupChart(mPieChart);
        mPresenter.fetchTodayData();
        return view;
    }

    @Override
    public void setPieData(final PieData data, final ScreenUsage screenUsage, DayHistoryPresenter.PieChange pieChange) {

        switch (pieChange) {
            case FIRST_LAUNCH:
                setPieDateWithoutAnimation(data, screenUsage);
                setRightArrowVisibility(false);
                break;
            case DATE_SELECTION:
                animateValues(data, screenUsage, Techniques.ZoomIn, Techniques.ZoomOut);
                break;
            case LEFT_SLIDE:
                animateValues(data, screenUsage, Techniques.SlideInLeft, Techniques.SlideOutRight);
                break;
            case RIGHT_SLIDE:
                animateValues(data, screenUsage, Techniques.SlideInRight, Techniques.SlideOutLeft);
                break;
        }
    }

    private void animateValues(final PieData data, final ScreenUsage screenUsage, final Techniques inTechnique, Techniques outTechnique) {
        YoYo.with(outTechnique).duration(400).onEnd(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                YoYo.with(inTechnique).duration(400).onStart(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        setPieDateWithoutAnimation(data, screenUsage);
                        mPieChart.animate();
                    }
                }).playOn(mTimerContainer);
            }
        }).playOn(mTimerContainer);

        /*YoYo.with(outTechnique).duration(400).onEnd(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                YoYo.with(inTechnique).duration(400).playOn(mDateContainer);
            }
        }).playOn(mDateContainer);

        YoYo.with(outTechnique).duration(400).onEnd(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                YoYo.with(inTechnique).duration(400).playOn(mEmoticonIV);
            }
        }).playOn(mEmoticonIV);*/

        YoYo.with(Techniques.SlideOutUp).duration(400).onEnd(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                YoYo.with(Techniques.BounceInDown).duration(400).playOn(mArcTop);
            }
        }).playOn(mArcTop);

        YoYo.with(Techniques.SlideOutDown).duration(400).onEnd(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                YoYo.with(Techniques.BounceInUp).duration(400).playOn(mArcBottom);
            }
        }).playOn(mArcBottom);
    }

    @Override
    public void setLeftArrowVisibility(boolean isEnabled) {
        mLeftArrow.setVisibility(isEnabled ? View.VISIBLE : View.INVISIBLE);
        mLeftArrow.setEnabled(isEnabled);
    }

    @Override
    public void setRightArrowVisibility(boolean isEnabled) {
        mRightArrow.setVisibility(isEnabled ? View.VISIBLE : View.INVISIBLE);
        mRightArrow.setEnabled(isEnabled);
    }

    private void setPieDateWithoutAnimation(PieData data, ScreenUsage screenUsage) {
        mPieChart.setEntryLabelColor(Color.rgb(41, 74, 98));
        mPieChart.setData(data);
        mPieChart.setCenterText(mPresenter.generateCenterSpannableText(mContext, screenUsage.getSecondsUsed()));
        mPieChart.invalidate();
        mDateTV.setText(TimeUtil.convertStringDateToFormattedString(screenUsage.getDate()));
        mEmoticonIV.setImageResource(mPresenter.calculateForEmoticon(screenUsage));
    }

    private void setLegendIfToday(long usedTime, int allowedTime) {
        Legend legend = mPieChart.getLegend();
        legend.setEnabled(true);
        legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setTextSize(12);
        legend.setTextColor(Color.WHITE);
        legend.setYEntrySpace(10);

        mPieChart.getLegend().setCustom(mPresenter.getLegendEntries(usedTime, allowedTime));
    }


    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        mDatePickerDialog.dismiss();
        mPresenter.fetchData(year, month, day);
    }

    @Override
    public void setData() {
//        mPresenter.updateData();
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
