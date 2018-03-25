package com.rachitgoyal.screentimer.modules.history.week;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rachitgoyal.screentimer.R;

public class WeekHistoryFragment extends Fragment {

    public WeekHistoryFragment() {
    }

    public static WeekHistoryFragment newInstance() {
        WeekHistoryFragment fragment = new WeekHistoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_week_history, container, false);
    }
}
