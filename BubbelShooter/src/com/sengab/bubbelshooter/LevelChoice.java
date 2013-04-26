package com.sengab.bubbelshooter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LevelChoice extends Activity {

	Button level1, level2, level3;
	final String TAG = LevelChoice.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.levels);

		level1 = (Button) findViewById(R.id.level1);
		level2 = (Button) findViewById(R.id.level2);
		level3 = (Button) findViewById(R.id.level3);

		final Intent intent = new Intent(this, GameActivity.class);

		level1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				intent.putExtra("rowSpeed", 300);
				intent.putExtra("rownum", 4);
				startActivity(intent);

			}
		});

		level2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				intent.putExtra("rowSpeed",200);
				intent.putExtra("rownum", 5);
				startActivity(intent);

			}
		});
		level3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				intent.putExtra("rowSpeed", 150);
				intent.putExtra("rownum", 5);
				startActivity(intent);

			}
		});

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

}
