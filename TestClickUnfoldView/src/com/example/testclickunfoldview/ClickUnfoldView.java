package com.example.testclickunfoldview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug.HierarchyTraceType;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * 点击展开的控件
 * 
 * @author xl
 * @date:2016-3-8下午3:21:35
 */
public class ClickUnfoldView extends LinearLayout {

	/** 展开的高 */
	int mUnfoldHeight = 0;
	/** 点击的高 */
	int mClickHeight = 0;

	private LinearLayout mUnfoldView;
	private LinearLayout mClickView;
	private View mClose;

	/** 展开状态 */
	private boolean isExpand;
	/** 展开 */
	private Animation mAnimation_down;
	/** 收起 */
	private Animation mAnimation_up;

	public ClickUnfoldView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (this.mUnfoldHeight == 0) {
			this.mUnfoldView.measure(widthMeasureSpec, 0);
			this.mUnfoldHeight = this.mUnfoldView.getMeasuredHeight();
		}
		if (this.mClickHeight == 0) {
			this.mClickView.measure(widthMeasureSpec, 0);
			this.mClickHeight = this.mClickView.getMeasuredHeight();
		}
		Log.i("XL", "mUnfoldHeight" + mUnfoldHeight + "mClickHeight"
				+ mClickHeight);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * 加载完布局映射后调用
	 */
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		this.mClickView = (LinearLayout) findViewById(R.id.click);
		this.mUnfoldView = (LinearLayout) findViewById(R.id.unfold);
		this.mClose = findViewById(R.id.close);
		this.mClickView.setOnClickListener(new ExpandListener());
		this.mClose.setOnClickListener(new ExpandListener());
		mUnfoldView.setVisibility(View.GONE);

	}

	private class ExpandListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			clearAnimation();
			switch (v.getId()) {
			case R.id.click:
				Toast.makeText(getContext(), "click", 0).show();
				if (!isExpand) {
					if (mAnimation_down == null) {
						mAnimation_down = new ExpandAnimation(mUnfoldView,
								mUnfoldHeight, true);
						mAnimation_down.setDuration(200);// 测试一秒
					}
					startAnimation(mAnimation_down);
					mUnfoldView.startAnimation(AnimationUtils.loadAnimation(
							getContext(), R.anim.animalpha));
					isExpand = true;
					mClickView.setVisibility(View.GONE);
				} else {
					isExpand = false;
					if (mAnimation_up == null) {
						mAnimation_up = new ExpandAnimation(mUnfoldView,
								mUnfoldHeight, false);
						mAnimation_up.setDuration(200);
					}
					startAnimation(mAnimation_up);
					mClickView.setVisibility(View.VISIBLE);
				}

				break;
			case R.id.close:
				Toast.makeText(getContext(), "close", 0).show();
				if (!isExpand) {
					if (mAnimation_down == null) {
						mAnimation_down = new ExpandAnimation(mUnfoldView,
								mUnfoldHeight, true);
						mAnimation_down.setDuration(200);// 测试一秒
					}
					startAnimation(mAnimation_down);
					mUnfoldView.startAnimation(AnimationUtils.loadAnimation(
							getContext(), R.anim.animalpha));
					isExpand = true;
					mClickView.setVisibility(View.GONE);
				} else {
					isExpand = false;
					if (mAnimation_up == null) {
						mAnimation_up = new ExpandAnimation(mUnfoldView,
								mUnfoldHeight, false);
						mAnimation_up.setDuration(200);
					}
					startAnimation(mAnimation_up);
					mClickView.setVisibility(View.VISIBLE);
				}

				break;

			default:
				break;
			}

		}

	}

	private class ExpandAnimation extends Animation {

		private View mView;
		private int mHeight;
		private boolean mExpand;

		/**
		 * 动画的构造方法
		 * 
		 * @param view
		 *            对象
		 * @param height
		 *            高度
		 * @param isExpand
		 *            是否展开
		 */
		public ExpandAnimation(View view, int height, boolean isExpand) {
			mView = view;
			mHeight = height;
			mExpand = isExpand;
		}

		@Override
		protected void applyTransformation(float interpolatedTime,
				Transformation t) {

			int currentHeight;
			if (mExpand) {
				currentHeight = (int) (mHeight * interpolatedTime);
			} else {
				currentHeight = (int) (mHeight * (1 - interpolatedTime));
			}
			mView.getLayoutParams().height = currentHeight;
			mView.requestLayout();
			if (mView.getVisibility() == View.GONE) {
				mView.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void initialize(int width, int height, int parentWidth,
				int parentHeight) {
			super.initialize(width, height, parentWidth, parentHeight);
		}

		@Override
		public boolean willChangeBounds() {
			return true;
		}
	}
}
