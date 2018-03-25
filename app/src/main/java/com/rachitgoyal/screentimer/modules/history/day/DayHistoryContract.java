package com.rachitgoyal.screentimer.modules.history.day;

import android.text.SpannableString;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.PieData;

import java.util.List;

/**
 * Created by Rachit Goyal on 25/03/18
 */

public class DayHistoryContract {
    public interface View {
        void setData(PieData data);

        void setCenterText(SpannableString centerText, String s);

        void setAngle(float angle);

        void setLegend(List<LegendEntry> legendEntries);

        void setLabelColor(int color);
    }

    public interface Presenter {
        void setupChart(PieChart chart);

        void setData();
    }
}
