package com.rachitgoyal.screentimer.modules.history.month;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rachitgoyal.screentimer.R;

public class MonthHistoryFragment extends Fragment {

    public MonthHistoryFragment() {
    }

    public static MonthHistoryFragment newInstance() {
        MonthHistoryFragment fragment = new MonthHistoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_month_history, container, false);
    }
}
