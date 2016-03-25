package com.nightonke.wowoviewpagerexample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by Weiping on 2016/3/3.
 */
public class SetEaseTypeAdapter extends BaseAdapter {

    private Context mContext;

    public SetEaseTypeAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mContext.getResources().getStringArray(R.array.ease_types).length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_ease_type, null);

        ((TextView)convertView.findViewById(R.id.textview)).setText(
                mContext.getResources().getStringArray(R.array.ease_types)[position]);

        return convertView;
    }
}
