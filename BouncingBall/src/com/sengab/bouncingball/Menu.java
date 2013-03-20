package com.sengab.bouncingball;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Menu extends Activity {
	final String TAG = "Menu";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);

		Button play = (Button) findViewById(R.id.start);
		play.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent game = new Intent(Menu.this, Game.class);
				startActivity(game);

			}
		});

	}

}
