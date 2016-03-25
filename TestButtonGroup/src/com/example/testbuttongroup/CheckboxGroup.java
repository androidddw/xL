package com.example.testbuttongroup;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class CheckboxGroup extends View {

	private Paint mPaint;

	private Paint mPaint_bg;
	private int KIND_HEIGHT = 40;
	private int VALUE_HEIGHT = 30;
	private int TEXT_PADDING = 10;

	private int VALUE_MARGIN = 10;
	private RectF mRectF;
	private int RADIUS = 10;
	/** 度量工具 */
	private FontMetricsInt mFontMetricsInt;
	String[] mKinds = new String[] { "法师", "战士", "术士", "医师", "狗屎", "牛屎", "猪屎",
			"猫屎", "红猫", "蓝猫" };
	String[] mValues = new String[] { "法师艾德阿瓦打我1", "战士阿瓦打我打我2", "术大队委爱的士3",
			"医师阿达瓦达瓦达瓦大4", "狗阿达哇打我打我屎5", "牛屎6", "猪屎阿瓦打我打我单位7", "猫屎8",
			"红阿瓦打我打我打我打我打我猫9", "蓝猫阿瓦达瓦大文档我10", "法师11", "战士阿瓦娃娃的12", "术士13",
			"医师14", "狗阿瓦打我打我打我的屎15", "牛屎16", "阿瓦达瓦大猪屎17", "啊啊啊啊啊18",
			"法师艾德阿瓦打我1", "战士阿瓦打我打我2", "术大队委爱的士3", "医师阿达瓦达瓦达瓦大4", "狗阿达哇打我打我屎5",
			"牛屎6", "猪屎阿瓦打我打我单位7", "猫屎8", "红阿瓦打我打我打我打我打我猫9", "蓝猫阿瓦达瓦大文档我10",
			"法师11", "战士阿瓦娃娃的12", "术士13", "医师14", "狗阿瓦打我打我打我的屎15", "牛屎16",
			"阿瓦达瓦大猪屎17", "啊啊啊啊啊18" };

	public CheckboxGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		KIND_HEIGHT = DensityUtil.dip2px(context, KIND_HEIGHT);
		VALUE_HEIGHT = DensityUtil.dip2px(context, VALUE_HEIGHT);
		TEXT_PADDING = DensityUtil.dip2px(context, TEXT_PADDING);
		VALUE_MARGIN = DensityUtil.dip2px(context, VALUE_MARGIN);
		mPaint = new Paint();
		mPaint.setColor(0xFFFFCCFF);
		mPaint.setTextSize(34);
		mFontMetricsInt = mPaint.getFontMetricsInt();
		mPaint.setTextAlign(Paint.Align.CENTER);

		mPaint_bg = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint_bg.setColor(Color.BLUE);

		mRectF = new RectF();
	}

	private float mX_down = 0;
	private float mY_down = 0;

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mX_down = event.getX();
			mY_down = event.getY();
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_CANCEL:
			break;
		default:
			break;
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int specWidth = MeasureSpec.getSize(widthMeasureSpec);
		int actualWidth = specWidth - getPaddingLeft() - getPaddingRight();
		int actualHeight = 0;
		int size_k = mKinds.length;// 子类型数
		actualHeight = size_k * KIND_HEIGHT;// 所有的子类型占高

		int value_rows = 0;// 属性的行数
		for (int i = 0; i < size_k; i++) {

			int width = 0;// 条目所占宽
			int size_v = mValues.length;// 子类的的条目数
			if (size_v > 0) {
				value_rows++;
			}
			for (int j = 0; j < size_v; j++) {
				width += (int) mPaint.measureText(mValues[j]) + 2
						* TEXT_PADDING + VALUE_MARGIN;
				if (width > actualWidth) { // 占宽超过一行
					value_rows++;
					width = 0;// 当前占款置0
				}
			}
		}
		actualHeight += value_rows * VALUE_HEIGHT;
		setMeasuredDimension(specWidth, actualHeight);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int width = getWidth() - getPaddingLeft() - getPaddingRight();
		int size_k = mKinds.length;

		int PADDINGS = 2 * TEXT_PADDING;
		int y = 0;
		int x;
		for (int i = 0; i < size_k; i++) {
			x = getPaddingLeft();

			mRectF.left = x;
			mRectF.top = y;
			mRectF.right = x + mPaint.measureText(mKinds[i]) + PADDINGS;
			mRectF.bottom = y + KIND_HEIGHT;
			mPaint_bg.setColor(0xFFFF6600);
			canvas.drawRoundRect(mRectF, RADIUS, RADIUS, mPaint_bg);// 绘制类型背景框
			int baseline_k = (int) ((mRectF.bottom + mRectF.top
					- mFontMetricsInt.bottom - mFontMetricsInt.top) / 2);
			canvas.drawText(mKinds[i], mRectF.centerX(), baseline_k, mPaint);// 居中绘制文本
			y += KIND_HEIGHT;
			int size_v = mValues.length;
			for (int j = 0; j < size_v; j++) {
				int right = (int) (x + mPaint.measureText(mValues[j]) + PADDINGS);
				if (right > width) {// 换行
					y += VALUE_HEIGHT;
					x = getPaddingLeft();
					Log.i("XL", x + "");
				}
				mRectF.left = x;
				mRectF.top = y;
				mRectF.right = x + mPaint.measureText(mValues[j]) + PADDINGS;
				mRectF.bottom = y + VALUE_HEIGHT;
				if (mX_down >= mRectF.left && mX_down <= mRectF.right
						&& mY_down >= mRectF.top && mY_down <= mRectF.bottom) {
					mPaint_bg.setColor(Color.RED);

				} else {
					mPaint_bg.setColor(Color.BLUE);
				}
				canvas.drawRoundRect(mRectF, RADIUS, RADIUS, mPaint_bg);
				// 绘制条目背景框
				int baseline_v = (int) ((mRectF.bottom + mRectF.top
						- mFontMetricsInt.bottom - mFontMetricsInt.top) / 2);
				canvas.drawText(mValues[j], mRectF.centerX(), baseline_v,
						mPaint);// 居中绘制文本
				x += (int) (mRectF.right + VALUE_MARGIN);
			}
			// 换行,绘制类型
			y += VALUE_HEIGHT;
		}
	}
}
