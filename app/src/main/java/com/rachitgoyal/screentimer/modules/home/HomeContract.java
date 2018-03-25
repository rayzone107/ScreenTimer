package com.rachitgoyal.screentimer.modules.home;

import android.text.SpannableString;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;

/**
 * Created by Rachit Goyal on 15/03/18
 */

public interface HomeContract {
    interface View {
        void setData(PieData data);

        void setCenterText(SpannableString centerText);

        void setAngle(float angle);
    }

    interface Presenter {
        void setupChart(PieChart chart);

        void setData();

        void handleMaxTimeChange();
    }
}
