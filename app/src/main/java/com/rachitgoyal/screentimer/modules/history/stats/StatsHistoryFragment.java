package com.rachitgoyal.screentimer.modules.history.stats;

import android.animation.Animator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.orm.query.Select;
import com.rachitgoyal.screentimer.R;
import com.rachitgoyal.screentimer.model.ScreenUsage;
import com.rachitgoyal.screentimer.modules.base.BaseFragment;
import com.rachitgoyal.screentimer.util.TimeUtil;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StatsHistoryFragment extends BaseFragment {

    private static final float SECONDS_IN_A_DAY = 86400;

    @BindView(R.id.stats_cv)
    CardView mHeaderCV;

    @BindView(R.id.average_usage_cv)
    CardView mAverageUsageCV;
    @BindView(R.id.average_usage_pb)
    CircularProgressBar mAverageUsagePB;
    @BindView(R.id.average_usage_tv)
    TextView mAverageUsageTV;

    @BindView(R.id.max_usage_cv)
    CardView mMaxUsageCV;
    @BindView(R.id.max_usage_pb)
    CircularProgressBar mMaxUsagePB;
    @BindView(R.id.max_usage_tv)
    TextView mMaxUsageTV;
    @BindView(R.id.date_max_usage_tv)
    TextView mMaxUsageDateTV;

    @BindView(R.id.min_usage_cv)
    CardView mMinUsageCV;
    @BindView(R.id.min_usage_pb)
    CircularProgressBar mMinUsagePB;
    @BindView(R.id.min_usage_tv)
    TextView mMinUsageTV;
    @BindView(R.id.date_min_usage_tv)
    TextView mMinUsageDateTV;

    @BindView(R.id.other_stats_cv)
    CardView mOtherStatsCV;
    @BindView(R.id.total_usage_ll)
    LinearLayout mTotalUsageLL;
    @BindView(R.id.days_tv)
    TextView mDaysTV;
    @BindView(R.id.hours_tv)
    TextView mHoursTV;
    @BindView(R.id.mins_tv)
    TextView mMinsTV;
    @BindView(R.id.percent_time_ll)
    LinearLayout mPercentTimeLL;
    @BindView(R.id.average_percentage_tv)
    TextView mAveragePercentageTV;
    @BindView(R.id.red_days_ll)
    LinearLayout mRedDaysLL;
    @BindView(R.id.red_day_count_tv)
    TextView mRedDayCountTV;
    @BindView(R.id.green_days_ll)
    LinearLayout mGreenDaysLL;
    @BindView(R.id.green_day_count_tv)
    TextView mGreenDayCountTV;
    @BindView(R.id.total_days_ll)
    LinearLayout mTotalDaysLL;
    @BindView(R.id.total_days_tv)
    TextView mTotalDaysTV;

    public StatsHistoryFragment() {
    }

    public static StatsHistoryFragment newInstance() {
        return new StatsHistoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats_history, container, false);
        ButterKnife.bind(this, view);
        calculateValues();
        return view;
    }

    private void calculateValues() {
        long totalUsage = 0, averageUsage = 0, maxUsage = 0, minUsage = Long.MAX_VALUE, numberOfDays = 0, numberOfRedDays = 0, numberOfGreenDays = 0;
        String maxUsageDate = "", minUsageDate = "";

        List<ScreenUsage> screenUsages = Select.from(ScreenUsage.class).list();
        numberOfDays = screenUsages.size();

        for (ScreenUsage screenUsage : screenUsages) {
            totalUsage = totalUsage + screenUsage.getSecondsUsed();
            if (maxUsage < screenUsage.getSecondsUsed()) {
                maxUsage = screenUsage.getSecondsUsed();
                maxUsageDate = screenUsage.getDate();
            }
            if (minUsage > screenUsage.getSecondsUsed()) {
                minUsage = screenUsage.getSecondsUsed();
                minUsageDate = screenUsage.getDate();
            }
            if (screenUsage.getSecondsUsed() > screenUsage.getSecondsAllowed()) {
                numberOfRedDays++;
            } else {
                numberOfGreenDays++;
            }
        }
        averageUsage = totalUsage / numberOfDays;
        setData(totalUsage, averageUsage, maxUsage, maxUsageDate, minUsage, minUsageDate, numberOfDays, numberOfRedDays, numberOfGreenDays);
    }

    private void setData(long totalUsage, long averageUsage, long maxUsage, String maxUsageDate, long minUsage, String minUsageDate, long numberOfDays,
                         long numberOfRedDays, long numberOfGreenDays) {

        mAverageUsageTV.setText(TimeUtil.convertSecondsToExactTimeString(averageUsage));

        mMaxUsageTV.setText(TimeUtil.convertSecondsToExactTimeString(maxUsage));
        mMaxUsageDateTV.setText(maxUsageDate);

        mMinUsageTV.setText(TimeUtil.convertSecondsToExactTimeString(minUsage));
        mMinUsageDateTV.setText(minUsageDate);

        int days = (int) (totalUsage / 86400);
        int hours = (int) ((totalUsage - (days * 86400)) / 3600);
        int mins = (int) ((totalUsage - (days * 86400) - (hours * 3600)) / 60);

        mDaysTV.setText(String.valueOf(days));
        mHoursTV.setText(String.valueOf(hours));
        mMinsTV.setText(String.valueOf(mins));

        mAveragePercentageTV.setText(String.format("%s %%", String.format(Locale.getDefault(), "%.2f", (float) averageUsage / 86400)));
        mRedDayCountTV.setText(String.valueOf(numberOfRedDays));
        mGreenDayCountTV.setText(String.valueOf(numberOfGreenDays));
        mTotalDaysTV.setText(String.valueOf(numberOfDays));

        animateEntryViews(averageUsage, maxUsage, minUsage);
    }

    private void animateEntryViews(final long averageUsage, final long maxUsage, final long minUsage) {

        YoYo.with(Techniques.FadeIn).duration(400).onStart(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                mHeaderCV.setVisibility(View.VISIBLE);
            }
        }).onEnd(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                animateAverageCV(averageUsage, maxUsage, minUsage);
            }
        }).playOn(mHeaderCV);
    }

    private void animateAverageCV(final long averageUsage, final long maxUsage, final long minUsage) {
        YoYo.with(Techniques.ZoomIn).duration(600).onStart(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                mAverageUsageCV.setVisibility(View.VISIBLE);
            }
        }).onEnd(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                mAverageUsagePB.setProgressWithAnimation((averageUsage / SECONDS_IN_A_DAY) * 100, 1000);
                animateMaxMinCV(maxUsage, minUsage);

            }
        }).playOn(mAverageUsageCV);
    }

    private void animateMaxMinCV(final long maxUsage, final long minUsage) {
        YoYo.with(Techniques.SlideInLeft).duration(600).onStart(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                mMaxUsageCV.setVisibility(View.VISIBLE);
            }
        }).onEnd(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                mMaxUsagePB.setProgressWithAnimation((maxUsage / SECONDS_IN_A_DAY) * 100, 800);
            }
        }).playOn(mMaxUsageCV);
        YoYo.with(Techniques.SlideInRight).duration(600).onStart(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                mMinUsageCV.setVisibility(View.VISIBLE);

            }
        }).onEnd(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                mMinUsagePB.setProgressWithAnimation((minUsage / SECONDS_IN_A_DAY) * 100, 800);
                animateOtherStatsCV();
            }
        }).playOn(mMinUsageCV);
    }

    private void animateOtherStatsCV() {
        YoYo.with(Techniques.BounceInUp).duration(600).onStart(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                mOtherStatsCV.setVisibility(View.VISIBLE);
                animateTotalUsageItem();
            }
        }).playOn(mOtherStatsCV);
    }

    private void animateTotalUsageItem() {
        YoYo.with(Techniques.SlideInLeft).duration(600).onStart(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                mTotalUsageLL.setVisibility(View.VISIBLE);
            }
        }).onEnd(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                animatePercentTimeItem();
            }
        }).playOn(mTotalUsageLL);
    }

    private void animatePercentTimeItem() {
        YoYo.with(Techniques.SlideInLeft).duration(600).onStart(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                mPercentTimeLL.setVisibility(View.VISIBLE);
            }
        }).onEnd(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                animateRedDaysItem();
            }
        }).playOn(mPercentTimeLL);
    }

    private void animateRedDaysItem() {
        YoYo.with(Techniques.SlideInLeft).duration(600).onStart(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                mRedDaysLL.setVisibility(View.VISIBLE);
            }
        }).onEnd(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                animateGreenDaysItem();
            }
        }).playOn(mRedDaysLL);
    }

    private void animateGreenDaysItem() {
        YoYo.with(Techniques.SlideInLeft).duration(600).onStart(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                mGreenDaysLL.setVisibility(View.VISIBLE);
            }
        }).onEnd(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                animateTotalDaysItem();
            }
        }).playOn(mGreenDaysLL);
    }

    private void animateTotalDaysItem() {
        YoYo.with(Techniques.SlideInLeft).duration(600).onStart(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                mTotalDaysLL.setVisibility(View.VISIBLE);
            }
        }).playOn(mTotalDaysLL);
    }
}
