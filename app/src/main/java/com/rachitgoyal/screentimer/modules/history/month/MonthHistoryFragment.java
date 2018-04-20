package com.rachitgoyal.screentimer.modules.history.month;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.orm.query.Select;
import com.rachitgoyal.screentimer.R;
import com.rachitgoyal.screentimer.model.ScreenUsage;
import com.rachitgoyal.screentimer.util.TimeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MonthHistoryFragment extends Fragment {

    private LineChart mChart;
    private Context mContext;

    public MonthHistoryFragment() {
    }

    public static MonthHistoryFragment newInstance() {
        return new MonthHistoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_month_history, container, false);
        mChart = view.findViewById(R.id.line_chart);
        mChart.setExtraOffsets(20, 20, 20, 20);
        mChart.setBackgroundColor(Color.WHITE);
        mChart.getDescription().setEnabled(false);
        mChart.setTouchEnabled(true);
        mChart.setHighlightPerTapEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setPinchZoom(true);
        mChart.setDrawGridBackground(true);
        mChart.setMaxHighlightDistance(300);
        mChart.getAxisRight().setEnabled(false);

        setLineData();
        mChart.getLegend().setEnabled(false);
        mChart.setVisibleXRangeMaximum(10);
        mChart.setVisibleXRangeMinimum(4);
        mChart.setVisibleYRangeMinimum(3, YAxis.AxisDependency.LEFT);
        mChart.getViewPortHandler().setMaximumScaleX(2f);
        mChart.getViewPortHandler().setMaximumScaleY(2f);
        mChart.animateXY(1000, 1000);

        CustomMarkerView mv = new CustomMarkerView(mContext, R.layout.item_line_chart_marker);
        mv.setChartView(mChart);
        mChart.setMarker(mv);

        mChart.invalidate();
        return view;
    }

    private void setLineData() {
        List<ScreenUsage> screenUsageList = Select.from(ScreenUsage.class).list();
        final HashMap<Integer, String> xLabels = new HashMap<>();
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        for (int i = 0; i < screenUsageList.size(); i++) {
            ScreenUsage screenUsage = screenUsageList.get(screenUsageList.size() - 1 - i);
            yVals.add(new Entry(i, screenUsage.getSecondsUsed()));
            xLabels.put(i, screenUsage.getDate());
        }

        LineDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(yVals, "Screen Usage");
            set1.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

            set1.setHighlightEnabled(true);
            set1.setDrawFilled(true);
            set1.setFillColor(Color.rgb(104, 241, 175));
            set1.setDrawCircles(true);
            set1.setLineWidth(1.8f);
            set1.setCircleRadius(4f);
            set1.setCircleColor(Color.WHITE);
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setFillAlpha(100);
            set1.setDrawHorizontalHighlightIndicator(false);

            // create a data object with the datasets
            LineData data = new LineData(set1);
            data.setValueTextSize(9f);
            data.setDrawValues(false);

            XAxis xAxis = mChart.getXAxis();
            xAxis.setDrawGridLines(true);
            xAxis.setEnabled(true);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTextColor(Color.BLACK);
            xAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return xLabels.get((int) value);
                }
            });

            YAxis yAxis = mChart.getAxisLeft();
            yAxis.setLabelCount(6, false);
            yAxis.setTextColor(Color.BLACK);
            yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            yAxis.setDrawGridLines(true);
            yAxis.setAxisLineColor(Color.BLUE);
            yAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return TimeUtil.convertSecondsToApproximateTimeString((long) value);
                }
            });

            mChart.moveViewToX(screenUsageList.size() - 1);
            mChart.setData(data);
        }
    }
}
