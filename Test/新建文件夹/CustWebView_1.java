package news.onon.yaowen.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

public class CustWebView_1 extends WebView {
	boolean allowDragBottom = true; // 如果是true，则允许拖动至底部的下一页
	float downY = 0;
	boolean needConsumeTouch = true; // 是否需要承包touch事件，needConsumeTouch一旦被定性，则不会更改
	int maxScroll = -1; // 最大滑动距离
	private int lastTop;

	public CustWebView_1(Context arg0) {
		this(arg0, null);
	}

	public CustWebView_1(Context arg0, AttributeSet arg1) {
		this(arg0, arg1, 0);
	}

	public CustWebView_1(Context arg0, AttributeSet arg1, int arg2) {
		super(arg0, arg1, arg2);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			downY = ev.getRawY();
			needConsumeTouch = true; // 默认情况下，scrollView内部的滚动优先，默认情况下由该ScrollView去消费touch事件
			if (maxScroll > 0 && isAtBottom()) {
				// 允许向上拖动底部的下一页
				allowDragBottom = true;
			} else {
				// 不允许向上拖动底部的下一页
				allowDragBottom = false;
			}
		} else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
			if (!needConsumeTouch) {
				// 在最顶端且向上拉了，则这个touch事件交给父类去处理
				getParent().requestDisallowInterceptTouchEvent(false);
				return false;
			} else if (allowDragBottom) {
				// needConsumeTouch尚未被定性，此处给其定性
				// 允许拖动到底部的下一页，而且又向上拖动了，就将touch事件交给父view
				if (downY - ev.getRawY() > 2) {
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

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		if (maxScroll < 0) {
			maxScroll = computeVerticalScrollRange();
		}
		lastTop = t;
		super.onScrollChanged(l, t, oldl, oldt);
	}

	public int getLastPostion() {
		return lastTop;
	}

	public boolean isAtBottom() {
		float webcontent = getContentHeight() * getScale();// webview的高度
		float webnow = getHeight() + getScrollY();// 当前webview的高度
		if (Math.abs(webcontent - webnow) < 5) {
			return true;
		}
		return false;
	}

}
