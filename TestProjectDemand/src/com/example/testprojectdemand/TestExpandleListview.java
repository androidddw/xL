package com.example.testprojectdemand;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;

public class TestExpandleListview extends Activity {

	private int beforeExpand;
	private ExpandableListView mExpandableListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.test);
		mExpandableListView = (ExpandableListView) findViewById(R.id.elv);

		int height = getResources().getDisplayMetrics().heightPixels
				- getStatusHeight(this);// 除去状态栏的屏幕高度
		mExpandableListView.setAdapter(new ExpandListAdapter(this, height));
		mExpandableListView.setGroupIndicator(null);
		mExpandableListView
				.setOnGroupExpandListener(new OnGroupExpandListener() {// 展开监听

					@Override
					public void onGroupExpand(int groupPosition) {

						if (groupPosition != beforeExpand) {
							mExpandableListView.collapseGroup(beforeExpand);// 关闭之前的展开项

						}
						beforeExpand = groupPosition;
						mExpandableListView.setSelection(groupPosition);

					}
				});
	}

	/**
	 * 获得状态栏的高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getStatusHeight(Context context) {

		int statusHeight = -1;
		try {
			Class clazz = Class.forName("com.android.internal.R$dimen");
			Object object = clazz.newInstance();
			int height = Integer.parseInt(clazz.getField("status_bar_height")
					.get(object).toString());
			statusHeight = context.getResources().getDimensionPixelSize(height);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusHeight;
	}
}
