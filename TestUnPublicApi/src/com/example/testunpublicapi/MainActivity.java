package com.example.testunpublicapi;

import java.lang.reflect.Field;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	private EditText edt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		edt = (EditText) findViewById(R.id.edt);

		try {
			Field field = TextView.class.getDeclaredField("mCursorDrawableRes");
			field.setAccessible(true);
			field.set(edt, R.drawable.ic_launcher);
		} catch (Exception e) {
			new IllegalArgumentException("field不存在!");
		}
	}
}
