package com.example.testprojectdemand;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * 测试项目需求
 * 
 * @author xl
 * @date:2016-3-22下午3:50:12
 */
public class MainActivity extends Activity {

	View tabs[] = new View[3];

	Data datas[] = new Data[3];

	View contents[] = new View[3];

	private int SIZE = 3;

	/** 当前展开的位置 */
	private int currentExpandPosition = NO_EXPAND;
	private final static int NO_EXPAND = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tabs[0] = findViewById(R.id.tab1);
		tabs[0].setOnClickListener(new Click());
		tabs[1] = findViewById(R.id.tab2);
		tabs[1].setOnClickListener(new Click());
		tabs[2] = findViewById(R.id.tab3);
		tabs[2].setOnClickListener(new Click());

		datas[0] = new Data();
		datas[1] = new Data();
		datas[2] = new Data();

		contents[0] = findViewById(R.id.sv1);
		contents[1] = findViewById(R.id.sv2);
		contents[2] = findViewById(R.id.sv3);

	}

	private class Click implements OnClickListener {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.tab1:
				changUi(0);
				startActivity(new Intent(MainActivity.this,
						TestExpandleListview.class));
				break;
			case R.id.tab2:
				changUi(1);
				break;
			case R.id.tab3:
				changUi(2);
				break;

			default:
				break;
			}

		}

		private void changUi(int position) {
			datas[position].isExpand = !datas[position].isExpand;// 改变数据

			boolean currentExpand = datas[position].isExpand;
			contents[position].setVisibility(currentExpand ? View.VISIBLE
					: View.GONE);// 改变绑定数据的UI
			if (!currentExpand) {// 关闭状态
				currentExpandPosition = NO_EXPAND;// 清空展开记录
				for (View tab : tabs) {
					tab.setVisibility(View.VISIBLE);

				}
			} else {// 展开状态
				if (position != SIZE - 1) {// 不是最后一条
					for (int i = 0; i < SIZE; i++) {
						if (!(i == position || i == position + 1)) {// 自己和之后的一个显示,其他隐藏
							tabs[i].setVisibility(View.GONE);
						} else {
							tabs[i].setVisibility(View.VISIBLE);
						}
					}

				} else {// 最后一条
					for (int i = 0; i < SIZE - 1; i++) {
						tabs[i].setVisibility(View.GONE);
					}
				}
				// 处理之前展开的内容区域
				if (currentExpandPosition != position) {
					if (currentExpandPosition != NO_EXPAND) {// 关闭之前的展开对象
						contents[currentExpandPosition]
								.setVisibility(View.GONE);
						tabs[currentExpandPosition].setVisibility(View.GONE);
						datas[currentExpandPosition].isExpand = false;
					}
					currentExpandPosition = position;
				}
			}

		}
	}

	private class Data {
		boolean isExpand;
	}

}
