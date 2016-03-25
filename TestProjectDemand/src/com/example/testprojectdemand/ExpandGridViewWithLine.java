package com.example.testprojectdemand;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

/**
 * 不到滚动条 直接显示所有条目
 * 
 * @author xl
 * 
 */
public class ExpandGridViewWithLine extends ViewGroup {

	private int mMinHeight;

	private int mItemSize;

	private int DEFAULT_SPACING = 2;

	private int MAX_NUMBER = 3;

	private Paint mPaint_line;

	private int childCount;

	private int COLOR_LINE = 0xFF999999;

	public ExpandGridViewWithLine(Context context) {
		super(context);
		int screenWirth = getResources().getDisplayMetrics().widthPixels;// 屏幕宽
		mItemSize = (screenWirth) / MAX_NUMBER;
		mPaint_line = new Paint();
		mPaint_line.setColor(COLOR_LINE);
		mPaint_line.setStyle(Paint.Style.STROKE);
		mPaint_line.setStrokeWidth(1);// 线宽
	}

	public ExpandGridViewWithLine(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ExpandGridViewWithLine(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setChildCount(int count) {
		this.childCount = count;
	}

	@Override
	public void setMinimumHeight(int minHeight) {
		mMinHeight = minHeight;
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		measureChildren(widthMeasureSpec, heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int count = getChildCount();
		int line = (count % MAX_NUMBER == 0 ? 0 : 1) + count / MAX_NUMBER;
		setMeasuredDimension(width, line * mItemSize);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int count = getChildCount();
		int x = 0;// 横坐标开始
		int y = 0;// 纵坐标开始

		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);

			child.layout(x, y, x + mItemSize, y + mItemSize);
			if ((i + 1) % 3 == 0) {// 换行
				x = 0;
				y += mItemSize;
			} else {
				x += mItemSize;
			}
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		int height = getHeight();
		int width = getWidth();
		int column = 3;// 列数
		int childCount = getChildCount();// 子控件数目
		if (childCount > 0) {
			int surplus = childCount % column;// 最后一行的个数
			int line = (surplus == 0 ? 0 : 1) + childCount / column;
			// 画横线
			for (int i = 1; i < line; i++) {
				int y = i * mItemSize;
				canvas.drawLine(0, y, width, y, mPaint_line);
			}
			// 画纵线
			for (int i = 1; i < column; i++) {
				int x = i * mItemSize;
				if (surplus == 0 || i <= surplus) {// 最后一列有三个或者为余项
					canvas.drawLine(x, 0, x, height, mPaint_line);
				} else {
					canvas.drawLine(x, 0, x, height - mItemSize, mPaint_line);
				}
			}
		}
	}

}
