package com.example.testupdateservice;

import org.xutils.x;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		x.Ext.init(getApplication());
		Intent intent = new Intent(this, ZBYUpdateService.class);
		startService(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

}
