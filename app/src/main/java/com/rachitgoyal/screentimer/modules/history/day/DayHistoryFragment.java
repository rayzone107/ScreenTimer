package com.rachitgoyal.screentimer.modules.history.day;

import android.animation.Animator;
import android.app.DatePickerDialog;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.florent37.arclayout.ArcLayout;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.PieData;
import com.rachitgoyal.screentimer.R;
import com.rachitgoyal.screentimer.libraries.swipe_detection.SwipeDetector;
import com.rachitgoyal.screentimer.model.ScreenUsage;
import com.rachitgoyal.screentimer.modules.base.BaseFragment;
import com.rachitgoyal.screentimer.service.TimeChangeBroadcastReceiver;
import com.rachitgoyal.screentimer.util.Constants;
import com.rachitgoyal.screentimer.util.TimeUtil;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DayHistoryFragment extends BaseFragment implements DayHistoryContract.View,
        TimeChangeBroadcastReceiver.TimeChangeListener, DatePickerDialog.OnDateSetListener {

    private TimeChangeBroadcastReceiver mTimeChangeReceiver;
    private DayHistoryContract.Presenter mPresenter;
    private DatePickerDialog mDatePickerDialog;
    private boolean mIsDateSet = false;

    @BindView(R.id.parent_rl)
    RelativeLayout mParentRL;

    @BindView(R.id.arc_top)
    ArcLayout mArcTop;

    @BindView(R.id.arc_bottom)
    ArcLayout mArcBottom;

    @BindView(R.id.date_tv)
    TextView mDateTV;

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
        mDatePickerDialog.setTitle(null);

        mDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatePickerDialog.show();
            }
        });

        mLeftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIsDateSet = false;
                mPresenter.handleLeftClick();
            }
        });

        mRightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIsDateSet = false;
                mPresenter.handleRightClick();
            }
        });

        mParentRL.setOnTouchListener(new SwipeDetector() {
            @Override
            public void leftSwipe() {
                super.leftSwipe();
                if (mRightArrow.getVisibility() == View.VISIBLE) {
                    mRightArrow.performClick();
                }
            }

            @Override
            public void rightSwipe() {
                super.rightSwipe();
                if (mLeftArrow.getVisibility() == View.VISIBLE) {
                    mLeftArrow.performClick();
                }
            }
        });

        mPresenter.setupChart(mPieChart, getActivity().getResources().getConfiguration().orientation);
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

        try {
            Calendar calendar = Calendar.getInstance();
            Date date = TimeUtil.sdf.parse(screenUsage.getDate());
            calendar.setTime(date);
            if (!mIsDateSet) {
                mDatePickerDialog.updateDate(calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                mIsDateSet = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
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
        mPieChart.setData(data);
        mPieChart.setCenterText(mPresenter.generateCenterSpannableText(mContext, screenUsage.getSecondsUsed()));
        mPieChart.invalidate();
        mDateTV.setText(TimeUtil.convertStringDateToFormattedString(screenUsage.getDate()));
        mEmoticonIV.setImageResource(mPresenter.calculateForEmoticon(screenUsage));
    }

    @Override
    public void setLegend(List<LegendEntry> legendEntries) {
        mPieChart.getLegend().setCustom(legendEntries);
    }


    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        mIsDateSet = false;
        mDatePickerDialog.dismiss();
        mPresenter.fetchData(year, month, day);
    }

    @Override
    public void setData() {
        mPresenter.updateData();
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
