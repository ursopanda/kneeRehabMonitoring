package com.careconnectpatient;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import database.RehabSession;

/**
 * Created by austris on 25/05/2016.
 */
public class HistoryListAdapter extends BaseAdapter{

    private Context mContext;
    private List<RehabSession> mHistoryList;

    public HistoryListAdapter(Context mContext, List<RehabSession> mHistoryList) {
        this.mContext = mContext;
        this.mHistoryList = mHistoryList;
    }

    @Override
    public int getCount() {
        return mHistoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return mHistoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.history_list_item, null);
        TextView date = (TextView) view.findViewById(R.id.rehab_history_date);
        TextView duration = (TextView) view.findViewById(R.id.rehab_history_duration);
        TextView exceeded = (TextView) view.findViewById(R.id.rehab_history_exceeded);
        TextView comment = (TextView) view.findViewById(R.id.rehab_history_comment);

        //Set text for TextView
        date.setText(mHistoryList.get(position).getDate());
        duration.setText(mHistoryList.get(position).getDuration());
        exceeded.setText(String.valueOf(mHistoryList.get(position).getExceeded_max()));
        comment.setText(mHistoryList.get(position).getComment());

        return view;
    }
}
