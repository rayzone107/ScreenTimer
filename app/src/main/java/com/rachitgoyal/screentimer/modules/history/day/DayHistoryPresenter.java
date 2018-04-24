package com.rachitgoyal.screentimer.modules.history.day;

import android.content.Context;
import android.content.res.Configuration;
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
import com.rachitgoyal.screentimer.R;
import com.rachitgoyal.screentimer.model.ScreenUsage;
import com.rachitgoyal.screentimer.util.CustomTypefaceSpan;
import com.rachitgoyal.screentimer.util.TimeOptions;
import com.rachitgoyal.screentimer.util.TimeUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Rachit Goyal on 25/03/18
 */

public class DayHistoryPresenter implements DayHistoryContract.Presenter {

    public enum PieChange {
        FIRST_LAUNCH(0),
        DATE_SELECTION(1),
        LEFT_SLIDE(2),
        RIGHT_SLIDE(3);

        private int value;

        PieChange(int i) {
            value = i;
        }

        public int getValue() {
            return value;
        }
    }

    private DayHistoryContract.View mView;
    private String mDisplayedDate;

    DayHistoryPresenter(DayHistoryContract.View view) {
        mView = view;
    }

    @Override
    public void fetchData(int year, int month, int day) {
        String date = String.format(Locale.getDefault(), "%02d", day) + "/" + String.format(Locale.getDefault(), "%02d", (month + 1)) + "/" + year;
        List<ScreenUsage> screenUsageList = Select.from(ScreenUsage.class).orderBy(ScreenUsage.dateField)
                .where(Condition.prop(ScreenUsage.dateField).eq(date)).list();
        if (!screenUsageList.isEmpty()) {
            setPieValues(screenUsageList.get(0), PieChange.DATE_SELECTION);
        } else {
            ScreenUsage screenUsage = new ScreenUsage(date, 0, TimeOptions.THREE_HOURS);
            setPieValues(screenUsage, PieChange.DATE_SELECTION);
        }
    }

    @Override
    public void fetchTodayData() {
        List<ScreenUsage> screenUsageList = Select.from(ScreenUsage.class)
                .where(Condition.prop(ScreenUsage.dateField).eq(TimeUtil.getDateAsFormattedString(new Date())))
                .limit("1").list();

        if (!screenUsageList.isEmpty()) {
            setPieValues(screenUsageList.get(0), PieChange.FIRST_LAUNCH);
        }
    }

    @Override
    public void setupChart(PieChart chart, int orientation) {
        chart.setUsePercentValues(false);
        chart.setExtraOffsets(10, 10, 10, 10);

        chart.setDragDecelerationFrictionCoef(0.95f);

        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.TRANSPARENT);

        chart.setTransparentCircleColor(Color.WHITE);

        chart.setHoleRadius(45f);
        chart.setTransparentCircleRadius(50f);

        chart.setDrawCenterText(true);
        chart.setCenterTextSize(orientation == Configuration.ORIENTATION_LANDSCAPE ? 9 : 13);
        chart.setEntryLabelTextSize(orientation == Configuration.ORIENTATION_LANDSCAPE ? 9 : 13);
        chart.setEntryLabelColor(Color.BLACK);

        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);

        Legend legend = chart.getLegend();
        legend.setEnabled(true);
        legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setTextSize(12);
        legend.setTextColor(Color.BLACK);
        legend.setYEntrySpace(10);
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        } else {
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        }

        Description description = new Description();
        description.setText("");
        chart.setDescription(description);
        chart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
    }

    private void setPieValues(ScreenUsage screenUsage, PieChange pieChange) {
        mDisplayedDate = screenUsage.getDate();
        int allowedTime = (int) screenUsage.getSecondsAllowed();

        ArrayList<PieEntry> pieValues = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        long usedTime = screenUsage.getSecondsUsed();
        long exceededTime, leftTime, maxTime;

        if (usedTime == 0) {
            maxTime = TimeUtil.calculateGrayedTimeFromTimeOption(allowedTime) - allowedTime;

            pieValues.add(new PieEntry(allowedTime, TimeUtil.convertSecondsToApproximateTimeString(allowedTime)));
            pieValues.add(new PieEntry(maxTime, ""));

            colors.add(Color.GREEN);
            colors.add(Color.rgb(211, 211, 211));
        } else if (usedTime > allowedTime) {
            exceededTime = usedTime - allowedTime;
            maxTime = TimeUtil.getScaleMaxTimeFromExceededUsedTime(usedTime) - usedTime;

            PieEntry pieEntry1 = new PieEntry(allowedTime, TimeUtil.convertSecondsToApproximateTimeString(allowedTime));
            pieValues.add(pieEntry1);
            pieValues.add(new PieEntry(exceededTime, TimeUtil.convertSecondsToApproximateTimeString(exceededTime)));
            pieValues.add(new PieEntry(maxTime, ""));

            colors.add(Color.rgb(255, 151, 151));
            colors.add(Color.rgb(224, 8, 8));
            colors.add(Color.rgb(211, 211, 211));
        } else {
            leftTime = (screenUsage.getSecondsAllowed() - screenUsage.getSecondsUsed());
            maxTime = TimeUtil.calculateGrayedTimeFromTimeOption(allowedTime) - allowedTime;

            pieValues.add(new PieEntry(usedTime, TimeUtil.convertSecondsToApproximateTimeString(usedTime)));
            if (leftTime != 0) {
                pieValues.add(new PieEntry(leftTime, TimeUtil.convertSecondsToApproximateTimeString(leftTime)));
            }
            pieValues.add(new PieEntry(maxTime, ""));

            colors.add(Color.rgb(255, 151, 151));
            if (leftTime != 0) {
                colors.add(Color.GREEN);
            }
            colors.add(Color.rgb(211, 211, 211));
        }
        setLegendEntries(usedTime, allowedTime, colors, mDisplayedDate.equals(TimeUtil.sdf.format(new Date())));
        mView.setPieData(setPieData(pieValues, colors), screenUsage, pieChange);
        handleArrowState(screenUsage);
    }

    private void handleArrowState(ScreenUsage screenUsage) {
        mView.setLeftArrowVisibility(!screenUsage.getDate().equals(findFirstDate()));
        mView.setRightArrowVisibility(!screenUsage.getDate().equals(TimeUtil.sdf.format(new Date())));
    }

    private PieData setPieData(ArrayList<PieEntry> pieValues, ArrayList<Integer> colors) {
        PieDataSet dataSet = new PieDataSet(pieValues, "");
        dataSet.setSliceSpace(2f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(colors);

        dataSet.setValueLinePart1OffsetPercentage(1.f);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.INSIDE_SLICE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.rgb(211, 211, 211));
        data.setDrawValues(false);

        return data;
    }

    private void setLegendEntries(long usedTime, int allowedTime, ArrayList<Integer> colors, boolean isToday) {
        List<LegendEntry> legendEntries = new ArrayList<>();
        Legend.LegendForm formShape = Legend.LegendForm.DEFAULT;
        float formSize = 12f;
        if (usedTime == 0) {
            legendEntries.add(new LegendEntry("Allowed Time\n", formShape, formSize,
                    Float.NaN, null, colors.get(0)));
            legendEntries.add(new LegendEntry("Glad you didn't go here\n", formShape, formSize,
                    Float.NaN, null, colors.get(1)));
            legendEntries.add(new LegendEntry("", Legend.LegendForm.NONE, 0,
                    Float.NaN, null, Color.TRANSPARENT));
        } else if (usedTime > allowedTime) {
            legendEntries.add(new LegendEntry("Where you were supposed to stop\n", formShape, formSize,
                    Float.NaN, null, colors.get(0)));
            legendEntries.add(new LegendEntry(isToday ? "You are going too far\n" : "You went too far\n", formShape, formSize,
                    Float.NaN, null, colors.get(1)));
            legendEntries.add(new LegendEntry(isToday ? "You really shouldn't go here" : "Glad you didn't go here\n", formShape, formSize,
                    Float.NaN, null, colors.get(2)));
            legendEntries.add(new LegendEntry("", Legend.LegendForm.NONE, 0,
                    Float.NaN, null, Color.TRANSPARENT));
        } else {
            legendEntries.add(new LegendEntry(isToday ? "Damage to your eyes so far\n" : "Damage to your eyes\n", formShape, formSize,
                    Float.NaN, null, colors.get(0)));
            legendEntries.add(new LegendEntry(isToday ? "You can go this much more\n" : "Glad you saved your eyes from this bit\n", formShape, formSize,
                    Float.NaN, null, colors.get(1)));
            legendEntries.add(new LegendEntry("Danger Zone", formShape, formSize,
                    Float.NaN, null, colors.get(2)));
            legendEntries.add(new LegendEntry("", Legend.LegendForm.NONE, 0,
                    Float.NaN, null, Color.TRANSPARENT));
        }

        mView.setLegend(legendEntries);
    }

    @Override
    public SpannableString generateCenterSpannableText(Context context, long usedTime) {
        SpannableString s = new SpannableString(TimeUtil.convertSecondsToExactTimeString(usedTime));
        s.setSpan(new RelativeSizeSpan(1.5f), 0, s.length(), 0);
        s.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s.length(), 0);
        s.setSpan(new CustomTypefaceSpan(Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-BoldItalic.ttf")),
                0, s.length(), 0);
        return s;
    }

    @Override
    public int calculateForEmoticon(ScreenUsage screenUsage) {
        if (screenUsage.getSecondsUsed() < screenUsage.getSecondsAllowed() / 2) {
            return R.drawable.extra_happy;
        } else if (screenUsage.getSecondsUsed() < screenUsage.getSecondsAllowed() - 100) {
            return R.drawable.happy;
        } else if ((screenUsage.getSecondsUsed() > screenUsage.getSecondsAllowed() - 100) &&
                (screenUsage.getSecondsUsed() < screenUsage.getSecondsAllowed() + 100)) {
            return R.drawable.neutral;
        } else if (screenUsage.getSecondsUsed() > screenUsage.getSecondsAllowed() * 2) {
            return R.drawable.extra_sad;
        } else {
            return R.drawable.sad;
        }
    }

    @Override
    public long getMinDateForDatePicker() {
        List<ScreenUsage> screenUsageList = Select.from(ScreenUsage.class).orderBy(ScreenUsage.dateField).list();
        Collections.sort(screenUsageList, new TimeUtil.StringDateComparator());

        ScreenUsage firstScreenUsage = screenUsageList.get(0);

        Date date;
        try {
            date = TimeUtil.sdf.parse(firstScreenUsage.getDate());
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void handleLeftClick() {
        List<ScreenUsage> screenUsageList = Select.from(ScreenUsage.class).orderBy(ScreenUsage.dateField).list();
        Collections.sort(screenUsageList, new TimeUtil.StringDateComparator());

        int displayedPosition = screenUsageList.size() - 1;
        for (int i = screenUsageList.size() - 1; i >= 0; i--) {
            ScreenUsage screenUsage = screenUsageList.get(i);
            try {
                if (screenUsage.getDate().equals(mDisplayedDate) ||
                        TimeUtil.sdf.parse(screenUsage.getDate()).after(TimeUtil.sdf.parse(mDisplayedDate))) {
                    displayedPosition = i;
                } else {
                    break;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (displayedPosition != 0) {
            ScreenUsage previousUsage = screenUsageList.get(displayedPosition - 1);
            setPieValues(previousUsage, PieChange.LEFT_SLIDE);
        }
    }

    @Override
    public void handleRightClick() {
        List<ScreenUsage> screenUsageList = Select.from(ScreenUsage.class).orderBy(ScreenUsage.dateField).list();
        Collections.sort(screenUsageList, new TimeUtil.StringDateComparator());

        int displayedPosition = screenUsageList.size() - 1;
        for (int i = 0; i < screenUsageList.size(); i++) {
            ScreenUsage screenUsage = screenUsageList.get(i);
            try {
                if (screenUsage.getDate().equals(mDisplayedDate) ||
                        TimeUtil.sdf.parse(screenUsage.getDate()).before(TimeUtil.sdf.parse(mDisplayedDate))) {
                    displayedPosition = i;
                } else {
                    break;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (displayedPosition != screenUsageList.size() - 1) {
            ScreenUsage previousUsage = screenUsageList.get(displayedPosition + 1);
            setPieValues(previousUsage, PieChange.RIGHT_SLIDE);
        }
    }

    @Override
    public void updateData() {
        if (mDisplayedDate.equals(TimeUtil.sdf.format(new Date()))) {
            fetchTodayData();
        }
    }

    private String findFirstDate() {
        List<ScreenUsage> screenUsageList = Select.from(ScreenUsage.class).orderBy(ScreenUsage.dateField).list();
        Collections.sort(screenUsageList, new TimeUtil.StringDateComparator());
        return screenUsageList.get(0).getDate();
    }
}
