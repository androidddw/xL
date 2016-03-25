package com.example.testphonestate;

import java.lang.reflect.Field;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Paint;
import android.text.TextPaint;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		TextView tv = (TextView) findViewById(R.id.tv);
		TextView tv2 = (TextView) findViewById(R.id.tv2);
		String handSetInfo = "手机型号:" + android.os.Build.MODEL + ",SDK版本:"
				+ android.os.Build.VERSION.SDK + ",系统版本:"
				+ android.os.Build.VERSION.RELEASE;
		tv.setText(handSetInfo);

		tv2.setTextScaleX(2);
		TextPaint paint = tv2.getPaint();
		paint.setUnderlineText(true);
		String handSetInfo2 = "手机型号:" + android.os.Build.MODEL + "\nSDK版本:"
				+ android.os.Build.VERSION.SDK + "\n系统版本:"
				+ android.os.Build.VERSION.RELEASE + "??"
				+ android.os.Build.BRAND;
		paint.setFlags(Paint.UNDERLINE_TEXT_FLAG);

		try {
			Field field = TextPaint.class.getDeclaredField("underlineColor");// 颜色
			Field field2 = TextPaint.class.getDeclaredField("thickness");// 厚度
			field.setAccessible(true);
			field2.setAccessible(true);
			field.set(paint, 0xFFFF6633);
			field2.set(paint, 2F);
		} catch (Exception e) {

		}
		tv2.setText(handSetInfo2);
	}
}
