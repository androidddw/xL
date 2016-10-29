package com.example.test;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ListviewFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.f_listview, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		CustListView listvew = (CustListView) getView().findViewById(
				R.id.listview);
		listvew.setAdapter(new BaseAdapter() {

			@Override
			public View getView(int arg0, View arg1, ViewGroup arg2) {
				if (null == arg1) {
					arg1 = new TextView(arg2.getContext());
				}
				((TextView) arg1).setText(arg0);
				return arg1;
			}

			@Override
			public long getItemId(int arg0) {
				return arg0;
			}

			@Override
			public Integer getItem(int arg0) {
				return arg0;
			}

			@Override
			public int getCount() {
				return 200;
			}
		});
	}
}
