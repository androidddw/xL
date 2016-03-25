package com.example.testunderline;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 
 * 自定义下划线
 * 
 * @author xl
 * @date:2016-3-18下午5:55:46
 */
public class CustomUnderline extends TextView {

	private Paint mPaint_underline;
	// private Paint mPaint_text;
	private FontMetricsInt mFontMetrics;
	private Rect mRect;

	/** 文字和下划线的距离 */
	private int MARGIN = 2;

	public CustomUnderline(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CustomUnderline(Context context, AttributeSet attrs) {
		super(context, attrs);
		// mPaint_text = new Paint();
		// mPaint_text.setColor(Color.BLUE);
		// mPaint_text.setTextAlign(Paint.Align.CENTER);
		//
		// mPaint_text.setTextSize(26);
		// mFontMetrics = mPaint_text.getFontMetricsInt();
		// mPaint_underline = new Paint();
		mPaint_underline = getPaint();
		mPaint_underline.setColor(Color.RED);
		mRect = new Rect();

		getPaint();
	}

	public CustomUnderline(Context context) {
		super(context);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		getPaint().setColor(Color.RED);
		super.onDraw(canvas);
		int baseline = getBaseline();// 原生的方法
		mRect.left = 0;
		mRect.right = getWidth();
		mRect.bottom = getHeight();
		mRect.top = 0;
		// CharSequence text = getText();
		float textWidth = getPaint().measureText(getText().toString());
		// int baseline = (mRect.bottom + mRect.top - mFontMetrics.bottom -
		// mFontMetrics.top) / 2;//自己计算baseline
		// canvas.drawText(text.toString(), mRect.centerX(), baseline,
		// mPaint_text);
		canvas.drawLine(mRect.centerX() - textWidth / 2, baseline + MARGIN,
				mRect.centerX() + textWidth / 2, baseline + MARGIN,
				mPaint_underline);
	}
}
