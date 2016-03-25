package com.example.testprojectdemand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

/**
 * 自定义基类适配器
 * 
 * @author xl
 * 
 * @param <T>
 */
public abstract class SuperBaseAdapter<T> extends BaseAdapter {

	private final ArrayList<T> mList = new ArrayList<T>();
	protected LayoutInflater mInflater;
	protected Context mContext;

	private SparseIntArray positionOfSection;
	private SparseIntArray sectionOfPosition;
	private List<String> list;

	public SuperBaseAdapter(Context ctx) {
		mInflater = LayoutInflater.from(ctx);
		mContext = ctx;

	}

	public void setData(List<T> list) {
		mList.clear();
		if (list != null) {
			mList.addAll(list);
		}
		notifyDataSetChanged();
	}

	public void addData(List<T> list) {
		if (list != null) {
			mList.addAll(list);
		}
		notifyDataSetChanged();
	}

	public ArrayList<T> getData() {
		return mList;
	}

	@Override
	public boolean isEmpty() { // PullToReresh框架的下拉刷新是通过该方法进行相关处理,所有测试代码中写假数据的时候是通过getCount,而改方法回调的还是empty,所以此处做修改
		return mList.isEmpty() && getCount() == 0;
	}

	public void removeItem(int position) {
		if (mList.get(position) != null) {
			mList.remove(position);
			notifyDataSetChanged();
		}
	}

	public void addItem(T t) {
		mList.add(t);
		notifyDataSetChanged();
	}

	public void modifyItem(int position, T t) {
		mList.set(position, t);
		notifyDataSetChanged();
	}

	public void removeItem(T t) {
		if (mList.contains(t)) {
			mList.remove(t);
			notifyDataSetChanged();
		}
	}

	public void clear() {
		mList.clear();
		notifyDataSetChanged();
	}

	public void sort(Comparator<T> c) {
		Collections.sort(mList, c);
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public T getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

}
