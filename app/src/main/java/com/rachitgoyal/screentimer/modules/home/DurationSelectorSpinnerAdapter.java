package com.rachitgoyal.screentimer.modules.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rachitgoyal.screentimer.R;
import com.rachitgoyal.screentimer.util.Constants;

/**
 * Created by Rachit Goyal on 16/03/18
 */

public class DurationSelectorSpinnerAdapter extends BaseAdapter {

    private LayoutInflater mLayoutInflater;

    DurationSelectorSpinnerAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return Constants.timeOptions.size();
    }

    @Override
    public Object getItem(int position) {
        return Constants.timeOptions.get(position);
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
            view = mLayoutInflater.inflate(R.layout.item_max_time_list, null);
            viewHolder = new ViewHolder();
            viewHolder.time = view.findViewById(R.id.time_tv);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.time.setText(Constants.timeOptions.get(position));
        return view;
    }

    private static class ViewHolder {
        private TextView time;
    }
}
