package com.example.testprojectdemand;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testprojectdemand.Father.Son;

public class ExpandListAdapter extends BaseExpandableListAdapter {

	private static int ITEM_HEIGHT = 60;
	ArrayList<Father> mDatas = new ArrayList<Father>();

	private Context mContext;
	private LayoutInflater mInflater;
	private int ITEM_SIZE;
	private int MAX_NUMBER = 3;
	private int CONTENT_MIN_HEIGHT;
	private ExpandableListView.LayoutParams mLayoutParams;

	public ExpandListAdapter(Context context) {
		mContext = context;
		mInflater = LayoutInflater.from(context);

		initTestData();
	}

	public ExpandListAdapter(Context context, int listHeight) {
		this(context);
		ITEM_SIZE = mContext.getResources().getDisplayMetrics().widthPixels
				/ MAX_NUMBER;
		CONTENT_MIN_HEIGHT = listHeight - 2 * ITEM_HEIGHT;
		mLayoutParams = new ExpandableListView.LayoutParams(ITEM_SIZE,
				ITEM_SIZE);
	}

	private void initTestData() {
		for (int i = 0; i < 30; i++) {
			Father father = new Father();
			father.name = "father_" + i;
			father.sons = new ArrayList<Son>();
			for (int j = 0; j < i; j++) {
				Son son = new Son();
				son.name = "son_" + j;
				father.sons.add(son);
			}
			mDatas.add(father);
		}
	}

	public void setData(ArrayList<Father> list) {
		mDatas = list;
	}

	@Override
	public int getGroupCount() {
		return mDatas.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		ArrayList<Son> sons = mDatas.get(groupPosition).sons;
		if (sons != null) {
			return 1;
		}
		return 0;
	}

	@Override
	public Father getGroup(int groupPosition) {
		return mDatas.get(groupPosition);
	}

	@Override
	public Son getChild(int groupPosition, int childPosition) {
		return mDatas.get(groupPosition).sons.get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.elistview_group, parent,
					false);
		}
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ExpandGridViewWithLine gridView = new ExpandGridViewWithLine(mContext);
		for (int i = 0; i < getGroup(groupPosition).sons.size(); i++) {
			View view = mInflater.inflate(R.layout.item, null);
			view.setLayoutParams(mLayoutParams);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
				}
			});
			gridView.addView(view, i);
		}
		gridView.setBackgroundColor(0xFFFFCCFF);
		return gridView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

}
