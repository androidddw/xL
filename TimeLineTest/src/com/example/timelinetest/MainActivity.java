package com.example.timelinetest;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Button addButton;
	private Button subButton;
	private UnderLineLinearLayout mUnderLineLinearLayout;

	private View view;

	private boolean isShow;

	private View ll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		addButton = (Button) findViewById(R.id.add);
		view = findViewById(R.id.iv);
		ll = findViewById(R.id.ll);
		findViewById(R.id.ll).setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (isShow) {
					int[] location = new int[2];

					int x = view.getLeft();
					int y = view.getTop();
					if (event.getX() < x
							|| event.getX() > (x + view.getWidth())
							|| event.getY() < y
							|| event.getY() > (y + view.getHeight())) {// 不在范围内
						ll.setVisibility(View.GONE);
						Log.i("XL",
								"x=" + x + " y=" + y + " evenX=" + event.getX()
										+ " evenY" + event.getY());
						isShow = false;
						Toast.makeText(MainActivity.this, "外面", 0).show();
					} else {
						Toast.makeText(MainActivity.this, "里面", 0).show();
					}
					return true;
				}
				return false;
			}

		});
		subButton = (Button) findViewById(R.id.sub);
		mUnderLineLinearLayout = (UnderLineLinearLayout) findViewById(R.id.underline_layout);

		addButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addItem();
				ll.setVisibility(View.VISIBLE);
				isShow = true;
			}
		});
		subButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				subItem();
			}
		});
	}

	int i = 0;

	private void addItem() {
		View v = LayoutInflater.from(this).inflate(R.layout.item_test,
				mUnderLineLinearLayout, false);
		((TextView) v.findViewById(R.id.tx_action))
				.setText("this is test " + i);
		((TextView) v.findViewById(R.id.tx_action_time)).setText("2016-01-21");
		((TextView) v.findViewById(R.id.tx_action_status)).setText("finish");
		mUnderLineLinearLayout.addView(v);
		i++;
	}

	private void subItem() {
		if (mUnderLineLinearLayout.getChildCount() > 0) {
			mUnderLineLinearLayout.removeViews(
					mUnderLineLinearLayout.getChildCount() - 1, 1);
			i--;
		}
	}
}