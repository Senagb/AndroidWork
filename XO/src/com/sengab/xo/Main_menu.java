package com.sengab.xo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

public class Main_menu extends Activity implements
		OnSharedPreferenceChangeListener {

	private Button new_game, exit;
	private final String TAG = "Main_Menu";
	private SharedPreferences Prefs;
	private int grid_color = 2, X = 3, O = 2, background = 0;
	private boolean mute = false;
	private ToggleButton toggle;
	private boolean game_mode = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main_menu);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		new_game = (Button) findViewById(R.id.new_game);
		exit = (Button) findViewById(R.id.exit);
		toggle = (ToggleButton) findViewById(R.id.toggleButton1);
		Log.d(TAG, "Started the game");
		new_game.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Log.d(TAG, "New game clicked");
				Intent game = new Intent(Main_menu.this, Game.class);
				game.putExtra("data",
						new int[] { background, grid_color, X, O });
				game.putExtra("mute", mute);
				game.putExtra("GameMode", game_mode);
				startActivity(game);

			}
		});

		exit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(TAG, "Exit clicked");
				System.exit(0);

			}
		});
		toggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton Cb, boolean isChecked) {
				if (isChecked) {
					game_mode = true;
				} else {
					game_mode = false;
				}
			}
		});
		Prefs = PreferenceManager.getDefaultSharedPreferences(this);
		Prefs.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_settings:
			startActivity(new Intent(this, Prefs.class));
			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		background = Integer.parseInt(Prefs.getString("Background", "0"));
		grid_color = Integer.parseInt(Prefs.getString("line_color ", "2"));
		X = Integer.parseInt(Prefs.getString("X_color", "3"));
		O = Integer.parseInt(Prefs.getString("O_Color", "2"));
		mute = Prefs.getBoolean("Mute", false);
		Log.d(TAG, "prefs");
	}

}
