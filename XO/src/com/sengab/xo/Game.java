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

public class Game extends Activity implements OnSharedPreferenceChangeListener {

	final String TAG = "Game Activity";

	static SharedPreferences Prefs;
	static int grid_color = 2, X = 3, O = 2, background = 0;
	private GameView view;
	private static boolean mute = false, game_mode = false;

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		Bundle data = getIntent().getExtras();
		game_mode = data.getBoolean("GameMode");
		view = new GameView(this);
		Prefs = PreferenceManager.getDefaultSharedPreferences(this);
		Prefs.registerOnSharedPreferenceChangeListener(this);

		int[] vals = data.getIntArray("data");
		if (vals != null) {
			background = vals[0];
			grid_color = vals[1];
			X = vals[2];
			O = vals[3];
		}
		mute = data.getBoolean("mute");

		setContentView(view);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	// Initiating Menu XML file (menu.xml)
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.NewGameMenu:
			Log.d(TAG, "starting new game");
			view.newGame();
			view.invalidate();
			break;
		case R.id.ClearMenu:
			view.clear_score();
			view.invalidate();
			break;
		case R.id.helpMenu:
			Log.d(TAG, "help is going to open");
			Intent help_screen = new Intent(Game.this, Help.class);
			startActivity(help_screen);
			break;
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
		view.invalidate();
	}

	public static int getGrid_color() {
		return grid_color;
	}

	public static int getBackground() {
		return background;
	}

	public static int getX() {
		return X;
	}

	public static int getO() {
		return O;
	}

	public static void setGrid_color(int grid_color) {
		Game.grid_color = grid_color;
	}

	public static void setX(int x) {
		X = x;
	}

	public static void setO(int o) {
		O = o;
	}

	public static void setBackground(int background) {
		Game.background = background;
	}

	public static boolean isMute() {
		mute = Prefs.getBoolean("Mute", false);
		return mute;
	}

	public void setMute(boolean mute) {
		this.mute = mute;
	}

	public static boolean isGame_mode() {
		return game_mode;
	}

	public static void setGame_mode(boolean game_mode) {
		Game.game_mode = game_mode;
	}

}
