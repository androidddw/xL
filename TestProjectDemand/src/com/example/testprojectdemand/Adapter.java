package com.example.testprojectdemand;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.testprojectdemand.Father.Son;

public class Adapter extends SuperBaseAdapter<Son> {

	private int ITEM_SIZE;
	private int MAX_NUMBER = 3;
	private GridView.LayoutParams mLayoutParams;

	public Adapter(Context ctx) {
		super(ctx);
		ITEM_SIZE = mContext.getResources().getDisplayMetrics().widthPixels
				/ MAX_NUMBER;
		mLayoutParams = new GridView.LayoutParams(ITEM_SIZE, ITEM_SIZE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item, parent, false);
			convertView.setLayoutParams(mLayoutParams);
			Log.i("XL", ITEM_SIZE + "");
		}
		return convertView;
	}
}
