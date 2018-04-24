package com.rachitgoyal.screentimer.modules.history.day;

import android.content.Context;
import android.text.SpannableString;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.PieData;
import com.rachitgoyal.screentimer.model.ScreenUsage;

import java.util.List;

/**
 * Created by Rachit Goyal on 25/03/18
 */

public class DayHistoryContract {
    public interface View {
        void setPieData(PieData data, ScreenUsage screenUsage, DayHistoryPresenter.PieChange isFirstSet);

        void setLeftArrowVisibility(boolean isEnabled);

        void setRightArrowVisibility(boolean isEnabled);

        void setLegend(List<LegendEntry> legendEntries);
    }

    public interface Presenter {
        void setupChart(PieChart chart);

        void fetchTodayData();

        void fetchData(int year, int month, int day);

        SpannableString generateCenterSpannableText(Context context, long usedTime);

        int calculateForEmoticon(ScreenUsage screenUsage);

        long getMinDateForDatePicker();

        void handleLeftClick();

        void handleRightClick();
    }
}
