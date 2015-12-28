package com.example.testlinechart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class LineChartView extends View {

	/** X原点 */
	private int mXPoint;
	/** Y原点 */
	private int mYPoint;
	/** X轴长 */
	private int mXLength;
	/** Y轴长 */
	private int mYLength = 200;

	/** X轴刻度数 */
	private int mXScale = 10;
	/** Y轴刻度数 */
	private int mYScale = 12;

	/** X轴剩余 */
	private float REMAIN_X = 20;
	/** Y轴剩余 */
	private float REMAIN_Y = 20;
	private Paint mPaint_line;

	private Paint mPaint_txt;

	/** X轴信息 */
	private String mXInfo;
	/** Y轴信息 */
	private String mYInfo;

	/** 设置网格 */
	private boolean mGrididngLine;

	/** 箭头宽 */
	private float ARROWS_WIDTH = 5;
	/** 箭头长 */
	private float ARROWS_HEIGHT = 10;
	/** 刻度的长 */
	private float SCALE_LENGTH = 5;
	/** 数据项的圆点半径 */
	private float ITEM_CICLE_RADIUS = 3;
	private static ArrayList<Data> mDatas;
	static {
		initData();
	}

	private static void initData() {
		mDatas = new ArrayList<Data>();
		mDatas.add(new Data(0.5f, 5f));
		mDatas.add(new Data(5.5f, 25f));
		mDatas.add(new Data(2.5f, 45f));
		mDatas.add(new Data(6.5f, 55f));
		mDatas.add(new Data(1.5f, 35f));
		mDatas.add(new Data(7.5f, 30f));
		mDatas.add(new Data(1f, 15f));
		mDatas.add(new Data(8f, 52f));
		mDatas.add(new Data(9f, 23f));
		mDatas.add(new Data(2f, 16f));
		mDatas.add(new Data(3f, 7f));
		Collections.sort(mDatas);// 排序
		Log.i("TAG", mDatas.toString());
	}

	public LineChartView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public LineChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray arrays = context.obtainStyledAttributes(attrs,
				R.styleable.LineChartView);
		final int count = arrays.getIndexCount();
		for (int i = 0; i < count; i++) {
			int attr = arrays.getIndex(i);
			switch (attr) {
			case R.styleable.LineChartView_gridding_line:
				mGrididngLine = arrays.getBoolean(attr, false);
				break;

			default:
				break;
			}
		}
		arrays.recycle();
		initPaints();
	}

	/**
	 * 初始化画笔
	 * 
	 * @author xl
	 * @date:2015-12-28下午3:09:01
	 * @description
	 * @return
	 */
	private void initPaints() {
		// 线的画笔
		mPaint_line = new Paint();
		mPaint_line.setFlags(Paint.ANTI_ALIAS_FLAG);
		mPaint_line.setColor(0xFFFF6600);
		// 线宽
		mPaint_line.setStrokeWidth(com.easemob.util.DensityUtil.dip2px(
				getContext(), 1));
		// 文本画笔
		mPaint_txt = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint_txt.setColor(0xFF006699);
		// 文本对齐方式
		mPaint_txt.setTextAlign(Paint.Align.CENTER);
	}

	public LineChartView(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if (heightMode == MeasureSpec.EXACTLY) {// 指定了高
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			return;
		}
		int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
		int actualHeight = getPaddingBottom() + getPaddingTop()
				+ com.easemob.util.DensityUtil.dip2px(getContext(), mYLength);
		setMeasuredDimension(measuredWidth, actualHeight);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 测量工具
		FontMetrics fm = mPaint_txt.getFontMetrics();
		mXLength = getWidth() - getPaddingLeft() - getPaddingRight();
		mYLength = getHeight() - getPaddingBottom() - getPaddingTop();
		// X轴
		canvas.drawLine(getPaddingLeft(), getHeight() - getPaddingBottom(),
				getWidth() - getPaddingRight(), getHeight()
						- getPaddingBottom(), mPaint_line);
		// X轴箭头
		canvas.drawLine(getWidth() - getPaddingRight() - ARROWS_HEIGHT,
				getHeight() - getPaddingBottom() - ARROWS_WIDTH, getWidth()
						- getPaddingRight(), getHeight() - getPaddingBottom(),
				mPaint_line);
		canvas.drawLine(getWidth() - getPaddingRight() - ARROWS_HEIGHT,
				getHeight() - getPaddingBottom() + ARROWS_WIDTH, getWidth()
						- getPaddingRight(), getHeight() - getPaddingBottom(),
				mPaint_line);

		// X轴刻度线总长
		float xRealLength = mXLength - REMAIN_X;
		// 项宽
		float xItemWidth = xRealLength / mXScale;
		// X轴刻度
		for (int i = 1; i <= mXScale; i++) {
			// 刻度线
			canvas.drawLine(getPaddingLeft() + xItemWidth * i, getHeight()
					- getPaddingBottom() + SCALE_LENGTH, getPaddingLeft()
					+ xItemWidth * i, getHeight() - getPaddingBottom(),
					mPaint_line);

			// 刻度信息
			canvas.drawText(i + "", getPaddingLeft() + xItemWidth * i,
					getHeight() - getPaddingBottom() + 20, mPaint_txt);
		}
		// Y轴
		canvas.drawLine(getPaddingLeft(), getHeight() - getPaddingBottom(),
				getPaddingLeft(), getPaddingTop(), mPaint_line);
		// Y轴箭头
		canvas.drawLine(getPaddingLeft() - ARROWS_WIDTH, getPaddingTop()
				+ ARROWS_HEIGHT, getPaddingLeft(), getPaddingTop(), mPaint_line);
		canvas.drawLine(getPaddingLeft() + ARROWS_WIDTH, getPaddingTop()
				+ ARROWS_HEIGHT, getPaddingLeft(), getPaddingTop(), mPaint_line);
		// Y轴刻度线总长
		float yRealLength = mYLength - REMAIN_Y;
		// 项宽
		float yItemWidth = yRealLength / mYScale;
		// Y轴刻度
		for (int i = 1; i <= mYScale; i++) {
			canvas.drawLine(getPaddingLeft(), getHeight() - getPaddingBottom()
					- i * yItemWidth, getPaddingLeft() - SCALE_LENGTH,
					getHeight() - getPaddingBottom() - i * yItemWidth,
					mPaint_line);

			// 处理baseline的居中
			float y = getHeight() - getPaddingBottom() - i * yItemWidth
					- fm.descent + (fm.descent - fm.ascent) / 2;
			// 刻度信息
			canvas.drawText(i * 5 + "", getPaddingLeft() / 2, y, mPaint_txt);
		}

		if (mGrididngLine) {// 带网格(不在之前的循环里面做避免多次判断和多次重置画笔的颜色)
			mPaint_line.setStrokeWidth(com.easemob.util.DensityUtil.dip2px(
					getContext(), 0.5F));
			for (int i = 0; i <= mXScale; i++) {// 垂直线
				float x = getPaddingLeft() + i * xItemWidth;
				canvas.drawLine(x, getHeight() - getPaddingBottom(), x,
						getPaddingTop(), mPaint_line);
			}
			for (int i = 0; i <= mYScale; i++) {// 水平线
				float y = getHeight() - getPaddingBottom() - i * yItemWidth;
				canvas.drawLine(getPaddingLeft(), y, getWidth()
						- getPaddingRight(), y, mPaint_line);
			}
		}

		// 黑色
		mPaint_line.setColor(0xFF000000);
		if (mDatas != null) { // 绘制点线
			int n = mDatas.size();
			for (int i = 0; i < n - 1; i++) {
				Data before = mDatas.get(i);
				Data latter = mDatas.get(i + 1);

				// 计算数据的位置
				float beforeX = before.x * xItemWidth + getPaddingLeft();
				float beforeY = getHeight() - before.y * yItemWidth / 5
						- getPaddingBottom();
				float latterX = latter.x * xItemWidth + getPaddingLeft();
				float latterY = getHeight() - latter.y * yItemWidth / 5
						- getPaddingBottom();

				canvas.drawCircle(beforeX, beforeY, ITEM_CICLE_RADIUS,
						mPaint_line);// 前一个圆点
				canvas.drawLine(beforeX, beforeY, latterX, latterY, mPaint_line);// 连线
				if (i == n - 2) { // 最后一个圆点
					canvas.drawCircle(latterX, latterY, ITEM_CICLE_RADIUS,
							mPaint_line);
				}
			}
		}
	}
}
