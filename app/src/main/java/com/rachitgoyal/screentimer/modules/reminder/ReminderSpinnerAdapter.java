package com.rachitgoyal.screentimer.modules.reminder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rachitgoyal.screentimer.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rachit Goyal on 19/03/18
 */

public class ReminderSpinnerAdapter extends BaseAdapter {

    private LayoutInflater mLayoutInflater;
    private List<String> mDataList = new ArrayList<>();

    ReminderSpinnerAdapter(Context context, List<String> dataList) {
        mLayoutInflater = LayoutInflater.from(context);
        mDataList = dataList;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.add_reminder_frequency_spinner_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.item_tv = view.findViewById(R.id.frequency_spinner_item_tv);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.item_tv.setText(mDataList.get(position));
        return view;
    }

    private static class ViewHolder {
        private TextView item_tv;
    }
}
