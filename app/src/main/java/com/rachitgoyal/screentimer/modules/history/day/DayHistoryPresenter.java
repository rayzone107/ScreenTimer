package com.rachitgoyal.screentimer.modules.history.day;

import com.orm.query.Select;
import com.rachitgoyal.screentimer.model.ScreenUsage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Created by Rachit Goyal on 25/03/18
 */

public class DayHistoryPresenter implements DayHistoryContract.Presenter {

    private DayHistoryContract.View mView;

    DayHistoryPresenter(DayHistoryContract.View view) {
        mView = view;
    }

    @Override
    public void fetchData() {
        List<ScreenUsage> screenUsageList = Select.from(ScreenUsage.class).orderBy(ScreenUsage.dateField).list();
        Collections.sort(screenUsageList, new StringDateComparator());
        mView.setData(screenUsageList);
    }

    class StringDateComparator implements Comparator<ScreenUsage> {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        @Override
        public int compare(ScreenUsage screenUsage1, ScreenUsage screenUsage2) {
            try {
                return dateFormat.parse(screenUsage1.getDate()).compareTo(dateFormat.parse(screenUsage2.getDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }
}
