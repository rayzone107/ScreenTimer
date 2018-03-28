package com.rachitgoyal.screentimer.modules.history.day;

import com.orm.query.Select;
import com.rachitgoyal.screentimer.R;
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

    @Override
    public void updateData() {
        List<ScreenUsage> screenUsageList = Select.from(ScreenUsage.class).orderBy(ScreenUsage.dateField).list();
        mView.updateTodayData(screenUsageList.get(screenUsageList.size() - 1));
    }

    @Override
    public void calculateForEmoticon(ScreenUsage screenUsage) {
        if (screenUsage.getSecondsUsed() < screenUsage.getSecondsAllowed() / 2) {
            mView.setEmoticon(R.drawable.extra_happy);
        } else if (screenUsage.getSecondsUsed() < screenUsage.getSecondsAllowed() - 100) {
            mView.setEmoticon(R.drawable.happy);
        } else if ((screenUsage.getSecondsUsed() > screenUsage.getSecondsAllowed() - 100) &&
                (screenUsage.getSecondsUsed() < screenUsage.getSecondsAllowed() + 100)) {
            mView.setEmoticon(R.drawable.neutral);
        } else if (screenUsage.getSecondsUsed() > screenUsage.getSecondsAllowed() * 2) {
            mView.setEmoticon(R.drawable.extra_sad);
        } else {
            mView.setEmoticon(R.drawable.sad);
        }
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
