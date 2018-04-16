package com.rachitgoyal.screentimer.modules.history.day;

import com.rachitgoyal.screentimer.model.ScreenUsage;

import java.util.List;

/**
 * Created by Rachit Goyal on 25/03/18
 */

public class DayHistoryContract {
    public interface View {
        void setData(List<ScreenUsage> screenUsageList);
    }

    public interface Presenter {
        void fetchData();
    }
}
