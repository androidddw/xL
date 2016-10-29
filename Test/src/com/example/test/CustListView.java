package com.example.test;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnDrawListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;

public class CustListView extends ListView implements OnScrollListener {
	boolean allowDragTop = true; // 如果是true，则允许拖动至底部的下一页
	float downY = 0;
	boolean needConsumeTouch = true; // 是否需要承包touch事件，needConsumeTouch一旦被定性，则不会更改
	private MyPullUpListViewCallBack myPullUpListViewCallBack;
	private Context mContext;
	private int firstVisibleItem;
	private TextView footerView;
	private boolean hasAdd = false;

	public CustListView(Context context) {
		this(context, null);
		this.mContext = context;
		init();
	}

	public CustListView(Context context, AttributeSet arg1) {
		this(context, arg1, 0);
		this.mContext = context;
		init();
	}

	public CustListView(Context context, AttributeSet arg1, int arg2) {
		super(context, arg1, arg2);
		this.mContext = context;
		init();
	}

	private void init() {
		setOnScrollListener(this);
		setFooterDividersEnabled(false);
		initBottomView();
		getViewTreeObserver().addOnDrawListener(new OnDrawListener() {

			@Override
			public void onDraw() {
				if (getLastVisiblePosition() == getCount()) {
					getChildAt(
							getLastVisiblePosition()
									- getFirstVisiblePosition()).setVisibility(
							View.GONE);
				}

			}
		});
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			downY = ev.getRawY();
			needConsumeTouch = true; // 默认情况下，listView内部的滚动优先，默认情况下由该listView去消费touch事件
			allowDragTop = isAtTop();
			// isAtBottom();
		} else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
			if (!needConsumeTouch) {
				// 在最顶端且向上拉了，则这个touch事件交给父类去处理
				getParent().requestDisallowInterceptTouchEvent(false);
				return false;
			} else if (allowDragTop) {
				// needConsumeTouch尚未被定性，此处给其定性
				// 允许拖动到底部的下一页，而且又向上拖动了，就将touch事件交给父view
				if (ev.getRawY() - downY > 2) {
					// flag设置，由父类去消费
					needConsumeTouch = false;
					getParent().requestDisallowInterceptTouchEvent(false);
					return false;
				}
			}
		}
		// 通知父view是否要处理touch事件
		getParent().requestDisallowInterceptTouchEvent(needConsumeTouch);
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * 判断listView是否在顶部
	 * 
	 * @return 是否在顶部
	 */
	private boolean isAtTop() {
		boolean resultValue = false;
		int childNum = getChildCount();
		if (childNum == 0) {
			// 没有child，肯定在顶部
			resultValue = true;
		} else {
			if (getFirstVisiblePosition() == 0) {
				// 根据第一个childView来判定是否在顶部
				View firstView = getChildAt(0);
				if (Math.abs(firstView.getTop() - getTop()) < 2) {
					resultValue = true;
				}
			}
		}

		return resultValue;
	}

	public void initBottomView() {
		if (footerView == null) {
			// footerView = View
			// .inflate(mContext, R.layout.xlistview_footer, null);
			footerView = new TextView(mContext);
			footerView.setText("上拉加载更多");
			footerView.setPadding(0, 30, 0, 30);
			footerView.setGravity(Gravity.CENTER);
			addFooterView(footerView);
		}
	}

	/**
	 * 判断listView是否在底部
	 * 
	 * @return 是否在底部
	 */
	public boolean isAtBottom() {
		boolean result = false;
		int count = getCount();
		int lastVisiblePosition = getLastVisiblePosition();
		if (getLastVisiblePosition() == (getCount() - 1)
				&& lastVisiblePosition != -1) {
			final View bottomChildView = getChildAt(getLastVisiblePosition()
					- getFirstVisiblePosition());
			result = (getHeight() >= bottomChildView.getBottom());
		}
		return result;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
				&& getLastVisiblePosition() == getCount() - 1
				&& getChildAt(
						getLastVisiblePosition() - getFirstVisiblePosition())
						.getBottom() == getBottom()) {
			if (myPullUpListViewCallBack != null) {
				myPullUpListViewCallBack.scrollBottomState();
			}
			if (footerView != null && footerView.getBottom() == getBottom()) {
				footerView.setText("loading");
			}
		}
		if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
			footerView.setText("load_more");
		}
	}

	public void onPullUpComplet() {
		smoothScrollBy(-footerView.getHeight(), 500);
		invalidate();
		hasAdd = false;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		View lastView = null;
		if (footerView != null) {
			// 判断可视Item是否能在当前页面完全显示
			if (visibleItemCount == totalItemCount) {
				if (getChildAt(0) != null && getChildAt(0).getTop() == getTop()) {
					footerView.setVisibility(View.GONE);// 隐藏底部布局
				}
			} else {
				footerView.setVisibility(View.VISIBLE);// 显示底部布局
			}
		}
	}

	public void setMyPullUpListViewCallBack(
			MyPullUpListViewCallBack myPullUpListViewCallBack) {
		this.myPullUpListViewCallBack = myPullUpListViewCallBack;
	}

	/**
	 * 上拉刷新的ListView的回调监听
	 * 
	 * @author xiejinxiong
	 * 
	 */
	public interface MyPullUpListViewCallBack {

		void scrollBottomState();
	}
}
