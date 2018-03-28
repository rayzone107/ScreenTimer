package com.rachitgoyal.screentimer.modules.history.day;

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
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.rachitgoyal.screentimer.model.ScreenUsage;
import com.rachitgoyal.screentimer.util.Constants;
import com.rachitgoyal.screentimer.util.TimeUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Rachit Goyal on 25/03/18
 */

public class DayHistoryPresenter implements DayHistoryContract.Presenter {

    private static final long SECONDS_IN_A_DAY = 86400;
    private DayHistoryContract.View mView;
    private SharedPreferences mPrefs;

    public DayHistoryPresenter(DayHistoryContract.View view, SharedPreferences prefs) {
        mView = view;
        mPrefs = prefs;
    }

    @Override
    public void setupChart(PieChart chart) {
        chart.setUsePercentValues(false);
        chart.setExtraOffsets(10, 0, 10, 20);

        chart.setDragDecelerationFrictionCoef(0.95f);

        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.TRANSPARENT);

        chart.setTransparentCircleColor(Color.WHITE);

        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);

        chart.setDrawCenterText(true);

        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);
        Legend legend = chart.getLegend();
        legend.setEnabled(true);
        legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setTextSize(12);
        legend.setTextColor(Color.WHITE);
        legend.setYEntrySpace(10);

        Description description = new Description();
        description.setText("");
        chart.setDescription(description);
        chart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
    }

    @Override
    public void setData() {
        int allowedTime = TimeUtil.convertTimeOptionToSeconds(mPrefs.getString(Constants.PREFERENCES.MAX_TIME_OPTION, ""));
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
            maxTime = TimeUtil.getScaleMaxTimeFromExceededUsedTime(usedTime) - usedTime;

            pieValues.add(new PieEntry(allowedTime, TimeUtil.convertSecondsToApproximateTimeString(allowedTime)));
            pieValues.add(new PieEntry(exceededTime, TimeUtil.convertSecondsToApproximateTimeString(exceededTime)));
            pieValues.add(new PieEntry(maxTime, TimeUtil.convertSecondsToApproximateTimeString(maxTime)));

            colors.add(Color.rgb(255, 151, 151));
            colors.add(Color.rgb(186, 5, 5));
            colors.add(Color.rgb(211, 211, 211));
        } else {
            leftTime = (screenUsage.getSecondsAllowed() - screenUsage.getSecondsUsed());
            maxTime = TimeUtil.calculateGrayedTimeFromTimeOption(allowedTime) - allowedTime;

            pieValues.add(new PieEntry(usedTime, TimeUtil.convertSecondsToApproximateTimeString(usedTime)));
            pieValues.add(new PieEntry(leftTime, TimeUtil.convertSecondsToApproximateTimeString(leftTime)));
            pieValues.add(new PieEntry(maxTime, TimeUtil.convertSecondsToApproximateTimeString(maxTime)));

            colors.add(Color.rgb(255, 151, 151));
            colors.add(Color.GREEN);
            colors.add(Color.rgb(211, 211, 211));
        }

        PieDataSet dataSet = new PieDataSet(pieValues, "");
        dataSet.setSliceSpace(2f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(colors);

        dataSet.setValueLinePart1OffsetPercentage(1.f);
        mView.setLabelColor(Color.rgb(41, 74, 98));
        dataSet.setXValuePosition(PieDataSet.ValuePosition.INSIDE_SLICE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        data.setDrawValues(false);

        List<LegendEntry> legendEntries = new ArrayList<>();
        Legend.LegendForm formShape = Legend.LegendForm.DEFAULT;
        float formSize = 12f;

        if (usedTime > allowedTime) {
            legendEntries.add(new LegendEntry("Where you were supposed to stop\n", formShape, formSize,
                    Float.NaN, null, Color.rgb(255, 151, 151)));
            legendEntries.add(new LegendEntry("You are going too far\n", formShape, formSize,
                    Float.NaN, null, Color.rgb(186, 5, 5)));
            legendEntries.add(new LegendEntry("You really shouldn't go here", formShape, formSize,
                    Float.NaN, null, Color.rgb(211, 211, 211)));
            legendEntries.add(new LegendEntry("", Legend.LegendForm.NONE, 0,
                    Float.NaN, null, Color.argb(0, 211, 211, 211)));
        } else {
            legendEntries.add(new LegendEntry("Damage to your eyes so far\n", formShape, formSize,
                    Float.NaN, null, Color.rgb(255, 151, 151)));
            legendEntries.add(new LegendEntry("You can go this much more\n", formShape, formSize,
                    Float.NaN, null, Color.GREEN));
            legendEntries.add(new LegendEntry("Danger Zone", formShape, formSize,
                    Float.NaN, null, Color.rgb(211, 211, 211)));
            legendEntries.add(new LegendEntry("", Legend.LegendForm.NONE, 0,
                    Float.NaN, null, Color.argb(0, 211, 211, 211)));
        }

        mView.setData(data);
        mView.setCenterText(generateCenterSpannableText(), TimeUtil.convertSecondsToExactTimeString(usedTime));
        mView.setLegend(legendEntries);
    }

    private SpannableString generateCenterSpannableText() {
        SpannableString s = new SpannableString("GazeAway");
        s.setSpan(new RelativeSizeSpan(2.3f), 0, s.length(), 0);
        s.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(Color.rgb(0, 191, 255)), 0, 4, 0);
        s.setSpan(new ForegroundColorSpan(Color.WHITE), 4, s.length(), 0);
        return s;
    }
}
