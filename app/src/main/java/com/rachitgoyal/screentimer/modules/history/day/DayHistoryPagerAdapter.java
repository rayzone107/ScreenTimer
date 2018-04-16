package com.rachitgoyal.screentimer.modules.history.day;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.rachitgoyal.screentimer.R;
import com.rachitgoyal.screentimer.model.ScreenUsage;
import com.rachitgoyal.screentimer.util.CustomTypefaceSpan;
import com.rachitgoyal.screentimer.util.TimeUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Rachit Goyal on 28/03/18
 */

public class DayHistoryPagerAdapter extends RecyclerView.Adapter<DayHistoryPagerAdapter.ViewHolder> {

    private List<ScreenUsage> mScreenUsageList;
    private ViewHolder mViewHolder;

    public DayHistoryPagerAdapter(List<ScreenUsage> screenUsageList) {
        mScreenUsageList = screenUsageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_day_history_pager, parent, false);
        mViewHolder = new ViewHolder(view);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        mViewHolder.bindData(mScreenUsageList.get(position));
    }

    @Override
    public int getItemCount() {
        return mScreenUsageList.size();
    }

    public List<ScreenUsage> getScreenUsageList() {
        return mScreenUsageList;
    }

    public void setScreenUsageList(List<ScreenUsage> screenUsageList) {
        mScreenUsageList = screenUsageList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private PieChart mPieChart;
        private TextView mDateTV;
        private ImageView mEmoticonTV;
        private boolean isChartSetup = false;

        ViewHolder(View itemView) {
            super(itemView);
            mPieChart = itemView.findViewById(R.id.timer_chart);
            mDateTV = itemView.findViewById(R.id.date_tv);
            mEmoticonTV = itemView.findViewById(R.id.emoticon_iv);
        }

        void bindData(ScreenUsage screenUsage) {
            if (!isChartSetup) {
                setupChart();
            }
            setData(screenUsage);
            isChartSetup = true;
        }

        private void setupChart() {
            mPieChart.setUsePercentValues(false);
            mPieChart.setExtraOffsets(10, 10, 10, 10);

            mPieChart.setDragDecelerationFrictionCoef(0.95f);

            mPieChart.setDrawHoleEnabled(true);
            mPieChart.setHoleColor(Color.TRANSPARENT);

            mPieChart.setTransparentCircleColor(Color.WHITE);

            mPieChart.setHoleRadius(58f);
            mPieChart.setTransparentCircleRadius(61f);

            mPieChart.setDrawCenterText(true);

            mPieChart.setRotationEnabled(true);
            mPieChart.setHighlightPerTapEnabled(true);
            mPieChart.getLegend().setEnabled(false);

            Description description = new Description();
            description.setText("");
            mPieChart.setDescription(description);
            mPieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        }

        private void setData(ScreenUsage screenUsage) {
            int allowedTime = (int) screenUsage.getSecondsAllowed();

            ArrayList<PieEntry> pieValues = new ArrayList<>();
            ArrayList<Integer> colors = new ArrayList<>();

            long usedTime = screenUsage.getSecondsUsed();
            long exceededTime, leftTime, maxTime;
            if (usedTime > allowedTime) {
                exceededTime = usedTime - allowedTime;
                maxTime = TimeUtil.getScaleMaxTimeFromExceededUsedTime(usedTime) - usedTime;

                pieValues.add(new PieEntry(allowedTime, TimeUtil.convertSecondsToApproximateTimeString(allowedTime)));
                pieValues.add(new PieEntry(exceededTime, TimeUtil.convertSecondsToApproximateTimeString(exceededTime)));
                pieValues.add(new PieEntry(maxTime, ""));

                colors.add(Color.rgb(255, 151, 151));
                colors.add(Color.rgb(186, 5, 5));
                colors.add(Color.rgb(211, 211, 211));
            } else {
                leftTime = (screenUsage.getSecondsAllowed() - screenUsage.getSecondsUsed());
                maxTime = TimeUtil.calculateGrayedTimeFromTimeOption(allowedTime) - allowedTime;

                pieValues.add(new PieEntry(usedTime, TimeUtil.convertSecondsToApproximateTimeString(usedTime)));
                pieValues.add(new PieEntry(leftTime, TimeUtil.convertSecondsToApproximateTimeString(leftTime)));
                pieValues.add(new PieEntry(maxTime, ""));

                colors.add(Color.rgb(255, 151, 151));
                colors.add(Color.GREEN);
                colors.add(Color.rgb(211, 211, 211));
            }

            PieDataSet dataSet = new PieDataSet(pieValues, "");
            dataSet.setSliceSpace(2f);
            dataSet.setSelectionShift(5f);
            dataSet.setColors(colors);

            dataSet.setValueLinePart1OffsetPercentage(1.f);
            mPieChart.setEntryLabelColor(Color.rgb(41, 74, 98));
            dataSet.setXValuePosition(PieDataSet.ValuePosition.INSIDE_SLICE);

            PieData data = new PieData(dataSet);
            data.setValueFormatter(new PercentFormatter());
            data.setValueTextSize(11f);
            data.setValueTextColor(Color.BLACK);
            data.setDrawValues(false);

            mPieChart.setData(data);

            mPieChart.setCenterText(generateCenterSpannableText(usedTime));
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            /*if (screenUsage.getDate().equals(sdf.format(new Date()))) {
                setLegendIfToday(usedTime, allowedTime);
            }*/

            mEmoticonTV.setImageResource(calculateForEmoticon(screenUsage));
            mDateTV.setText(TimeUtil.convertStringDateToFormattedString(screenUsage.getDate()));
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

            mPieChart.getLegend().setCustom(legendEntries);
        }

        private SpannableString generateCenterSpannableText(long usedTime) {
            SpannableString s = new SpannableString(TimeUtil.convertSecondsToExactTimeString(usedTime));
            s.setSpan(new RelativeSizeSpan(2.0f), 0, s.length(), 0);
            s.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), 0);
            s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, s.length(), 0);
            s.setSpan(new CustomTypefaceSpan(Typeface.createFromAsset(mPieChart.getContext().getAssets(), "fonts/OpenSans-BoldItalic.ttf")),
                    0, s.length(), 0);
            return s;
        }

        private int calculateForEmoticon(ScreenUsage screenUsage) {
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
    }
}
