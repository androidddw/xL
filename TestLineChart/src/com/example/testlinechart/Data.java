package com.example.testlinechart;

import java.util.Comparator;

/**
 * 数据类
 * 
 * @author xl date:2015-12-28下午5:26:46
 */
public class Data implements Comparable<Data> {

	public float x;
	public float y;

	public Data(float x, float y) {
		super();
		this.x = x;
		this.y = y;
	}

	/**
	 * 以x为基准作比较
	 */
	@Override
	public int compareTo(Data arg0) {
		return Float.compare(this.x, arg0.x);
	}

	@Override
	public String toString() {
		return "Data [x=" + x + ", y=" + y + "]";
	}

}
