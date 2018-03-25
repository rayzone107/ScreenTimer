package com.rachitgoyal.screentimer.modules.home;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.rachitgoyal.screentimer.model.ScreenUsage;
import com.rachitgoyal.screentimer.util.Constants;
import com.rachitgoyal.screentimer.util.TimeUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Rachit Goyal on 15/03/18
 */

public class HomePresenter implements HomeContract.Presenter {

    private static final long SECONDS_IN_A_DAY = 86400;
    private HomeContract.View mView;
    private SharedPreferences mPrefs;

    HomePresenter(HomeContract.View view, SharedPreferences sharedPreferences) {
        mView = view;
        mPrefs = sharedPreferences;
    }

    @Override
    public void setupChart(PieChart chart) {
        chart.setUsePercentValues(false);
        chart.setExtraOffsets(20, 30, 20, 20);

        chart.setDragDecelerationFrictionCoef(0.95f);

        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.TRANSPARENT);

        chart.setTransparentCircleColor(Color.WHITE);

        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);
        chart.setEntryLabelColor(Color.BLACK);

        chart.setDrawCenterText(true);

        chart.setRotationEnabled(false);
        chart.setHighlightPerTapEnabled(true);
        chart.getLegend().setEnabled(false);
        Description description = new Description();
        description.setText("");
        chart.setDescription(description);
        chart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
    }

    @Override
    public void setData() {
        int allowedTime = TimeUtil.getMillsFromTimeOption(mPrefs.getString(Constants.PREFERENCES.MAX_TIME_OPTION, ""));
        List<ScreenUsage> screenUsageList = Select.from(ScreenUsage.class)
                .where(Condition.prop(ScreenUsage.dateField).eq(TimeUtil.getDateAsFormattedString(new Date()))).limit("1").list();
        ScreenUsage screenUsage;
        if (screenUsageList.isEmpty()) {
            screenUsage = new ScreenUsage(TimeUtil.getDateAsFormattedString(new Date()), 0, allowedTime);
            screenUsage.save();
        } else {
            screenUsage = screenUsageList.get(0);
        }

        ArrayList<PieEntry> pieValues = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        long usedTime = screenUsage.getSecondsUsed();
        long exceededTime, leftTime, maxTime;
        if (usedTime > allowedTime) {
            exceededTime = usedTime - allowedTime;
            maxTime = SECONDS_IN_A_DAY - usedTime;

            pieValues.add(new PieEntry(allowedTime, TimeUtil.generateTimeFromSeconds(allowedTime)));
            pieValues.add(new PieEntry(exceededTime, TimeUtil.generateTimeFromSeconds(exceededTime)));
            pieValues.add(new PieEntry(maxTime, TimeUtil.generateTimeFromSeconds(maxTime)));

            colors.add(Color.rgb(255, 151, 151));
            colors.add(Color.rgb(186, 5, 5));
            colors.add(Color.rgb(211, 211, 211));
        } else {
            leftTime = (screenUsage.getSecondsAllowed() - screenUsage.getSecondsUsed());
            maxTime = (SECONDS_IN_A_DAY - allowedTime);

            pieValues.add(new PieEntry(usedTime, TimeUtil.generateTimeFromSeconds(usedTime)));
            pieValues.add(new PieEntry(leftTime, TimeUtil.generateTimeFromSeconds(leftTime)));
            pieValues.add(new PieEntry(maxTime, TimeUtil.generateTimeFromSeconds(maxTime)));

            colors.add(Color.rgb(255, 151, 151));
            colors.add(Color.GREEN);
            colors.add(Color.rgb(211, 211, 211));
        }

        PieDataSet dataSet = new PieDataSet(pieValues, "");
        dataSet.setSliceSpace(2f);
        dataSet.setSelectionShift(5f);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setColors(colors);

        if (allowedTime < 14000) {
            dataSet.setValueLinePart1OffsetPercentage(60.f);
            dataSet.setValueLinePart1Length(0.4f);
            dataSet.setValueLinePart2Length(0.4f);
            dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
            if (allowedTime < 900) {
                mView.setAngle(-92);
            } else if (allowedTime < 1800) {
                mView.setAngle(-93);
            } else if (allowedTime < 2700) {
                mView.setAngle(-95);
            } else if (allowedTime < 3600) {
                mView.setAngle(-96);
            } else {
                mView.setAngle(-100);
            }
        } else {
            dataSet.setValueLinePart1OffsetPercentage(1.f);
            dataSet.setXValuePosition(PieDataSet.ValuePosition.INSIDE_SLICE);
            mView.setAngle(-90);
        }

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        data.setDrawValues(false);

        mView.setData(data);
        mView.setCenterText(generateCenterSpannableText(usedTime));
    }

    @Override
    public void handleMaxTimeChange() {

    }

    private SpannableString generateCenterSpannableText(long usedTime) {
        SpannableString s = new SpannableString("Today's Usage\n\n".concat(TimeUtil.convertMillisToString(usedTime)));
        s.setSpan(new RelativeSizeSpan(1.7f), 0, 13, 0);
        s.setSpan(new RelativeSizeSpan(1f), 14, 15, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 15, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), 15, s.length(), 0);
        s.setSpan(new RelativeSizeSpan(1.4f), 15, s.length(), 0);
        return s;
    }
}
